package com.kc.kimageloader;

import android.app.Activity;

/**
 * 当加载图片时,这个类封装了完成,一些任意的长时间运行的工作它处理的东西就像使用内存和硬盘
 * 高速缓存，运行工作在后台线程和设定的占位符图片。
 */
public abstract class ImageWorker {

    private static final String TAG = "ImageWorker";

    private Activity mActivity;




    protected ImageWorker(Activity activity) {
        mActivity = activity;
    }



}


