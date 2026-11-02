# Java Concurrency & Multithreading

## Lesson 3 --- Runnable, Callable and Future

> Learning objective: Separate a task from the thread that executes it,
> create tasks with Runnable and Callable, and understand
> ExecutorService and Future.

## 1. Why Runnable?

Extending Thread works, but Java supports single class inheritance. If a
class already extends another class, it cannot also extend Thread.
Implementing Runnable separates task logic from thread management.

-   Runnable describes **what work** should be performed.
-   Thread is one mechanism that can execute that work.

## 2. Runnable

Runnable is a functional interface in `java.lang`:

``` java
@FunctionalInterface
public interface Runnable {
    void run();
}
```

It represents a task that does not return a result through `run()`.

``` java
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Executing task...");
        System.out.println(Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        MyTask task = new MyTask();
        Thread thread = new Thread(task);
        thread.start();
    }
}
```

Flow: create task → pass it to `new Thread(task)` → call `start()` → the
new thread executes `task.run()`.

Creating a Runnable does not start a thread.

## 3. Thread vs Runnable

  -----------------------------------------------------------------------
  Thread                              Runnable
  ----------------------------------- -----------------------------------
  Class representing a thread of      Interface representing a task
  execution                           

  Provides thread operations such as  Defines task logic through `run()`
  `start()`                           

  Can be extended                     Can be implemented

  Extending it uses the class         Can be implemented while extending
  inheritance slot                    another class
  -----------------------------------------------------------------------

**Recommendation:** Prefer Runnable for task definitions. It keeps task
logic independent of how it is executed.

## 4. Runnable and lambdas

Runnable has one abstract method, so it can be implemented with a
lambda:

``` java
Runnable task = () -> {
    System.out.println("Hello from Runnable");
};

new Thread(task).start();
```

Or:

``` java
new Thread(() ->
    System.out.println("Hello from thread")
).start();
```

A lambda does not itself create a thread. `Thread.start()` starts the
new thread.

## 5. Callable

`Callable<V>` is a functional interface in `java.util.concurrent`:

``` java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;
}
```

-   `call()` returns a value of type `V`.
-   It can declare checked exceptions.
-   Examples: `Callable<Integer>`, `Callable<String>`,
    `Callable<Double>`.

### Runnable vs Callable

  -----------------------------------------------------------------------
  Runnable                            Callable
  ----------------------------------- -----------------------------------
  `run()`                             `call()`

  Returns `void`                      Returns a value

  Cannot declare checked exceptions   Can declare checked exceptions
  in method signature                 

  Java 1.0                            Java 5

  Can be passed to a Thread           Cannot be passed directly to a
  constructor                         Thread constructor

  Can be submitted to ExecutorService Can be submitted to ExecutorService
  -----------------------------------------------------------------------

Use Runnable when no result is needed; use Callable when the task must
return a result or propagate a checked exception.

## 6. Why Callable uses ExecutorService

Thread constructors accept Runnable, not Callable:

``` java
Callable<Integer> task = () -> 100;

// Does not compile:
// Thread thread = new Thread(task);
```

Submit Callable to an ExecutorService instead. An executor manages task
execution and may reuse worker threads.

``` java
ExecutorService executor =
        Executors.newSingleThreadExecutor();

Callable<Integer> task = () -> 100 + 200;
Future<Integer> future = executor.submit(task);
```

## 7. ExecutorService: submit, execute and shutdown

-   `submit()` accepts Runnable or Callable and returns a Future.
-   `execute()` accepts Runnable and returns `void`.
-   `shutdown()` initiates orderly shutdown: previously submitted tasks
    can finish, but new tasks are rejected.

  -----------------------------------------------------------------------
  `execute(Runnable)`                 `submit(...)`
  ----------------------------------- -----------------------------------
  Accepts Runnable                    Accepts Runnable or Callable

  Returns void                        Returns Future

  No Future handle                    Future can track completion,
                                      retrieve result, or attempt
                                      cancellation
  -----------------------------------------------------------------------

Executors can reuse existing worker threads; every submission does not
necessarily create a new thread.

## 8. Future

A `Future<V>` represents the pending or completed result of an
asynchronous computation.

  -----------------------------------------------------------------------
  Method                              Purpose
  ----------------------------------- -----------------------------------
  `get()`                             Waits for completion if necessary
                                      and retrieves result

  `get(timeout, unit)`                Waits up to a specified duration

  `isDone()`                          Checks whether task completed

  `isCancelled()`                     Checks whether task was cancelled

  `cancel(true)`                      Attempts cancellation and may
                                      interrupt executing thread
  -----------------------------------------------------------------------

