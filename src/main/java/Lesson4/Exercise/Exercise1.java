package Lesson4.Exercise;

public class Exercise1 {
    public static void main(String[] args) throws InterruptedException{
        Thread t1 = new Thread(()->System.out.println("Exercise 1"));
        System.out.println(t1.getState());
        t1.start();
        t1.join();
        
        System.out.println(t1.getState());
        
    }
}
