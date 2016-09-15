package com.kc.a50_android_hacks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.kc.a50_android_hacks.aidl.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chengkuang on 16/9/15.
 */
public class BinderPool {

    private static final String TAG = "BinderPool";

    public static final int BINDER_NONE= -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY_CNETER = 1;

    private Context mContext;
    private IBinderPool mBInderPool;
    private static volatile BinderPool sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    private BinderPool(Context cOntext) {
        mContext = cOntext.getApplicationContext();
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized  void connectBinderPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent serivcei = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(serivcei, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e ) {
        }
    }

    public IBinder queryBinder (int binderCode) {
        IBinder binder = null;
        try {
            if (mBInderPool != null) {
                binder = mBInderPool.queryBinder(binderCode);
            }
        }catch (RemoteException e) {

        }
        return binder;
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBInderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBInderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBInderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBInderPool = null;
            connectBinderPoolService();
        }
    };


    public static class BInderPoolImpl extends IBinderPool.Stub {

        public BInderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_SECURITY_CNETER:
                    //binder =
                    break;
                case BINDER_COMPUTE:
                    //binder =
                    break;
            }

            return binder;
        }
    }
}
