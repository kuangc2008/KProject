package asynctask.com.kc.binder_hook.intercept_activity;

import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kuangcheng on 16/9/1.
 */
public class AMSHookHelper {
    public static final String EXTRA_TARGET_INTENT = "extra_target_intent";

    public static void hookActivityManagerNative() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException {

        //        26public abstract class Singleton<T> {
        //            27    private T mInstance;
        //            28
        //                    29    protected abstract T create();
        //            30
        //                    31    public final T get() {
        //                32        synchronized (this) {
        //                    33            if (mInstance == null) {
        //                        34                mInstance = create();
        //                        35            }
        //                    36            return mInstance;
        //                    37        }
        //                38    }
        //            39}
        //        40


        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
        Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
        gDefaultField.setAccessible(true);
        Object gDefault = gDefaultField.get(null);

        // gDefault是一个android.util.Singleton对象，我们去除这个单例里面的字段
        Class<?> singleton = Class.forName("android.util.Singleton");
        Field mInstanceField = singleton.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);

        // ActivityManagerNative 的gDefault对象里面原始的IActivityManager对象
        Object rawIActivityManager = mInstanceField.get(gDefault);

        Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityManagerInterface},
                new IActivityManagerHandler(rawIActivityManager));
        mInstanceField.set(gDefault, proxy);


    }

    public static void hookActivityThreadHandler() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThread = currentActivityThreadField.get(null);

        Field mHField = activityThreadClass.getDeclaredField("mH");
        mHField.setAccessible(true);
        Handler mH = (Handler) mHField.get(currentActivityThread);

        Field mCallBackField = Handler.class.getDeclaredField("mCallback");
        mCallBackField.setAccessible(true);
        mCallBackField.set(mH, new ActivityThreadHandlerCallback(mH));


    }
}
