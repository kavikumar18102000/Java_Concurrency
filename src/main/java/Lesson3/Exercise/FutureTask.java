package Lesson3.Exercise;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureTask {
    public static void main(String[] args) {
        Callable<String> callable = ()->{
            Thread.sleep(5000);
            return "Task completed";
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(callable);
        System.out.println("Taks submitted");
        System.out.println("Main thread is doing other work");
        System.out.println("Is done? "+future.isDone());
        try {
            
         String res = future.get();
         System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }
    }
}
