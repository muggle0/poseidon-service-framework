package com.muggle.psf.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/11/9
 **/
public interface PsfSemaphore {

    void acquire() throws InterruptedException;

    void acquireUninterruptibly();

    boolean tryAcquire();

    boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException;

    void release();

    void acquire(int permits) throws InterruptedException;

    void acquireUninterruptibly(int permits);

    boolean tryAcquire(int permits);

    boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException;

    void release(int permits);

    int availablePermits();

    int drainPermits();

    boolean isFair();
}
