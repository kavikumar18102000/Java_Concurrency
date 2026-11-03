package Basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedVariable {
    public static void main(String[] args) {
        AtomicInteger a = new AtomicInteger(10);
        Runnable task1 = ()-> System.out.println(Thread.currentThread().getName()+" : "+a.incrementAndGet());
        
        Runnable task2 = ()-> System.out.println(Thread.currentThread().getName()+" : "+a.incrementAndGet());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.execute(task1);
        executorService.execute(task2);
        executorService.shutdown();
    }
}
