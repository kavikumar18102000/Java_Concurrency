# Java Concurrency & Multithreading

## Lesson 4 --- Thread Lifecycle and States

> Objective: Understand Java's six official thread states, inspect them,
> and reason about lock and waiting behaviour.

## 1. The six official states

  State             Meaning
  ----------------- --------------------------------------------------
  `NEW`             Thread object created; `start()` not called
  `RUNNABLE`        Eligible to run or currently executing
  `BLOCKED`         Waiting to acquire an object's monitor lock
  `WAITING`         Waiting indefinitely for another thread's action
  `TIMED_WAITING`   Waiting for a specified duration
  `TERMINATED`      Thread execution has completed

Java does **not** define a separate `RUNNING` state. A thread actively
executing is reported as `RUNNABLE`.

## 2. Simplified lifecycle

``` text
NEW --start()--> RUNNABLE
                   |
       +-----------+------------+
       |           |            |
  lock busy     wait/join    sleep/timed wait
       |           |            |
    BLOCKED      WAITING   TIMED_WAITING
       |           |            |
  lock acquired  notified/   timeout/event
       |         completed      |
       +-----------+------------+
                   |
                RUNNABLE
                   |
             run() completes
                   |
              TERMINATED
```

A thread may move between the middle states multiple times before
termination.

## 3. Each state with examples

### NEW

A Thread object is `NEW` after construction and before `start()`.

``` java
Thread worker = new Thread(() -> System.out.println("Working"));
System.out.println(worker.getState()); // NEW
```

Creating a Thread object does not start execution. Calling `run()`
directly is an ordinary method call on the caller thread.

### RUNNABLE

After `start()`, the thread becomes eligible for execution. It may be
waiting for CPU time or actively executing.

``` java
Thread worker = new Thread(() -> System.out.println("Working"));
worker.start();
System.out.println(worker.getState()); // Timing-dependent
```

The state may be `RUNNABLE`, or the worker may already have completed
and be `TERMINATED`.

### BLOCKED

A thread is `BLOCKED` when it tries to enter a `synchronized` block or
method but another thread owns that object's monitor.

``` java
Object lock = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lock) {
        try {
            Thread.sleep(5000); // Still holds lock while sleeping
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lock) {
        System.out.println("Acquired lock");
    }
});

t1.start();
Thread.sleep(500); // Give t1 time to acquire lock
t2.start();
Thread.sleep(500); // Give t2 time to attempt acquisition
System.out.println(t2.getState()); // Expected: BLOCKED
```

Thread-1 acquires the monitor and sleeps while holding it. Thread-2
tries to enter the same synchronized block and waits for the monitor.
`sleep()` does **not** release an owned monitor.

### WAITING

A thread enters `WAITING` when it waits indefinitely for another
thread's action.

Common causes: - `Object.wait()` --- until notified or interrupted. -
Untimed `Thread.join()` --- until the target thread terminates. -
`LockSupport.park()` --- until unparked or interrupted.

Example using `join()`:

``` java
Thread worker = new Thread(() -> {
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

worker.start();
Thread mainThread = Thread.currentThread();

Thread observer = new Thread(() ->
    System.out.println(mainThread.getState())
);

observer.start();
observer.join();
worker.join(); // Main waits here; main is WAITING
```

### TIMED_WAITING

A thread enters `TIMED_WAITING` when it waits for a specified duration.

  Method                      Example
  --------------------------- -----------------------------------------
  `Thread.sleep()`            `Thread.sleep(2000)`
  `Object.wait()`             `lock.wait(2000)`
  `Thread.join()`             `thread.join(2000)`
  `LockSupport.parkNanos()`   `LockSupport.parkNanos(2_000_000_000L)`

``` java
Thread worker = new Thread(() -> {
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

worker.start();
Thread.sleep(500);
System.out.println(worker.getState()); // Expected: TIMED_WAITING
```

### TERMINATED

A thread is `TERMINATED` when its `run()` method finishes, normally or
due to an uncaught exception.

``` java
Thread worker = new Thread(() -> System.out.println("Done"));
worker.start();
worker.join();
System.out.println(worker.getState()); // TERMINATED
```

A Thread instance can be started only once. Calling `start()` again
throws `IllegalThreadStateException`.

## 4. Why `Thread.sleep(500)` was used in Exercises 2 and 3

Calling `start()` does not guarantee that the new thread executes
immediately. The scheduler decides when it receives CPU time.

**Exercise 2:**

``` java
workerThread.start();
Thread.sleep(500);
System.out.println(workerThread.getState());
```

The worker sleeps for five seconds. The main thread sleeps for half a
second to give the worker an opportunity to enter its five-second sleep.
Expected observation: `TIMED_WAITING`.

