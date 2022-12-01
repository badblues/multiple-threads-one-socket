package badblues.server;

import java.io.*;
import javafx.util.Pair;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Queue;
import java.lang.Exception;


 class ServerThread extends Thread {

    Queue<Pair<Integer, String>> queue;
    int lower_gap = 0;
    int higher_gap = 0;
    int id;
    Pair<Integer, String> pair;

    public ServerThread(int id, Queue<Pair<Integer, String>> queue, int low, int high) {
        this.id = id;
        this.queue = queue;
        this.lower_gap = low;
        this.higher_gap = high;
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
            TimeUnit.MILLISECONDS.sleep(random.nextInt() % (higher_gap - lower_gap) + lower_gap);
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