package com.kc.kimageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * 当加载图片时,这个类封装了完成,一些任意的长时间运行的工作它处理的东西就像使用内存和硬盘
 * 高速缓存，运行工作在后台线程和设定的占位符图片。
 */
public abstract class ImageWorker {

    private static final String TAG = "ImageWorker";
    protected ImageCache mImageCache;

    private Activity mActivity;




    protected ImageWorker(Activity activity) {
        mActivity = activity;
    }



    public void loadImage(Object data, ImageView imageView) {
        Bitmap bitmap = null;

        //TODO:

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(data, imageView)) {

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

    private class BitmapWorkerTask extends LIFOTask {

        public BitmapWorkerTask(Runnable runnable) {
            super(runnable);
        }
    }


    private class BitmapWorkerRunnable implements Runnable {
        private Object data;
//        private final WeakReference<ImageView> imageVIewReference;
        private BitmapWorkerTask parentTask;

        @Override
        public void run() {

        }
    }
}