**Exercise 3:**

``` java
t1.start();
Thread.sleep(500); // Give t1 time to acquire lock
t2.start();
Thread.sleep(500); // Give t2 time to try to acquire lock
System.out.println(t2.getState());
```

-   First delay: gives Thread-1 time to acquire the monitor.
-   Second delay: gives Thread-2 time to attempt acquisition and become
    `BLOCKED`.

**Important:** 500 ms is a convenient observation delay, not a
guarantee. A busy system may not schedule a thread within that time. For
reliable coordination, prefer `CountDownLatch`, `Semaphore`, or other
concurrency utilities instead of arbitrary sleeps.

## 5. BLOCKED vs WAITING vs TIMED_WAITING

  -----------------------------------------------------------------------
  Feature           BLOCKED           WAITING           TIMED_WAITING
  ----------------- ----------------- ----------------- -----------------
  Reason            Waiting for       Waiting           Waiting for a
                    monitor lock      indefinitely for  duration
                                      an action         

  Duration          Until lock is     Indefinite        Specified
                    available                           duration

  Examples          Entering          Untimed `join()`, `sleep()`, timed
                    synchronized      `wait()`          `join()`
                    block                               

  Resumes when      Lock acquired     Notification,     Timeout or
                                      completion,       relevant event
                                      unpark, etc.      
  -----------------------------------------------------------------------

## 6. `sleep()` vs `wait()` vs `join()`

  --------------------------------------------------------------------------------------
  Feature                   `sleep()`         `wait()`                 `join()`
  ------------------------- ----------------- ------------------------ -----------------
  Defined on                `Thread`          `Object`                 `Thread`

  Purpose                   Pause current     Wait for                 Wait for another
                            thread            notification/condition   thread to finish

  Releases owned monitor?   No                Yes, releases the called No
                                              object's monitor         

  Timed form?               Yes               Yes                      Yes

  Throws                    Yes               Yes                      Yes
  `InterruptedException`?                                              
  --------------------------------------------------------------------------------------

``` java
synchronized (lock) {
    Thread.sleep(2000); // Still owns lock
}
```

``` java
synchronized (lock) {
    lock.wait(); // Releases lock while waiting
}
```

`wait()` must be called while owning that object's monitor; otherwise,
`IllegalMonitorStateException` is thrown.

## 7. `wait()`, `notify()` and `notifyAll()`

-   `wait()` releases that object's monitor and places the caller in its
    wait set.
-   `notify()` wakes one waiting thread chosen by the JVM.
-   `notifyAll()` wakes all threads waiting on that object's monitor.
-   The notifying thread does not immediately release the lock. A
    notified thread must reacquire the monitor before continuing.
-   These methods must be called while owning the same object's monitor.

Use a condition loop to handle spurious wakeups and notifications that
may occur before a thread starts waiting:

``` java
synchronized (lock) {
    while (!conditionIsMet) {
        lock.wait();
    }
    // Continue after the condition is true
}
```

## 8. Inspecting states with `getState()`

``` java
Thread.State state = worker.getState();

switch (state) {
    case NEW:
        System.out.println("Not started");
        break;
    case RUNNABLE:
        System.out.println("Ready or running");
        break;
    case BLOCKED:
        System.out.println("Waiting for monitor lock");
        break;
    case WAITING:
        System.out.println("Waiting indefinitely");
        break;
    case TIMED_WAITING:
        System.out.println("Waiting for a duration");
        break;
    case TERMINATED:
        System.out.println("Execution completed");
        break;
}
```

`getState()` returns a momentary snapshot. The thread can change state
immediately afterward. Do not use state polling as a synchronization
mechanism. Thread dumps are useful for diagnosing blocked and waiting
threads in production.

## 9. Review of your four exercises

### Exercise 1 --- NEW and TERMINATED

**Correct.** You printed `NEW`, started the thread, called `join()`,
then printed `TERMINATED`. `join()` ensures the worker finishes before
the second state check.

### Exercise 2 --- TIMED_WAITING

**Correct.** The half-second delay gives the worker an opportunity to
enter its five-second sleep. Expected state: `TIMED_WAITING`, though
timing is not guaranteed. Prefer catching `InterruptedException`
specifically rather than generic `Exception`.

### Exercise 3 --- BLOCKED

**Correct.** Thread-1 holds the monitor while sleeping. Thread-2 tries
to enter the same synchronized block and becomes `BLOCKED`. The two 500
ms delays make observation likely, not guaranteed.

### Exercise 4 --- WAITING

**Your original solution needs correction.** You created `worker` but
never called `worker.start()`. Calling `join()` on an unstarted thread
returns immediately. Also, the observer printed main's state while main
was waiting on `observer.join()`, so it could report `WAITING` for the
wrong reason.

