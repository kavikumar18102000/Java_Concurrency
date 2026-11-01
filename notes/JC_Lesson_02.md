# Java Concurrency & Multithreading

## Lesson 2 --- The Main Thread and Your First Java Threads

> **Learning goal:** Understand the JVM's main thread, inspect thread
> properties, create a thread, and explain `start()` vs `run()`.

------------------------------------------------------------------------

## 1. What happens when a Java application starts?

When you execute `java Demo`, the operating system starts the Java
launcher/JVM process. The JVM initializes the application and invokes
`Demo.main(String[] args)` on the initial thread, conventionally named
`main`.

``` text
java Demo
   |
Operating System starts Java launcher / JVM process
   |
JVM initializes the application
   |
Initial thread ("main") invokes Demo.main()
   |
Your application code executes
```

The JVM invokes `main()`; you do not normally call it yourself. A JVM
process may also have JVM-internal threads (for example,
garbage-collection or JIT-related threads), depending on the runtime and
application.

## 2. What is the Main Thread?

The **main thread** is the initial thread that begins executing the
application's `main()` method.

``` java
public class Demo {
    public static void main(String[] args) {
        System.out.println("Hello Java");
    }
}
```

Statements in `main()` execute on the main thread unless work is
explicitly delegated to another thread or execution mechanism.

## 3. Inspecting the current thread

`Thread.currentThread()` is a static method that returns the `Thread`
object representing the thread currently executing the code. It does not
create a thread.

``` java
public class ThreadInfoDemo {
    public static void main(String[] args) {
        Thread current = Thread.currentThread();

        System.out.println("Name     : " + current.getName());
        System.out.println("ID       : " + current.threadId()); // Java 19+
        System.out.println("Priority : " + current.getPriority());
        System.out.println("State    : " + current.getState());
    }
}
```

Possible output (ID and state can vary):

``` text
Name     : main
ID       : 1
Priority : 5
State    : RUNNABLE
```

### Common thread properties

  -----------------------------------------------------------------------
  Property                API                     Meaning
  ----------------------- ----------------------- -----------------------
  Name                    `getName()`             Human-readable thread
                                                  name

  ID                      `threadId()`            Thread identifier (Java
                                                  19+)

  Priority                `getPriority()`         Priority from 1 to 10

  State                   `getState()`            Current Java thread
                                                  state

  Thread group            `getThreadGroup()`      Group to which the
                                                  thread belongs
  -----------------------------------------------------------------------

**Version note:** `Thread.getId()` is deprecated in newer Java releases;
`threadId()` is the modern API (available since Java 19). On older JDKs,
use `getId()`.

### Thread priority

-   `Thread.MIN_PRIORITY` = 1
-   `Thread.NORM_PRIORITY` = 5
-   `Thread.MAX_PRIORITY` = 10

A new thread normally inherits its creator's priority. Priority is a
scheduling hint, **not a guarantee** that a higher-priority thread runs
first or gets a specific amount of CPU time. Never rely on priority for
correctness or coordination.

### Thread state

`getState()` returns a value from `Thread.State`: `NEW`, `RUNNABLE`,
`BLOCKED`, `WAITING`, `TIMED_WAITING`, or `TERMINATED`.

`RUNNABLE` means the thread is eligible to run or is running; it does
not prove that it is executing on a CPU at that exact instant. The full
lifecycle will be covered later.

## 4. Renaming a thread

Use `setName()` to assign a descriptive name. This is useful in logs and
debugging.

``` java
public class RenameThreadDemo {
    public static void main(String[] args) {
        Thread current = Thread.currentThread();
        current.setName("application-main");
        System.out.println(current.getName());
    }
}
```

Example names: `pdf-generator`, `payment-worker-1`,
`notification-worker`.

## 5. Creating a thread by extending `Thread`

One way to create a thread is to extend `Thread` and override `run()`.

``` java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from child thread!");
        System.out.println("Executing thread: "
                + Thread.currentThread().getName());
    }
}

public class ThreadCreationDemo {
    public static void main(String[] args) {
        MyThread worker = new MyThread();
        worker.start();

        System.out.println("Hello from main thread!");
    }
}
```

Possible output:

