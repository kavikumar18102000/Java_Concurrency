package Basic;

class ThreadCreation3 extends Thread{

    public void run(){
        System.out.println("Thread creation 3....");
    }
}

class ThreadCreation4 implements Runnable{

    @Override
    public void run() {
        System.out.println("Thread creation 4...");
    }
}

public class ThreadCreation {
    static void main() {
        Runnable task = ()-> System.out.println("Basic Thread creation....");
        Thread thread = new Thread(task, "Thread 1");
        thread.start();

        Thread thread2 = new Thread(()-> System.out.println("Thread creation 2"), "Thread 2");
        thread2.start();

        ThreadCreation3 threadCreation3 = new ThreadCreation3();
        threadCreation3.start();

        ThreadCreation4 threadCreation4 = new ThreadCreation4();
        new Thread(threadCreation4).start();

//        task.run();
    }
}
