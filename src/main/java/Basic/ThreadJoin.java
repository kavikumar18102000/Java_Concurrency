package Basic;

public class ThreadJoin {
    static void main() throws InterruptedException {
        Thread thread1 = new Thread(()-> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread 1....");
        });
        Thread thread2 = new Thread(()-> System.out.println("Thread 2...."));
        thread1.start();
        thread1.join();
        thread2.start();
    }
}
