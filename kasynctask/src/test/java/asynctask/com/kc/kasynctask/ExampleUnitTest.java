package asynctask.com.kc.kasynctask;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testExecutor() {
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(5);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, queue);

        final int len = 11;
        final boolean[] heheda = new boolean[len];

        for (int i=0;  i < len; i++) {
            final int finalI = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(new Date()  + "     heheda" + toString());

                    heheda[finalI] = true;
                    boolean success = true;
                    for (boolean hehe : heheda) {
                        if (!hehe) {
                            success = false;
                            break;
                        }
                    }
                    if (success) {
                        synchronized (ExampleUnitTest.class) {
                            ExampleUnitTest.class.notifyAll();
                        }
                    }
                }
            });

        }

        synchronized (ExampleUnitTest.class) {
            try {
                ExampleUnitTest.class.wait(1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}