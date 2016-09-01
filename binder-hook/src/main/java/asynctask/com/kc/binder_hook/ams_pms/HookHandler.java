package asynctask.com.kc.binder_hook.ams_pms;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by kuangcheng on 16/9/1.
 */
public class HookHandler implements InvocationHandler {

    private Object mBase;

    public HookHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.i("kcc", "method:" + method.getName() + " called with args:" + Arrays.toString(args));
        return method.invoke(mBase, args);
    }
}
