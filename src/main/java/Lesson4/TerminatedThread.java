package Lesson4;

public class TerminatedThread {
    public static void main(String[] args) throws InterruptedException{
        Thread worker = new Thread(()->System.out.println("Task completed"));
        
        worker.start();
        worker.join();
        
        System.out.println(worker.getState());
    }
}
