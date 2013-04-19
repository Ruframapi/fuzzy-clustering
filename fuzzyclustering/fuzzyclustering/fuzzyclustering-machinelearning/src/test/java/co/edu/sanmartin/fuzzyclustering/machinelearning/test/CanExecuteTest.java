package co.edu.sanmartin.fuzzyclustering.machinelearning.test;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class CanExecuteTest {

    private static int count = 1024; //10 MB

    public static void main(String[] args) throws Exception {
        RandomAccessFile memoryMappedFile = new RandomAccessFile("d:\\temp\\largeFile.txt", "rw");
       
        //Mapping a file into memory
        MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, count);
      
        //Writing into Memory Mapped File
        for (int i = 0; i < count; i++) {
            out.put((byte) 'A');
        }
        System.out.println("Writing to Memory Mapped File is completed");
      
        //reading from memory file in Java
        for (int i = 0; i < 10 ; i++) {
            System.out.print((char) out.get(i));
        }
        System.out.println("Reading from Memory Mapped File is completed");
    }
}


//Read more: http://javarevisited.blogspot.com/2012/01/memorymapped-file-and-io-in-java.html#ixzz2PwKGjBNB