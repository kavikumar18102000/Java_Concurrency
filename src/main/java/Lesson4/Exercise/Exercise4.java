package Lesson4.Exercise;

public class Exercise4 {
    public static void main(String[] args) throws InterruptedException{
        Thread worker = new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }, "Worker");

        Thread mainThread = Thread.currentThread();
        
        Thread observer =  new Thread(()->System.out.println(mainThread.getState()));
    
        observer.start();
        observer.join();
        worker.join();
        
    }
}
