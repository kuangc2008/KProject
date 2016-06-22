package com.kc.kimageloader;

import java.util.concurrent.Future;

/**
 * Created by chengkuang on 16/6/22.
 */
public class NetworkThreadPool {

    private static LIFOThreadPoolProcessor pool = new LIFOThreadPoolProcessor(3);

    public static Future<?> submitTask(LIFOTask task) {
        return pool.submitTask(task);
    }
}
