package Lesson4;

public class TimedWaitingThread {
    public static void main(String[] args) throws InterruptedException{
        Thread worker = new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        
        worker.start();
        Thread.sleep(500);
        
        System.out.println(worker.getState());
    }

}
