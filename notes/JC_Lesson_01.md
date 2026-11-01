# Java Concurrency - Chapter 1 Quick Revision Notes

## 1. Program

-   A **Program** is a set of instructions written by a programmer.
-   It is stored on disk and is **not executing**.
-   Example: `HelloWorld.class`

**Analogy:** A recipe in a cookbook.

------------------------------------------------------------------------

## 2. Process

-   A **Process** is a **running instance of a program**.
-   Created by the Operating System when the program starts.
-   Has its own:
    -   Memory
    -   Heap
    -   Threads
    -   Open files
    -   Network connections
    -   Execution context

**Interview Definition:** \> A process is an active, running instance of
a program with allocated resources.

------------------------------------------------------------------------

## 3. Thread

-   A **Thread** is the **smallest unit of execution**.
-   Threads execute Java code.
-   A process is only a container; threads do the actual work.

**Important:** Every Java application starts with at least one thread
called **`main`**.

------------------------------------------------------------------------

## 4. Program vs Process vs Thread

  Program               Process                Thread
  --------------------- ---------------------- -------------------------
  Stored on disk        Running program        Unit that executes code
  Passive               Active                 Active
  No memory allocated   Has memory/resources   Uses process resources

------------------------------------------------------------------------

## 5. Process vs Thread

  Process                        Thread
  ------------------------------ ------------------------
  Own memory space               Shares process memory
  Heavyweight                    Lightweight
  Expensive to create            Cheap to create
  Contains one or more threads   Belongs to one process

------------------------------------------------------------------------

## 6. Memory Model

Shared among threads: - Heap - Objects - Static variables

Private to each thread: - Stack - Program Counter (PC Register) - Method
call frames - Local variables

### Visualization

``` text
Java Process
├── Heap (Shared)
│
├── Thread-1
│   ├── Stack
│   └── PC Register
│
├── Thread-2
│   ├── Stack
│   └── PC Register
│
└── Thread-3
    ├── Stack
    └── PC Register
```

------------------------------------------------------------------------

## 7. Why Multithreading?

Allows multiple tasks to make progress together, improving
responsiveness.

Examples: - Download files - Play music - Receive notifications - Handle
user input

------------------------------------------------------------------------

## 8. CPU Cores and Threads

### Single-Core CPU

-   Only **one thread executes at any instant**.
-   OS performs **context switching**.

### Multi-Core CPU

-   One thread can execute per core.
-   Example:
    -   4 cores → Up to 4 threads simultaneously.
    -   8 cores → Up to 8 threads simultaneously.

------------------------------------------------------------------------

## 9. Context Switching

The operating system rapidly switches CPU execution between threads,
giving the illusion that many tasks run at the same time.

------------------------------------------------------------------------

## 10. Concurrency vs Parallelism

### Concurrency

-   Multiple tasks make progress during the same period.
-   May involve context switching.
-   Possible on a single-core CPU.

### Parallelism

-   Multiple tasks execute at the exact same time.
-   Requires multiple CPU cores.

------------------------------------------------------------------------

## 11. Key Interview Facts

-   A process **does not execute code**; threads execute code.
-   Every process must have at least one thread.
-   Every Java application starts with the `main` thread.
-   Threads share heap memory.
-   Each thread has its own stack.
-   Local variables are generally thread-safe because they are stored on
    the thread's private stack.

------------------------------------------------------------------------

## 12. Common Interview Questions

1.  Difference between Program, Process, and Thread.
2.  Why are threads lightweight?
3.  Why do threads share heap memory?
4.  Why does each thread have its own stack?
5.  Difference between concurrency and parallelism.
6.  Can a process exist without a thread? (No)
7.  Which component executes Java code? (Thread)

------------------------------------------------------------------------

## 13. Quick Recap

-   Program = Instructions stored on disk.
-   Process = Running instance of a program.
-   Thread = Smallest unit of execution.
-   Heap = Shared.
-   Stack = Private.
-   Single core = Concurrency via context switching.
-   Multi-core = Parallel execution possible.
-   JVM starts execution using the `main` thread.
