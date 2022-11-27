package badblues.client;

import java.io.*;
import java.net.Socket;


public class Client {
    
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 4999);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        RecieveThread recieveThread = new RecieveThread();
        SendThread sendThread = new SendThread();
        recieveThread.run();
        sendThread.run();
    }

    private static class RecieveThread extends Thread {

        //Поток приема ответов определяет по номеру в сообщении
        //поток, передавший запрос и пробуждает его. Ответное сообщени
        //находится в запросе, переданном потоком и выводится им

        @Override
        public void run() {
            System.out.println("HELLO FROM RECIEVE THREAD");
        }

    } 

    private static class SendThread extends Thread {

        //Поток передачи от клиента выбирает сообщения из
        //очереди и передает в соединение

        @Override
        public void run() {
            System.out.println("HELLO FROM SEND THREAD");
        }
    }


}