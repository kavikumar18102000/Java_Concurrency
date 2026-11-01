package Basic;

//Multiple threads use the same object(thread interference)
class Counter{
    private int counter;

    public Counter(int counter){
        this.counter = counter;
    }

    public void increment(){
        counter += 1;
    }

    public void decrement(){
        counter -= 1;
    }

    public void printCounter(){
        System.out.println(this.counter);
    }
}

public class ThreadInterference {
    static void main() {
        Counter counter = new Counter(10);
        Runnable taskIncrement = ()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            counter.increment();
            counter.printCounter();
        };
        Thread thread1 = new Thread(taskIncrement, "Thread 1");
        thread1.start();

        Runnable taskDecrement = ()->{
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            counter.decrement();
            counter.printCounter();
        };
        Thread thread2 = new Thread(taskDecrement, "Thread 2");
        thread2.start();
    }
}
