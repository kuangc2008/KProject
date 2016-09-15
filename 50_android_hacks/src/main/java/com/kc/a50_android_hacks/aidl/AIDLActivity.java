package com.kc.a50_android_hacks.aidl;

import android.app.Activity;
import android.os.IBinder;

import com.kc.a50_android_hacks.BinderPool;

/**
 * Created by chengkuang on 16/9/15.
 */
public class AIDLActivity extends Activity {

    private void doWOrk() {
        BinderPool binderPool = BinderPool.getInstance(this);
        IBinder securityBInder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);


    }
}
