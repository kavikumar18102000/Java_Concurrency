package Lesson4;

//Thread lifecyle(NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)

public class ThreadStateDemo {
    public static void main(String[] args) throws InterruptedException{
        // NEW STATE
        Thread thread = new Thread(()->System.out.println("Thread State Demo"));
        System.out.println(thread.getState());
        
        // RUNNABLE STATE
        thread.start();
        System.out.println(thread.getState());
    }
}
