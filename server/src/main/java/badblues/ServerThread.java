package badblues.server;

import java.io.*;
import javafx.util.Pair;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Queue;
import java.lang.Exception;


 class ServerThread extends Thread {

    Queue<Pair<Integer, String>> queue;
    int id;
    Pair<Integer, String> pair;

    public ServerThread(int id, Queue<Pair<Integer, String>> queue) {
        this.queue = queue;
        this.id = id;
    } 

    @Override
    public void run() {
        System.out.println("HELLO FROM SERVER THREAD #" + id);
        String newWord = reverseWord(pair.getValue());
        pair = new Pair<Integer, String>(id, newWord);
        synchronized(queue) {
            queue.add(pair);
        }
    }

    private String reverseWord(String word) {
        try {
            Random random = new Random();
            TimeUnit.MILLISECONDS.sleep(random.nextInt() % 5000);
        } catch(Exception e) {
            e.printStackTrace();
        }
        String newWord = "";
        char ch;
        for (int i=0; i<word.length(); i++) {
            ch = word.charAt(i);
            newWord = ch + newWord;
        }
        return newWord;
    }

    public int getThreadId() {
        return id;
    }

    public void setPair(Pair<Integer, String> pair) {
        this.pair = pair;
    }
}