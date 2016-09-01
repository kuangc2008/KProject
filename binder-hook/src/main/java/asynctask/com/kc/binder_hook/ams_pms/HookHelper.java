package asynctask.com.kc.binder_hook.ams_pms;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kuangcheng01 on 2016/8/26.
 */
public class HookHelper {
    public static void hookActivityManager() {

        try {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");

            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            //4.x
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);

            // activityMangerNative的gDefault对象里面，原始的IActivityManger对象
            Object rawIActivityManager = mInstanceField.get(gDefault);
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iActivityManagerInterface}, new HookHandler(rawIActivityManager));
            mInstanceField.set(gDefault, proxy);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void hookPackageManager(Context newBase) {

        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);


            Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object sPackageManager = sPackageManagerField.get(currentActivityThread);

            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class<?>[]{iPackageManagerInterface},
                    new HookHandler(sPackageManager));

            sPackageManagerField.set(currentActivityThread, proxy);

            // 2. 替换 ApplicationPackageManager里面的 mPm对象
            PackageManager pm = newBase.getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            mPmField.set(pm, proxy);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
