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
                RecieveThread recieveThread = new RecieveThread();
                SendThread sendThread = new SendThread();
                recieveThread.run();
                sendThread.run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class RecieveThread extends Thread {

        //поток приема на сервере, приняв очередное сообщение, запускает поток
        //исполнения запроса, по завершении которого в очередь ответов ставить ответ
        //в котором сохраняется порядковый номер запроса

        @Override
        public void run() {
            System.out.println("HELLO FROM RECIEVE THREAD");
            if (din.available() > 0) {
                    int i = din.readInt();
                }
        }

    } 

    private static class SendThread extends Thread {

        //поток передачи ответов на сервере выбирает сообщения из очереди и передает в соединение

        @Override
        public void run() {
            System.out.println("HELLO FROM SEND THREAD");
        }
    }

}