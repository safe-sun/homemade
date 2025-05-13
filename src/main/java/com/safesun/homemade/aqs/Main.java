package com.safesun.homemade.aqs;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int count = 1000;

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0;i < 1000;i++) {
            Thread thread = new Thread(() -> {
                try {

                    Thread.sleep(2);
                    count--;

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
        }

        for (var t : threads) {
            t.start();

        }
        for (var t : threads) {
            t.join();
        }
        System.out.println(count);
    }
}
