package badblues.client;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.LinkedList;
import javafx.util.Pair;
import java.lang.Exception;


public class Client {
    
    private static Socket socket;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    private static Vector<ClientThread> threadVector = new Vector();
    private static Queue<Pair<Integer, String>> queue = new LinkedList<>();

    public static void main(String[] args) {
        try {
            socket = new Socket("localhost", 4999);
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("connected and made oin, oout");
        RecieveThread recieveThread = new RecieveThread();
        SendThread sendThread = new SendThread();
        recieveThread.start();
        sendThread.start();
        for (int i = 0; i < 10; i++) {
            ClientThread thread = new ClientThread(i);
            thread.start();
            threadVector.add(thread);
        }
    }

    private static class RecieveThread extends Thread {

        //Поток приема ответов определяет по номеру в сообщении
        //поток, передавший запрос и пробуждает его. Ответное сообщени
        //находится в запросе, переданном потоком и выводится им

        @Override
        public void run() {
            System.out.println("HELLO FROM RECIEVE THREAD");
            while(true) {
                try {
                   Pair<Integer, String> pair = (Pair<Integer,String>)oin.readObject();
                   System.out.println("got something");
                   for (ClientThread thread : threadVector) {
                       if (thread.getThreadId() == pair.getKey()) {
                           thread.setPrintMode(true);
                           thread.setMessage(pair.getValue());
                           thread.unpause();
                           break;
                       }
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
            System.out.println("HELLO FROM SEND THREAD");
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

    private static class ClientThread extends Thread {

        int id;
        String message;
        boolean printMode = false;

        protected volatile boolean paused = false;
	    protected final Object pauseLock = new Object();

        public ClientThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while(true) {
                synchronized (pauseLock) {
                    if (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
                if (printMode) {
                    System.out.println(message);
                    break;
                }
                generateWord(5);
                Pair<Integer, String> pair = new Pair(id, message);
                synchronized(queue) {
                    queue.add(pair);
                }
                pause();
            }
        }

        public void generateWord(int length) {
            Random random = new Random();
            char[] word = new char[length];
            for(int j = 0; j < length; j++) {
                word[j] = (char)('a' + random.nextInt(26));
            }
            message = new String(word);
        }

        public void pause() {
            paused = true;
        }

        public void unpause() {
            synchronized (pauseLock) {
                paused = false;
                pauseLock.notifyAll();
            }
        }

        public int getThreadId() {
            return id;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setPrintMode(boolean flag) {
            this.printMode = flag;
        }

    }
}