package Lesson3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunnableThread implements Runnable {

    @Override
    public void run() {
        System.out.println("Runnable Interface");
        
    }
    
    public static void main(String[] args) {
        RunnableThread runnableThread = new RunnableThread();
        Thread thread = new Thread(runnableThread);
        thread.start();

        //or ExecutorService can be used to run the runnable code.
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(runnableThread);
    }
    
}
