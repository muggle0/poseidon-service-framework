package com.muggle.psf.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @Author: muggle
 * @Date: 2020/7/28
 **/
public class NamedThreadFactory implements ThreadFactory {
    private final String prefix;
    private final AtomicLong threadIds;

    NamedThreadFactory(final String prefix) {
        this.prefix = prefix;
        this.threadIds = new AtomicLong();
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(r, this.prefix + this.threadIds.incrementAndGet());
        t.setDaemon(false);
        return t;
    }
}
