package Lesson3.Exercise;

public class PrintNumberLambda {
    public static void main(String[] args) {
        Thread thread =  new Thread(()->{
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName()+" : "+i);
            }
        });
        
        thread.start();
    }
}
