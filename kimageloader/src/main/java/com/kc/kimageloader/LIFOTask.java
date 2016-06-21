package com.kc.kimageloader;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 和优先队列一起使用;  当增加一个item时,将优先级增加1
 */
public class LIFOTask extends FutureTask<Object> implements Comparable<LIFOTask> {

    private static long counter = 0;

    private final long priority;

    public LIFOTask(Runnable runnable) {
        super(runnable, new Object());
        priority = counter++;
    }

    public long getPriority() {
        return priority;
    }

    @Override
    public int compareTo(LIFOTask another) {
        return priority > another.getPriority() ? -1 : 1;
    }
}
