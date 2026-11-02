package Lesson3.Exercise;

import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Exercise4 {
    public static void main(String[] args) {
        Callable<Integer> callable = ()->{
            Scanner sc  = new Scanner(System.in);
            System.out.println("Enter A: ");
            int a = sc.nextInt();
            System.out.println("Enter B: ");
            int b = sc.nextInt();
            return (a + b) ;
            
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(callable);
        try {
            
        Integer res = future.get();
        System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            executorService.shutdown();
        }
    }
}