``` text
Hello from main thread!
Hello from child thread!
Executing thread: Thread-0
```

The child message may appear first instead. The main and child threads
are independently eligible to run, so their output order is not
guaranteed.

### What is `run()`?

`run()` contains the work that the thread is intended to perform.
Overriding it defines the task's behavior.

### What is `start()`?

Calling `start()` asks the JVM to start a new thread of execution. That
new thread will execute `run()`. The scheduler determines when it
receives CPU time.

A `Thread` instance can be started **only once**. Calling `start()`
again on the same instance throws `IllegalThreadStateException`, even if
its first execution has completed.

## 6. `start()` vs `run()` --- essential distinction

### Calling `start()`

``` java
MyThread worker = new MyThread();
worker.start();
```

-   Starts a new thread of execution.
-   The new thread invokes `run()`.
-   The calling thread can continue independently.
-   Exact execution order is not guaranteed.

### Calling `run()` directly

``` java
MyThread worker = new MyThread();
worker.run();
```

This is an ordinary method call. It does **not** start a new thread. The
thread that calls `run()` executes its body. In this example, that is
`main`.

  -----------------------------------------------------------------------
  `start()`                           Direct `run()` call
  ----------------------------------- -----------------------------------
  Starts a new thread of execution    Does not create a thread

  New thread executes `run()`         Calling thread executes `run()`

  May overlap with caller             Ordinary synchronous method call

  A `Thread` instance can be started  A normal method can be called
  once                                repeatedly
  -----------------------------------------------------------------------

**Interview answer:** `start()` starts a new thread and the JVM arranges
for that thread to execute `run()`. Calling `run()` directly is a normal
method invocation, so it executes on the current thread and does not
create another thread.

## 7. Thread scheduling and output order

After `start()` is called, both the main thread and child thread may be
ready to run. The JVM and operating-system scheduler determine when
eligible threads receive CPU time.

Never depend on an assumed execution order. When a particular order is
required, use coordination mechanisms such as `join()`, locks, or
higher-level concurrency utilities. These are covered in later lessons.

## 8. Exercise review and corrections

### Exercise 1 --- Print main-thread properties

**Your approach is correct.** You used `Thread.currentThread()` and
printed the name, ID, priority, and state. A polished version:

``` java
public class Exercise1 {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();

        System.out.println("Name     : " + thread.getName());
        System.out.println("ID       : " + thread.threadId()); // Java 19+
        System.out.println("Priority : " + thread.getPriority());
        System.out.println("State    : " + thread.getState());
    }
}
```

For a JDK earlier than Java 19, use `thread.getId()` instead of
`thread.threadId()`. Adding labels makes the output easier to read.

### Exercise 2 --- Create a child thread

**Correct.** You extended `Thread`, implemented `run()`, and called
`start()`. Add `@Override` so the compiler can verify that you are
overriding the method.

``` java
public class Exercise2 extends Thread {
    @Override
    public void run() {
        System.out.println("Hello from child thread..");
        System.out.println("Current thread: "
                + Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        Exercise2 exercise2 = new Exercise2();
        exercise2.start();
    }
}
```

The child thread commonly gets a default name such as `Thread-0`; the
exact name can vary.

### Exercise 3 --- Does output order stay the same?

Your submitted answer explained that calling `start()` twice on the same
thread throws `IllegalThreadStateException`. That statement is correct,
but it answers a different question.

**Answer to the original exercise:** No, the output order is not
guaranteed. After `start()`, the main thread and child thread can both
be scheduled. Their execution order depends on scheduling and runtime
conditions, so output may differ between runs.

**Your additional point --- starting the same thread twice:**

``` java
MyThread worker = new MyThread();

worker.start(); // First start: valid
worker.start(); // Second start on same object: IllegalThreadStateException
```

A `Thread` object is single-use. To perform the task again, create a new
`Thread` instance or, in production code, generally submit another task
to an executor.

### Exercise 4 --- Replace `start()` with `run()`

**Your reasoning is correct.** Directly calling `run()` does not create
a new thread; the main thread executes the method.

``` java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Current thread: "
                + Thread.currentThread().getName());
    }
}

public class Exercise4 {
    public static void main(String[] args) {
        MyThread worker = new MyThread();
        worker.run(); // Direct method call
    }
}
```

