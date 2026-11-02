package Lesson3.Exercise;

import java.util.List;

public class RunnableVsThread {
    public static void main(String[] args) {
        Thread numeric = new Thread(()->{
            for (int i = 1; i <= 5; i++) {
                System.out.println(i);
            }
        });
        
        Thread alphabets = new Thread(()->{
            List<Character> charList = List.of('A', 'B', 'C', 'D', 'E');
            charList.forEach(System.out::println);
        });
        
        numeric.start();
        alphabets.start();
    }
}
