package asynctask.com.kc.binder_hook.intercept_activity;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by kuangcheng on 16/9/1.
 */
public class IActivityManagerHandler implements InvocationHandler {

    private static final String TAG = "kc";
    private Object mBase;

    public IActivityManagerHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("startActivity".equals(method.getName())) {
            Intent raw;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            raw = (Intent) args[index];


            Intent newIntent = new Intent();
            String stubPackage = "com.kc.kproejct";
            ComponentName conponentName = new ComponentName(stubPackage, StubActivity.class.getName());
            newIntent.setComponent(conponentName);

            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, raw);

            args[index] = newIntent;

            Log.i("kcc", "hook success");

            return method.invoke(mBase, args);
        }

        return method.invoke(mBase, args);
    }
}
