package asynctask.com.kc.kasynctask;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * modified android.os.AsyncTask
 */
public abstract class KAsyncTask<Params, Progress, Result> {
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();


    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

    private static final int KEEP_ALIVE = 1;
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "KAsyncTask #" + mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(10);

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;

    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
        Runnable mActive;

        @Override
        public void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });

            if (mActive == null) {
                scheduleNext();
            }
        }

        private synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }



    private static final InternalHandler sHandler = new InternalHandler(Looper.getMainLooper());

    private volatile Status mStatus = Status.PENDING;

    public enum Status {
        PENDING,
        RUNNING,
        FINISHED,
    }



    public KAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            @Override
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return postResult(doInBackground(mParams));
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()",
                            e.getCause());
                } catch(CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };

    }

    protected void onPreExecute() {}
    protected abstract Result doInBackground(Params... params);
    protected void onProgressUpdate(Progress... values) {
    }
    protected void onPostExecute(Result result) {
    }

    private Result postResult(Result result) {
        Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    public final KAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    public final KAsyncTask<Params, Progress, Result> executeParallel(Params... params) {
        return executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    public final KAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute();

        mWorker.mParams = params;

        try {
            exec.execute(mFuture);
        } catch (RejectedExecutionException e) {
            // report this exception to crash backend
        }

        return this;
    }



    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }


    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    private static class AsyncTaskResult<Data> {
        final KAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(KAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }


    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS,
                    new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    protected void onCancelled(Result result) {
        onCancelled();
    }

    protected void onCancelled() {
    }


    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }


    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }


    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }
}




