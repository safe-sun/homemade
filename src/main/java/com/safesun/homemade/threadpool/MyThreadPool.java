package com.safesun.homemade.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {
    BlockingQueue<Runnable> workQueue;
    Queue<Runnable> supportCacheList = new LinkedList<>();

    private int corePoolSize;
    private int maxPoolSize;
    private int supportThreadAliveTime;
    private TimeUnit unit;

    private List<Thread> coreThreadList = new ArrayList<>();
    private List<Thread> supportThreadList = new ArrayList<>();

    public MyThreadPool(BlockingQueue<Runnable> workQueue, int coreThreadNum, int maxPoolSize, int supportThreadAliveTime, TimeUnit unit) {
        this.workQueue = workQueue;
        this.corePoolSize = coreThreadNum;
        this.maxPoolSize = maxPoolSize;
        this.supportThreadAliveTime = supportThreadAliveTime;
        this.unit = unit;
    }

    void execute(Runnable task) {
        if (coreThreadList.size() < corePoolSize) {
            Thread thread = new CoreThread();
            coreThreadList.add(thread);
            thread.start();
        }

        boolean offerFlag = workQueue.offer(task);

        // workqueue is full
        if (!offerFlag) {
            if (coreThreadList.size() + supportThreadList.size() < maxPoolSize) {
                Thread thread = new SupportThread();
                supportThreadList.add(thread);

                // after start, thread state from NEW to READY, and wait for cpu schedule
                thread.start();

                supportCacheList.add(task);
            } else {
                throw new RuntimeException("pool and queue all full");
            }
        }
    }

    class CoreThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = workQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    class SupportThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Runnable task = supportCacheList.poll();
                    if (task == null) {
                        task = workQueue.poll(supportThreadAliveTime, unit);
                        if (task == null) {
                            supportThreadList.remove(this);
                            break;
                        }
                    }
                    task.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
