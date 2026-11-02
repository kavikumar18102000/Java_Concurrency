package Lesson3.Exercise;

public class PrintNumbers implements Runnable {

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.println(Thread.currentThread().getName() +" : " +i);
        }
    }
    
    public static void main(String[] args) {
        Thread thread = new Thread(new PrintNumbers());
        thread.start();
    }
    
}
