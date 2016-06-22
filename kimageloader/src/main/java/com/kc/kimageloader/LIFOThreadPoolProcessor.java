package com.kc.kimageloader;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengkuang on 16/6/22.
 */
public class LIFOThreadPoolProcessor {
    private BlockingQueue<Runnable> opsToRun = new PriorityBlockingQueue<>(64, new Comparator<Runnable>() {
        @Override
        public int compare(Runnable lhs, Runnable rhs) {
            if (lhs instanceof LIFOTask && rhs instanceof LIFOTask) {
                LIFOTask l0 = (LIFOTask) lhs;
                LIFOTask l1 = (LIFOTask) rhs;
                return l0.compareTo(l1);
            }
            return 0;
        }
    });


    private ThreadPoolExecutor executor;

    public LIFOThreadPoolProcessor(int threadCount) {
        executor = new ThreadPoolExecutor(threadCount, threadCount, 0, TimeUnit.SECONDS, opsToRun);
    }

    public Future<?> submitTask(LIFOTask task) {
        return executor.submit(task);
    }

    public void clear() {
        executor.purge();
    }
}
