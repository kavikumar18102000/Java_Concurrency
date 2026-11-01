package Lesson2;

public class Exercise2 extends Thread{
    public void run(){
        System.out.println("Hello from child thread..");
    }
    public static void main(String[] args) {
        Exercise2 exercise2 = new Exercise2();
        exercise2.start();
        exercise2.start();
    }
}
