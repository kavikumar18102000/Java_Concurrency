package Basic;

public class SimpleThreads {
    static void threadMessage(String message){
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop implements Runnable{

        @Override
        public void run() {
            String message = "How are you Arya....";
            try {
                Thread.sleep(2000);
                threadMessage(message);
            }
            catch (InterruptedException e){
                threadMessage("I wasn't done...");
            }
        }
    }

    static void main() {
        Thread thread = new Thread(new MessageLoop(), "Custom Thread - 1");
        thread.start();
    }
}
