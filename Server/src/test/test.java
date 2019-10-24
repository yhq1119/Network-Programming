package test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class test {
    ServerSocket serverSocket = null;
    @Test
    void test(){


        try {
             serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){

            try {
                Socket temp = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("?????");
        }


    }

}
