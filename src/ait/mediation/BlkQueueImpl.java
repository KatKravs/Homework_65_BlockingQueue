package ait.mediation;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueueImpl<T> implements BlkQueue<T> {
    private LinkedList<T> queue = new LinkedList<>();
    private int maxSize;
    Lock mutex = new ReentrantLock();
    Condition notFull = mutex.newCondition();
    Condition notEmpty = mutex.newCondition();

    public BlkQueueImpl(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void push(T message) {
        mutex.lock();
        try {
            while (this.queue.size() == maxSize) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(message);
            notEmpty.signal();
        } finally {
            mutex.unlock();
        }
    }

    @Override
    public T pop() {
        mutex.lock();
        try {
            while (this.queue.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
           T messege = queue.removeFirst();
            notFull.signal();
            return messege;
        } finally {
            mutex.unlock();
        }
    }
}
