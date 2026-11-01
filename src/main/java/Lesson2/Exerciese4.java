package Lesson2;

public class Exerciese4 extends Thread{
    public void run(){
        System.out.println("Child thread...");
    }
    
    public static void main(String[] args) {
        Exerciese4 exerciese4 = new Exerciese4();
        exerciese4.run();
        System.out.println(Thread.currentThread().getName());
    }
    
}
