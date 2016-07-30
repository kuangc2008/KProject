package com.kc.kimageloader;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.Image;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * 当加载图片时,这个类封装了完成,一些任意的长时间运行的工作它处理的东西就像使用内存和硬盘
 * 高速缓存，运行工作在后台线程和设定的占位符图片。
 */
public abstract class ImageWorker {

    private static final String TAG = "ImageWorker";
    protected ImageCache mImageCache;

    protected Activity mActivity;

    private volatile boolean mExitTasksEarly = false;
    private boolean mFadeInBitmap = true;
    private Bitmap mLoadingBitmap;




    protected ImageWorker(Activity activity) {
        mActivity = activity;
    }



    public void loadImage(Object data, ImageView imageView) {
        Bitmap bitmap = null;

        if (mImageCache != null) {
            bitmap =  mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(data, imageView)) {
            final BitmapWorkerTask task = makeTask(imageView, data);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mActivity.getResources(), mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            NetworkThreadPool.submitTask(task);
        }
    }


    /**
     * 如果当前任务已被取消,或没有开始,则return true;   如果正在下载,则return false
     * @param data
     * @param imageView
     * @return
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {

        return false;

    }

    private BitmapWorkerTask makeTask(ImageView imageView, Object data) {
        BitmapWorkerRunnable runnable = new BitmapWorkerRunnable(imageView, data);
        BitmapWorkerTask task = new BitmapWorkerTask(runnable, data);
        runnable.setParentTask(task);
        return task;
    }

    private class BitmapWorkerTask extends LIFOTask {
        Object data;

        public BitmapWorkerTask(BitmapWorkerRunnable runnable, Object data) {
            super(runnable);
            this.data = data;
        }

        public boolean isSameData(Object data) {
            return this.data.equals(data);
        }
    }


    private class BitmapWorkerRunnable implements Runnable {
        private Object data;
        private final WeakReference<ImageView> imageVIewReference;
        private BitmapWorkerTask parentTask;

        private BitmapWorkerRunnable(ImageView imageView, Object data) {
            imageVIewReference = new WeakReference<ImageView>(imageView);
            this.data = data;
        }

        public BitmapWorkerTask getParentTask() {
            return parentTask;
        }

        public void setParentTask(BitmapWorkerTask parentTask) {
            this.parentTask = parentTask;
        }

        private boolean isCancelled() {
            return parentTask == null || parentTask.isCancelled();
        }

        @Override
        public void run() {
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            //TODO
            if (mImageCache != null && !isCancelled()
                    && getAttachedImageView() != null && !mExitTasksEarly) {
                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            }

            // imagecache可用;   task没有被另一个线程cancel
            // imageview与task是相匹配的;    退出flag也没设置
            if (bitmap == null && !isCancelled() && getAttachedImageView() != null && !mExitTasksEarly) {
                bitmap = processBitmap(data);
            }

            // bitmap被处理, cache可用, 则添加到cache中
            if (bitmap != null && mImageCache != null) {
                mImageCache.addBitmapToCache(dataString, bitmap);
            }

            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            if (bitmap != null) {
                BitmapSetter runnable;
                if (mFadeInBitmap) {
                    runnable = new FadeInBitmapSetter(data, bitmap, getAttachedImageView(), mLoadingBitmap);
                } else {
                    runnable = new BitmapSetter(data, bitmap, getAttachedImageView());
                }

                mActivity.runOnUiThread(runnable);
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = imageVIewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (getParentTask() == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    protected abstract Bitmap processBitmap(Object data);


    static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }




    private static class AsyncDrawable extends BitmapDrawable {
        private BitmapWorkerTask bitmapWorkerTask;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            this.bitmapWorkerTask = bitmapWorkerTask;
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTask;
        }
    }



    private static class BitmapSetter implements Runnable {
        protected Object data;
        protected Bitmap bitmap;
        protected ImageView imageView;

        private BitmapSetter(Object data, Bitmap bitmap, ImageView imageView) {
            this.data = data;
            this.bitmap = bitmap;
            this.imageView = imageView;
        }

        @Override
        public void run() {
            if (imageView != null && bitmap != null && data != null) {
                if (isValidData(data, imageView)) {
                    setImageBitmap();
                }
            }
            imageView = null;
            bitmap = null;
            data = null;
        }

        protected void setImageBitmap() {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static boolean isValidData(Object data, ImageView imageView) {
        BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        return bitmapWorkerTask != null && bitmapWorkerTask.isSameData(data);
    }

    private static class FadeInBitmapSetter extends BitmapSetter {
        private static final int FADE_IN_TIME = 200;
        protected Bitmap loadingBitmap;

        private FadeInBitmapSetter(Object data, Bitmap bitmap, ImageView imageView, Bitmap loadingBitmap) {
            super(data, bitmap, imageView);
            this.loadingBitmap = loadingBitmap;
        }

        @Override
        protected void setImageBitmap() {
            Resources resources = imageView.getContext().getResources();

            TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                    new ColorDrawable(android.R.color.transparent),
                    new BitmapDrawable(resources, bitmap)
            });

            imageView.setBackgroundDrawable(new BitmapDrawable(resources, loadingBitmap));
            imageView.setImageDrawable(td);

            td.startTransition(FADE_IN_TIME);
        }
    }
}


