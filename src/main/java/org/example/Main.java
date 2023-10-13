package org.example;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(2000);
        int read = 0;
        try(Socket c = s.accept()) {
            try(InputStream fis = (c.getInputStream());
                OutputStream fos =(c.getOutputStream())){
                byte[] data = new byte[256];
                fis.read(data);
                String fileName = new String(data, data.length).trim();
                System.out.println("The client is downloading " + new String(data, data.length).trim());
                data = "Ok! Send me the chunk size!".getBytes();
                fos.flush();
                fos.write(data);

                //we receive the size of the chunk
                data = new byte[256];
                read = fis.read(data);
                Integer chunkSize = Integer.valueOf( new String(data).trim());
                System.out.println(chunkSize +  " is the size of the chunk!");

                InputStream image = new FileInputStream("./files/" + fileName);
                //We send the number of chunks that will be sent
                byte[] imageData = image.readAllBytes();
                System.out.println(imageData.length);
                Integer numberOfChunks = (imageData.length/chunkSize) + 1;
                data = String.valueOf(numberOfChunks).getBytes();
                fos.write(data);
                fos.flush();

                //We start sending the file that the client wants in chunks
                for (Integer i = 1; i < numberOfChunks; i++) {
                    byte[] x = new byte[chunkSize];
                    x = Arrays.copyOfRange(imageData, (i-1)  * chunkSize, i * chunkSize);
                    fos.write(x);
                    fos.flush();
                }
            }
        }
    }
}