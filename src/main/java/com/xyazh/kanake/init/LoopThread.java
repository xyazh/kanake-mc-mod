package com.xyazh.kanake.init;

import java.util.concurrent.LinkedBlockingQueue;

public class LoopThread implements Runnable {
    public static LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    public static void creatThread(){
        Thread thread = new Thread(new LoopThread());
        thread.start();
        //blockingQueue.add(()->{System.out.println(2333);});
    }

    public void run() {
        while (true){
            try {
                Runnable element = blockingQueue.take();
                element.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

