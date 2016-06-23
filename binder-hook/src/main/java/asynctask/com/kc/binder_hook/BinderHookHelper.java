package asynctask.com.kc.binder_hook;

import android.os.IBinder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by kuangcheng01 on 2016/6/23.
 */
public class BinderHookHelper {

    public static void hookClipboardService() throws Exception {
        final String CLIPBOARD_SERVICE = "clipboard";

        // 下面这一段的意思实际就是: ServiceManager.getService("clipboard");
        // 只不过 ServiceManager这个类是@hide的
        Class<?> serviceManager = Class.forName("android.os.ServiceManager");
        Method getService = serviceManager.getDeclaredMethod("getService", String.class);

        // ServiceManager里面管理的原始的Clipboard Binder对象
        // 一般来说这是一个Binder代理对象
        IBinder rawBinder = (IBinder) getService.invoke(null, CLIPBOARD_SERVICE);

        // Hook 掉这个Binder代理对象的 queryLocalInterface 方法
        // 然后在 queryLocalInterface 返回一个IInterface对象, hook掉我们感兴趣的方法即可.
        IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                new Class<?>[] { IBinder.class },
                new BinderProxyHookHandler(rawBinder));




    }
}
