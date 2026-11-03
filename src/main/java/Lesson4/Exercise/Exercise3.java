package Lesson4.Exercise;

public class Exercise3 {
    public static void main(String[] args) throws InterruptedException{
        Object lock = new Object();
        Thread t1 = new Thread(()->{
            try {
                synchronized (lock){
                Thread.sleep(5000);
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }, "Thread - 1");
        
        Thread t2 = new Thread(()->{
            synchronized (lock){
                System.out.println("Thread 2 acquired lock.");
            }
        }, "Thread - 2");
        
        t1.start();
        Thread.sleep(500);
        
        t2.start();
        Thread.sleep(500);
        
        System.out.println(t2.getState());
    }
}