**Important:** `Future.get()` is blocking. If the task has not
completed, the calling thread waits. `isDone()` is only a snapshot; its
value depends on timing.

``` java
Future<String> future = executor.submit(() -> {
    Thread.sleep(3000);
    return "Task Completed";
});

System.out.println("Task submitted");
System.out.println("Main thread is doing other work");

String result = future.get(); // Blocks if task is unfinished
System.out.println(result);
```

## 9. Exception handling

Runnable's `run()` does not declare checked exceptions. Catch them
inside the task:

``` java
Runnable task = () -> {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
};
```

Callable's `call()` declares `throws Exception`, so checked exceptions
can propagate:

``` java
Callable<String> task = () -> {
    Thread.sleep(1000);
    return "Completed";
};
```

When calling `Future.get()`, handle: - `InterruptedException`: the
waiting thread was interrupted. Restore its interrupt status if it
cannot be propagated. - `ExecutionException`: the task failed; inspect
`getCause()` for the original exception.

``` java
try {
    String result = future.get();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} catch (ExecutionException e) {
    System.out.println("Task failed: " + e.getCause());
}
```

## 10. Exercise review and corrected solutions

### Exercise 1 --- Runnable class

**Your solution: Correct.** You implemented Runnable, overrode `run()`,
printed the current thread name, and used `start()`.

``` java
public class PrintNumbers implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.println(
                Thread.currentThread().getName() + " : " + i);
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(
            new PrintNumbers(), "number-printer");
        thread.start();
    }
}
```

A descriptive thread name helps debugging.

### Exercise 2 --- Lambda Runnable

**Your solution: Correct.**

``` java
public class PrintNumberLambda {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println(
                    Thread.currentThread().getName() + " : " + i);
            }
        });
        thread.start();
    }
}
```

The lambda implements Runnable's `run()` method.

### Exercise 3 --- Two tasks

**Your conclusion was correct:** output order is not guaranteed.

Refinement: two threads do not necessarily run on different CPU cores.
They may run in parallel on different cores or be interleaved through
context switching on one core. Output can interleave before either
thread completes; the order depends on scheduling and timing, not simply
on which thread finishes first.

Interview-ready answer:

> The execution order is not guaranteed because scheduling is controlled
> by the JVM and operating system. Threads may execute concurrently
> through context switching or in parallel on multiple CPU cores. Their
> output can interleave and vary between runs.

### Exercise 4 --- Callable addition

**Your solution: Correct.** You used Callable, ExecutorService,
submit(), Future.get(), and shutdown().

Design recommendation: collect console input on the main thread and pass
values into Callable. Keep Callable focused on calculation.

``` java
import java.util.Scanner;
import java.util.concurrent.*;

public class Exercise4 {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter A: ");
            int a = sc.nextInt();

            System.out.print("Enter B: ");
            int b = sc.nextInt();

            Callable<Integer> task = () -> a + b;
            ExecutorService executor =
                    Executors.newSingleThreadExecutor();

            try {
                Future<Integer> future = executor.submit(task);
                System.out.println("Sum: " + future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                System.out.println("Task failed: " + e.getCause());
            } finally {
                executor.shutdown();
            }
        }
    }
}
```

Responsibilities are separated: - Main thread: collect input. -
Callable: calculate. - ExecutorService: execute. - Future: represent
result.

### Exercise 5 --- Future and delayed task

**Your solution: Correct.** You demonstrated submission, other
main-thread work, checking completion, and retrieving the result.

``` java
import java.util.concurrent.*;

public class FutureDemo {
    public static void main(String[] args) {
        Callable<String> task = () -> {
            Thread.sleep(5000);
            return "Processing Completed";
        };

        ExecutorService executor =
                Executors.newSingleThreadExecutor();

        try {
            Future<String> future = executor.submit(task);

            System.out.println("Task submitted");
            System.out.println("Main thread is doing other work");
            System.out.println("Is done? " + future.isDone());

            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            System.out.println("Task failed: " + e.getCause());
        } finally {
            executor.shutdown();
        }
    }
}
```

Notes: - `isDone()` will normally be false while the five-second task is
running, but its value is timing-dependent. - `future.get()` blocks if
the task is unfinished. - Rename your class `FutureDemo`, not
`FutureTask`, to avoid confusion with Java's
`java.util.concurrent.FutureTask`. - Handle InterruptedException and
ExecutionException separately.

## 11. Common mistakes

