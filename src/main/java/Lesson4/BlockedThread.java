package Lesson4;

public class BlockedThread {
    public static void main(String[] args) throws InterruptedException{
        Object lock = new Object();
        Thread t1 = new Thread(()->{
            synchronized (lock){
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread t2 = new Thread(()->{
            synchronized (lock){
                System.out.println("Thread 2 acqired lock.");
            }
        });
        
        t1.start();
        Thread.sleep(500);
        
        t2.start();
        Thread.sleep(500);
        
        System.out.println(t2.getState());
    }
}