Corrected version:

``` java
public class Exercise4 {
    public static void main(String[] args)
            throws InterruptedException {

        Thread mainThread = Thread.currentThread();

        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Worker");

        Thread observer = new Thread(() -> {
            try {
                Thread.sleep(500);
                System.out.println(
                    "Main thread state: " + mainThread.getState()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Observer");

        worker.start();
        observer.start();

        worker.join();   // Main waits for worker
        observer.join();
    }
}
```

Expected observation: `WAITING`. Main waits for the five-second worker
using `join()`. The observer checks main's state after half a second.

## 10. Common mistakes

1.  Assuming `start()` means execution begins immediately.
2.  Treating RUNNABLE and a separate RUNNING state as different Java
    states.
3.  Assuming `sleep()` releases a monitor lock.
4.  Confusing BLOCKED (waiting for monitor) with WAITING (waiting for
    another action).
5.  Assuming `notifyAll()` immediately releases the lock.
6.  Forgetting a notified thread must reacquire the monitor.
7.  Treating arbitrary sleeps as guaranteed ordering.
8.  Calling `join()` on an unstarted thread and assuming it waits.
9.  Treating `getState()` as stable rather than a snapshot.
10. Trying to restart a terminated Thread instance.

## 11. Interview questions and answers

### Q1. How many official states does a Java thread have?

Six: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, and TERMINATED.

### Q2. Does Java have a RUNNING state?

No. A thread that is executing is reported as RUNNABLE.

### Q3. Difference between BLOCKED and WAITING?

BLOCKED means waiting to acquire a monitor lock. WAITING means waiting
indefinitely for another thread's action, such as notification or thread
completion.

### Q4. What is TIMED_WAITING?

A state in which a thread waits for a specified duration, such as
through `sleep()`, timed `wait()`, or timed `join()`.

### Q5. Does sleep() release a lock?

No. A thread retains monitor locks it already owns while sleeping.

### Q6. Does wait() release a lock?

Yes. It releases the monitor of the object on which `wait()` is called.

### Q7. Does notify() release the lock?

No. The notifying thread retains the monitor until it exits the
synchronized block or method.

### Q8. What happens after notifyAll()?

All threads waiting on that monitor are awakened and become eligible to
compete for the monitor. They must reacquire it before continuing.

### Q9. What does join() do?

It makes the calling thread wait for the target thread to terminate.
Untimed join generally places the caller in WAITING.

### Q10. Can a terminated thread be restarted?

No. Calling `start()` again on the same Thread instance throws
`IllegalThreadStateException`.

### Q11. Why is getState() not suitable for synchronization?

It returns only a snapshot. The state can change immediately afterward.
Use coordination primitives instead.

### Q12. Why use sleep(500) in the exercises?

To give another thread time to reach the expected state before observing
it. It increases likelihood but does not guarantee the state.

## 12. Quick revision summary

  Concept         Remember
  --------------- -------------------------------------------------
  NEW             Created, not started
  RUNNABLE        Eligible to run or executing
  BLOCKED         Waiting for monitor lock
  WAITING         Waiting indefinitely
  TIMED_WAITING   Waiting for a duration
  TERMINATED      Execution completed
  `sleep()`       Pauses current thread; does not release monitor
  `wait()`        Releases object's monitor and waits
  `notify()`      Wakes one waiting thread
  `notifyAll()`   Wakes all threads waiting on that monitor
  `join()`        Waits for another thread to terminate
  `getState()`    Returns a momentary state snapshot

### Five key interview takeaways

1.  Java has six official thread states.
2.  RUNNABLE includes ready and running.
3.  BLOCKED is specifically about monitor-lock acquisition.
4.  `wait()` releases the object's monitor; `sleep()` does not.
5.  A notified thread must reacquire the monitor before resuming.

## 13. Recommendations and practice

Before moving on, make sure you can: - Explain all six states without
notes. - Write examples for NEW, TIMED_WAITING, BLOCKED, WAITING, and
TERMINATED. - Explain why 500 ms delays help but are not guarantees. -
Demonstrate the difference between `sleep()` and `wait()`. - Explain why
`notifyAll()` does not immediately release a monitor. - Use `join()` to
wait for a worker. - Explain why `getState()` is only a snapshot.

**Recommended experiment:** Run the BLOCKED example repeatedly, change
the delay values, and observe how timing affects the state. Then
consider how `CountDownLatch` could make the test more deterministic.

## 14. Next lesson

**Lesson 5 --- Thread Interruption and Safe Cancellation**

Topics: `interrupt()`, `isInterrupted()`, `Thread.interrupted()`,
`InterruptedException`, cooperative cancellation, and safely stopping
long-running tasks.
