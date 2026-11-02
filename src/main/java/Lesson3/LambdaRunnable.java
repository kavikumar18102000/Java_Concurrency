package Lesson3;

public class LambdaRunnable {
    public static void main(String[] args) {
        Runnable task = ()->System.out.println("Lambda Runnable  task");
        Thread thread = new Thread(task);
        thread.start();
        
        Thread thread2 = new Thread(()->System.out.println("Simplified Lambda"));
        thread2.start();
    }
}
