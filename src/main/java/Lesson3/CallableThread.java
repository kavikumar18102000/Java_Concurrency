package Lesson3;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallableThread {
    public static void main(String[] args) {
        Callable<Integer> task = ()->{
            System.out.println("""
                    Callable executing on : 
                    """+Thread.currentThread().getName());
            return (Integer) 10 + 20;
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> futureResult = executorService.submit(task);
        try {
            
            Integer i = futureResult.get();
            System.out.println(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }
    }
    
}
