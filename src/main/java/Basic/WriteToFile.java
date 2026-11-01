package Basic;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class WriteToFile {
    static void main() {
        
        Thread thread1 = new Thread(()->{
            try {
                writeToFile("Thread 1.", 1);
                System.out.println("File written successfully.");
            } catch (Exception e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        });
        
        
        Thread thread2 = new Thread(()->{
            try {
                writeToFile("Thread 2.");
                System.out.println("File written successfully.");
            } catch (Exception e) {
                System.out.println("An error occurred while writing to the file.");
                e.printStackTrace();
            }
        });
        
        thread1.start();
        thread2.start();
    }
    
    /* static void writeToFile(String text){
        try(FileChannel channel = FileChannel.open(
                "src/main/resources/basic.txt",
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING){
            synchronized (writer) {
                channel.write(text, 1);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    } */
    
}
