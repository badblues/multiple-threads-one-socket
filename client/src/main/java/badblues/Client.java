package badblues.client;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.HashMap;
import java.util.Vector;
import java.util.LinkedList;
import javafx.util.Pair;
import java.lang.Exception;



public class Client {
    
    private static int responseCounter = 0;
    private static int threadNumber;
    private static Socket socket;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    private static HashMap<Integer, ClientThread> threads = new HashMap<>();
    private static Queue<Pair<Integer, String>> queue = new LinkedList<>();
    private static Vector<Long> performance = new Vector<>();

    public static void main(String[] args) {
        threadNumber = Integer.parseInt(args[0]);
        try {
            socket = new Socket("localhost", 4999);
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        RecievingThread recievingThread = new RecievingThread();
        SendingThread sendingThread = new SendingThread();
        recievingThread.start();
        sendingThread.start();
        synchronized(threads) {
            for (int i = 0; i < threadNumber; i++) {
                ClientThread thread = new ClientThread(i, queue, performance);
                thread.start();
                threads.put(i, thread);
            }
        }
    }

    private static class RecievingThread extends Thread {
        //Поток приема ответов определяет по номеру в сообщении
        //поток, передавший запрос и пробуждает его. Ответное сообщени
        //находится в запросе, переданном потоком и выводится им
        @Override
        public void run() {
            while(true) {
                try {
                    Pair<Integer, String> pair = (Pair<Integer,String>)oin.readObject();
                    synchronized(threads) {
                        ClientThread thread = threads.get(pair.getKey());
                        thread.setPrintMode(true);
                        thread.setMessage(pair.getValue());
                        thread.unpause();
                    }
                    responseCounter++;
                    if (responseCounter == threadNumber) {
                        synchronized(performance){
                            double sum = 0;
                            for (Long n : performance)
                                sum += (double)n;
                            sum /= (double)performance.size();
                            System.out.println("AVERAGE TIME: " + sum + "ms");
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    } 

    private static class SendingThread extends Thread {
        //Поток передачи от клиента выбирает сообщения из
        //очереди и передает в соединение
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized(queue) {
                        if (!queue.isEmpty()) {
                            System.out.println("SENT REQUEST: " + queue.peek());
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