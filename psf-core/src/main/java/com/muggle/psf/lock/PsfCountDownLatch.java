package com.muggle.psf.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/11/9
 **/
public interface PsfCountDownLatch {

    void await() throws InterruptedException;

    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    void countDown();

    long getCount();
}
