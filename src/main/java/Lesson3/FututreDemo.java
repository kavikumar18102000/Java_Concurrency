package Lesson3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FututreDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> futureTask = executorService.submit(() -> {
            Thread.sleep(5000);
            return "Task Comleted";
        });
        
        System.out.println("Is task done? "+futureTask.isDone());
        try {
            String s = futureTask.get();
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }
}
