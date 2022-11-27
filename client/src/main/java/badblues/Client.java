package badblues.client;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.Vector;
import java.util.LinkedList;
import javafx.util.Pair;
import java.lang.Exception;


public class Client {
    
    private static int counter = 0;
    private static long startTime;
    private static int threadNumber;
    private static Socket socket;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    private static Vector<ClientThread> threadVector = new Vector();
    private static Queue<Pair<Integer, String>> queue = new LinkedList<>();

    public static void main(String[] args) {
        threadNumber = Integer.parseInt(args[0]);
        try {
            socket = new Socket("localhost", 4999);
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        RecieveThread recieveThread = new RecieveThread();
        SendThread sendThread = new SendThread();
        recieveThread.start();
        sendThread.start();
        for (int i = 0; i < threadNumber; i++) {
            ClientThread thread = new ClientThread(i, queue);
            thread.start();
            threadVector.add(thread);
        }
        startTime = System.currentTimeMillis();
    }

    private static class RecieveThread extends Thread {

        //Поток приема ответов определяет по номеру в сообщении
        //поток, передавший запрос и пробуждает его. Ответное сообщени
        //находится в запросе, переданном потоком и выводится им

        @Override
        public void run() {
            while(true) {
                try {
                   Pair<Integer, String> pair = (Pair<Integer,String>)oin.readObject();
                   for (ClientThread thread : threadVector) {
                       if (thread.getThreadId() == pair.getKey()) {
                           thread.setPrintMode(true);
                           thread.setMessage(pair.getValue());
                           thread.unpause();
                           break;
                       }
                   }
                   counter++;
                   if (counter == threadNumber) {
                       System.out.println("Program working time: " + (System.currentTimeMillis() - startTime) + "ms");
                   }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

    } 

    private static class SendThread extends Thread {

        //Поток передачи от клиента выбирает сообщения из
        //очереди и передает в соединение

        @Override
        public void run() {
            while (true) {
               try {
                    synchronized(queue) {
                        if (!queue.isEmpty()) {
                            System.out.println("message: " + queue.peek());
                            oout.writeObject(queue.poll());
                        }
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                } 
            }   
        }
    }
}