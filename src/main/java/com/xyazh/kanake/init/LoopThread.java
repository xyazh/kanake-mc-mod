package com.xyazh.kanake.init;

import com.xyazh.kanake.Kanake;

import java.util.concurrent.LinkedBlockingQueue;

public class LoopThread implements Runnable {
    public static LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    public static void creatThread(){
        Thread thread = new Thread(new LoopThread(), "kanake-loop");
        // 守护线程：避免服务器 stop / 客户端退出后 JVM 被这个死循环线程挂住
        thread.setDaemon(true);
        thread.start();
        //blockingQueue.add(()->{System.out.println(2333);});
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Runnable element = blockingQueue.take();
                element.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable e) {
                if (Kanake.logger != null) {
                    Kanake.logger.error("Error while running a kanake loop task", e);
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}

