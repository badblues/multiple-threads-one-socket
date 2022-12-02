package badblues.client;

import java.util.Queue;
import java.util.Vector;
import javafx.util.Pair;
import java.util.Random;

class ClientThread extends Thread {

    int id;
    String message;
    boolean printMode = false;
    Queue<Pair<Integer, String>> queue;
    Vector<Long> performance;
    long startTime;

    protected volatile boolean paused = false;
    protected final Object pauseLock = new Object();

    public ClientThread(int id, Queue<Pair<Integer, String>> queue, Vector<Long> performance) {
        this.id = id;
        this.queue = queue;
        this.performance = performance;
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
                System.out.println("GOT RESPONSE: " + id + "=" + message);
                synchronized(performance) {
                    performance.add(System.currentTimeMillis() - startTime);
                }
                break;
            }
            generateWord(5);
            Pair<Integer, String> pair = new Pair(id, message);
            synchronized(queue) {
                queue.add(pair);
            }
            startTime = System.currentTimeMillis();
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