package asynctask.com.kc.kasynctask;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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


    @Test
    public void testFunture() {
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(5);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, queue);

        Callable<Integer> heheda = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("1 ");
                Thread.sleep(3000);
                System.out.println("2 ");
                return 3;
            }
        };

        Future<Integer> future = executor.submit(heheda);



        for (int i=0; i< 5; i++) {
            try {
                System.out.println("3 ");
                System.out.println("1 " + future.get());
                System.out.println("4 ");
            } catch (Exception e) {
                System.out.println(e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }




    FutureTask<Integer> task  = null;
    @Test
    public void testFuntureTask() {
        BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>(5);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 0, TimeUnit.SECONDS, queue);

        Callable<Integer> heheda = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("1 ");
                Thread.sleep(3000);
                System.out.println("2 ");
                return 3;
            }
        };

        task = new FutureTask<Integer>(heheda){
            @Override
            protected void done() {
                for (int i=0; i< 5; i++) {
                    try {
                        System.out.println("3 ");
                    System.out.println("1 " + task.get());
                        System.out.println("4 ");
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

       executor.execute(task);



        for (int i=0; i< 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}