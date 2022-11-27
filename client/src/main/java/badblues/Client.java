package badblues.client;

import java.io.IOException;
import java.net.Socket;


public class Client {
    
    private static Socket socket;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 4999);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}