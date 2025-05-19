package com.safesun.homemade.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(
                new ArrayBlockingQueue<>(8),
                2,
                4,
                30,
                TimeUnit.SECONDS
        );

        try {
            for (int i = 0;i < 15;i++) {
                final int fi = i;
                pool.execute(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(Thread.currentThread().getName() + " done task " + fi);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Thread-main done");
    }
}
