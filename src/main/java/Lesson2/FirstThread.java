package Lesson2;

public class FirstThread extends Thread{
    
    @Override 
    public void run(){
        System.out.println("Fist thread...");
    }
    
    public static void main(String[] args){
        FirstThread firstThread = new FirstThread();
        firstThread.start();
        
        System.out.println("main thread...");
        
        firstThread.run();
    }
    
}
