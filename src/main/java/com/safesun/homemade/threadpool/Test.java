package com.safesun.homemade.threadpool;

import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {
        Cat cat = new Cat();
        WeakReference<Cat> catWeakReference = new WeakReference<>(cat);
        cat = null;
        System.gc();
        if (catWeakReference.get() == null) {
            System.out.println("cat is gc");
        }
    }

    private static void exec() {
        Executor executor = new ThreadPoolExecutor(
                2,
                4,
                1,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1024)
        );
        executor.execute(() -> System.out.println("exec a task"));
    }
}

class Cat {

}