Output:

``` text
Current thread: main
```

If you replace `worker.run()` with `worker.start()`, the output will
normally show the worker's name, such as `Thread-0`. Print
`Thread.currentThread().getName()` inside `run()` to identify which
thread executes it.

## 9. Common mistakes to avoid

1.  Calling `run()` and expecting a new thread: only `start()` starts a
    new thread.
2.  Starting the same `Thread` instance twice: this throws
    `IllegalThreadStateException`.
3.  Assuming output order is fixed: scheduling is nondeterministic
    unless threads are coordinated.
4.  Treating priority as a guarantee: priority does not guarantee
    execution order.
5.  Assuming `RUNNABLE` means currently using a CPU: it includes
    ready-to-run and running.
6.  Forgetting `@Override`: it helps catch accidental method-signature
    mistakes.

## 10. Interview questions and concise answers

### Q1. Who calls the `main()` method?

The JVM invokes the application's `main()` entry point on the initial
thread, conventionally named `main`.

### Q2. What is the main thread?

It is the initial application thread that begins executing the `main()`
method.

### Q3. What does `Thread.currentThread()` return?

It returns the `Thread` object representing the thread currently
executing the code.

### Q4. What does `Thread[main,5,main]` mean?

It is a typical string representation showing the thread's name
(`main`), priority (`5`), and thread group (`main`).

### Q5. What is the difference between `start()` and `run()`?

`start()` starts a new thread that will execute `run()`. Calling `run()`
directly is a normal method call on the current thread.

### Q6. Is thread execution order guaranteed?

No. The scheduler determines when eligible threads receive CPU time. Use
coordination mechanisms when an order is required.

### Q7. Why does calling `run()` directly not create a new thread?

Because `run()` is an ordinary method. A new thread is started through
`start()`.

### Q8. Can we call `start()` twice on the same Thread object?

No. A `Thread` object can be started only once. A subsequent call throws
`IllegalThreadStateException`.

### Q9. Can a thread be started again after it terminates?

No. The same `Thread` object cannot be restarted. Create a new thread
object or submit a new task to an executor.

### Q10. Does a higher-priority Java thread always execute first?

No. Priority is only a scheduling hint and does not guarantee execution
order.

### Q11. What does `RUNNABLE` mean?

The thread is executing or is eligible to execute when CPU time is
available.

### Q12. Why give threads meaningful names?

Meaningful names make application logs, debugging, and thread-dump
analysis easier.

## 11. Lesson 2 --- Quick summary

  -----------------------------------------------------------------------
  Concept                             Remember
  ----------------------------------- -----------------------------------
  Main thread                         Initial application thread;
                                      conventionally named `main`

  `main()`                            JVM invokes the application's entry
                                      point

  `currentThread()`                   Returns the currently executing
                                      thread

  `getName()`                         Reads a thread's name

  `threadId()`                        Reads its ID (Java 19+)

  `getPriority()`                     Reads priority (1--10)

  `getState()`                        Reads its Java thread state

  `setName()`                         Changes a thread's name

  `run()`                             Defines the task; direct call is an
                                      ordinary method call

  `start()`                           Starts a new thread of execution

  Scheduler                           Determines when eligible threads
                                      get CPU time

  Output order                        Not guaranteed without coordination

  Repeated `start()`                  Throws
                                      `IllegalThreadStateException`
  -----------------------------------------------------------------------

### Five points to remember before an interview

1.  The JVM invokes `main()` on the initial `main` thread.
2.  `Thread.currentThread()` identifies the thread executing the current
    code.
3.  `start()` starts a new thread; direct `run()` does not.
4.  Thread scheduling does not guarantee a particular output order.
5.  A `Thread` instance can be started only once.

## 12. Suggested practice before Lesson 3

1.  Run the `start()` example several times and observe the output
    order.
2.  Run the same example with a direct `run()` call and compare thread
    names.
3.  Try starting the same thread object twice and observe the exception.
4.  Create a new thread object and give it a descriptive name using
    `setName()`.
5.  Print the current thread name from both `main()` and `run()`.

**Next lesson:** Creating threads with `Runnable` and `Callable`, and
understanding why separating a task from a `Thread` is useful.
