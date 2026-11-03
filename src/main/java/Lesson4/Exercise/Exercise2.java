package Lesson4.Exercise;

public class Exercise2 {
    public static void main(String[] args) throws InterruptedException{
        Thread workerThread = new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        
        workerThread.start();
        Thread.sleep(500);
        
        System.out.println(workerThread.getState());
    }
}
