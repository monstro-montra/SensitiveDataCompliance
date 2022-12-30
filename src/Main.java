import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Main {
    public static void main(String[] args) {
        System.out.println("Non-Compliant file read:");
        try{
            readData();
        } catch (IOException er){
            System.out.println("Error");
        }

        System.out.println("\nCompliant file read using NIO:");
        readData2();

        System.out.println("\n\nCompliant file read using NIO, example 2:");
        readData3();
    }

    //non-compliant example
    static void readData() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader (new FileInputStream("src/file.txt"))); //instantiate new buffered reader
        String data = br.readLine(); //readLine() method will return sensitive data as string object. may persist after data is no longer needed.
        System.out.println(data);
    }

    //compliant example
    static void readData2(){
        long bufferSize;
        FileInputStream fileInput;
        FileChannel fileChannel;
        ByteBuffer buff;

        try{
            fileInput = new FileInputStream("src/file.txt"); //file input stream directed at src/file.txt
            fileChannel = fileInput.getChannel();
            bufferSize = fileChannel.size(); //set the buffer size to the size of the fileChannel
            buff = ByteBuffer.allocateDirect((int)bufferSize); //allocate bufferSize for buff
            fileChannel.read(buff); //read buff pin fileChannel
            buff.rewind(); //set position to 0
            for (int i = 0; i < bufferSize; i++){
                System.out.print((char)buff.get());
            }
            fileChannel.close();
            fileInput.close();
        } catch (Throwable e){
            System.out.println("Error");
        }
    }

    static void readData3() {
        int bufferSize = 16 * 1024; //set the buffer size
        byte[] zeroes = new byte[bufferSize]; //create an array of bytes of size bufferSize
        ByteBuffer buff = ByteBuffer.allocateDirect(bufferSize);
        try (FileChannel reader = (new FileInputStream("src/file.txt")).getChannel()) {
            while (reader.read(buff) > 0) {
                buff.rewind();
                for (int i = 0; i < bufferSize; i++){
                    System.out.print((char)buff.get());
                }
                buff.clear();
                buff.put(zeroes); // overwrite buffer with zeroes
                buff.clear();
            }
        } catch (Throwable e) {
            // Handle error
        }
    }
}