1.  Assuming Runnable creates a thread: it only describes a task.
2.  Assuming a lambda creates a thread: it implements a functional
    interface.
3.  Passing Callable to Thread: Thread constructors accept Runnable, not
    Callable.
4.  Assuming every executor submission creates a new thread: executors
    can reuse workers.
5.  Calling Future.get() too early: it can block the caller.
6.  Assuming isDone() must be false immediately after submit(): a task
    may finish quickly.
7.  Catching all exceptions generically: handle interruption separately.
8.  Mixing console input with task computation: keep responsibilities
    separate.
9.  Assuming threads always run on different cores: scheduling
    determines execution.

## 12. Interview questions and answers

### Q1. Difference between Thread and Runnable?

Thread represents a thread of execution and provides lifecycle
operations. Runnable represents a task. Runnable separates task logic
from thread management.

### Q2. Why prefer Runnable over extending Thread?

Java supports single class inheritance. Runnable lets a class extend
another class and keeps task logic independent of thread management.

### Q3. Is Runnable a functional interface?

Yes. It has exactly one abstract method, `run()`, so it can be
implemented with a lambda.

### Q4. Can Runnable execute without creating a new Thread?

Yes. Calling `run()` directly executes synchronously on the calling
thread. An executor can also execute Runnable tasks.

### Q5. Difference between Runnable and Callable?

Runnable's `run()` returns void and cannot declare checked exceptions.
Callable's `call()` returns a value and can declare checked exceptions.

### Q6. Why can't Callable be passed to Thread?

Thread constructors accept Runnable, not Callable. Callable tasks are
commonly submitted to ExecutorService, which returns a Future.

### Q7. What is Future?

Future represents the result of an asynchronous computation and lets you
retrieve the result, check completion, or attempt cancellation.

### Q8. Is Future.get() blocking?

Yes. It waits if the computation is unfinished. The timed overload waits
only up to the specified timeout.

### Q9. What if a Callable throws an exception?

The task failure is captured. Calling `Future.get()` throws
`ExecutionException`; its cause contains the original exception.

### Q10. Can multiple threads execute the same Runnable object?

Yes. The same instance can be passed to multiple threads or submitted
more than once. Shared mutable state must be made thread-safe.

### Q11. Can Callable return different data types?

Each Callable instance has one declared result type, such as
`Callable<Integer>` or `Callable<String>`. Different instances can use
different types.

### Q12. Difference between submit() and execute()?

`execute()` accepts Runnable and returns void. `submit()` accepts
Runnable or Callable and returns a Future for tracking completion or
retrieving a result.

## 13. Quick summary

  Concept           Remember
  ----------------- --------------------------------------------------
  Runnable          Task with no return value
  Callable          Task that returns a value
  Lambda            Concise implementation of a functional interface
  Thread            One mechanism for executing a task
  ExecutorService   Manages task execution
  `submit()`        Submits a task and returns Future
  `execute()`       Submits Runnable without returning Future
  Future            Represents asynchronous result
  `get()`           Retrieves result; blocks if necessary
  `isDone()`        Checks whether computation completed
  `cancel()`        Attempts to cancel computation
  `shutdown()`      Initiates orderly executor shutdown

Execution model:

``` text
Runnable / Callable
       | defines WHAT work to perform
       v
Thread / ExecutorService
       | executes or schedules the task
       v
Future (for submitted tasks)
       | represents completion and result
       v
Result or task failure
```

### Five interview takeaways

1.  Runnable and Callable represent tasks, not threads.
2.  Runnable returns void; Callable returns a value.
3.  ExecutorService manages and can reuse worker threads.
4.  Future.get() blocks until a result is available.
5.  A task can be reused, but shared mutable state must be handled
    safely.

## 14. Recommendations and practice

Before moving on, make sure you can: - Explain why Runnable is preferred
over extending Thread for task definitions. - Write the same Runnable
using a class and a lambda. - Explain why Callable cannot be passed to a
Thread constructor. - Submit a Callable and retrieve its result using
Future. - Demonstrate that Future.get() blocks while the task is
running. - Explain why output from two threads can interleave. - Handle
InterruptedException by restoring interrupt status when appropriate. -
Keep input collection separate from task computation.

**Recommended experiment:** Submit the same Runnable instance twice to a
fixed thread pool. Observe that both executions can happen. Then
consider what changes if the Runnable modifies a shared field.

## 15. Next lesson

**Lesson 4 --- Java Thread Lifecycle and States**

Topics: `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`,
`TERMINATED`, `sleep()`, `join()`, and `interrupt()`.
