package Basic;

public class ThreadPause {
    static void main() throws InterruptedException{
        String[] fruits = {"Apple", "Orange", "Guava", "Pineapple"};
        for(String f :fruits){
            Thread.sleep(4000); //pause for 4 seconds
            System.out.println(f);
        }
    }
}
