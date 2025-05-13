package com.safesun.homemade.aqs;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class MyLock {
    AtomicBoolean lockedFlag = new AtomicBoolean(false);

    Thread lockOwner = null;

    // the pointer to head and tail, at first they are in same place -- dummy
    AtomicReference<Node> headPointer = new AtomicReference<>(new Node());
    AtomicReference<Node> tailPointer = new AtomicReference<>(headPointer.get());

    void lock() {
        if (lockedFlag.compareAndSet(false, true)) {
            lockOwner = Thread.currentThread();
            return;
        }

        Node current = new Node();
        current.thread = Thread.currentThread();
        while (true) {
            Node curTail = tailPointer.get();
            if (tailPointer.compareAndSet(curTail, current)) {
                current.pre = curTail;
                curTail.next = current;
                break;
            }
        }

        // avoid spurious wakeup
        while (true) {
            // already insert as tail of linked array, try once, then block here wait for lock owner`s unpark
            if (current.pre == headPointer.get() && lockedFlag.compareAndSet(false, true)) {
                lockOwner = Thread.currentThread();
                headPointer.set(current);
                current.pre.next = null;
                current.pre = null;
                return;
            }
            LockSupport.park();
        }

    }

    void unlock() {
        if (lockOwner != Thread.currentThread()) {
            throw new IllegalStateException("current thread do not own the lock");
        }
        Node head = headPointer.get();
        Node next = head.next;
        lockedFlag.set(false);
        if (next != null) {
            LockSupport.unpark(next.thread);
        }
    }

    class Node {
        Node pre;
        Node next;
        Thread thread;
    }
}
