package Lesson4;

public class WaitingStateThread {
    public static void main(String[] args) throws InterruptedException{
        Thread workThread = new Thread(()->{
            try{
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread mainThread = Thread.currentThread();
        
        Thread observer = new Thread(()->System.out.println(mainThread.getState()));
        
        observer.start();
        observer.join();
        workThread.join();
    }

}
