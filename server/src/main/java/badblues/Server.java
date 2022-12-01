package badblues.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javafx.util.Pair;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Exception;

public class Server {

    private static int threadNumber;
    private static ObjectInputStream oin;
    private static ObjectOutputStream oout;
    private static Queue<Pair<Integer, String>> queue = new LinkedList<>();
    private static Vector<ServerThread> threadVector = new Vector();

    public static void main(String[] args) {
        threadNumber = Integer.parseInt(args[0]);
        int lower_gap = Integer.parseInt(args[1]);
        int higher_gap = Integer.parseInt(args[2]);
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(4999);
            assert serverSocket != null : "Not connected";
            socket = serverSocket.accept();
            System.out.println("CLient accepted!");
            oout = new ObjectOutputStream(socket.getOutputStream());
            oin = new ObjectInputStream(socket.getInputStream());
            RecievingThread recievingThread = new RecievingThread();
            SendingThread sendingThread = new SendingThread();
            recievingThread.start();
            sendingThread.start();
            synchronized(threadVector) {
                for (int i = 0; i < threadNumber; i++) {
                    ServerThread thread = new ServerThread(i, queue, lower_gap, higher_gap);
                    threadVector.add(thread);
                }  
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class RecievingThread extends Thread {

        //поток приема на сервере, приняв очередное сообщение, запускает поток
        //исполнения запроса, по завершении которого в очередь ответов ставить ответ
        //в котором сохраняется порядковый номер запроса

        @Override
        public void run() {
            while(true) {
                try {
                    Pair<Integer, String> pair = (Pair<Integer,String>)oin.readObject();
                    synchronized(threadVector) {
                        for (ServerThread thread : threadVector) {
                            if (thread.getThreadId() == pair.getKey()) {
                                thread.setPair(pair);
                                thread.start();
                                break;
                            }
                        }
                    }
                } catch(Exception e) {
                    //e.printStackTrace();
                }
            }
        }

    } 

    private static class SendingThread extends Thread {

        //поток передачи ответов на сервере выбирает сообщения из очереди и передает в соединение

        @Override
        public void run() {
            while(true){
                try {
                    synchronized(queue) {
                        if (!queue.isEmpty()) {
                            System.out.println("SENT RESPONSE: " + queue.peek());
                            oout.writeObject(queue.poll());
                        }
                    }
                } catch(IOException e) {
                    //e.printStackTrace();
                }
            }
        }
    }

}