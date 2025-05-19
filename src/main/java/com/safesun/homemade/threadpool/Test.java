package com.safesun.homemade.threadpool;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0;i < 100000000;i++) {
                //Thread.sleep(500);
                System.out.println(i);
            }
        });
        thread.start();
        thread.interrupt();
    }
}
