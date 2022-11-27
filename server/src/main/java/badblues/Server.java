package badblues.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(4999);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                assert serverSocket != null : "Not connected";
                socket = serverSocket.accept();
                System.out.println("CLient accepted!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}