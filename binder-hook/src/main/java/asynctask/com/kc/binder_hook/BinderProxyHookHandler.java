package asynctask.com.kc.binder_hook;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 由于ServiceManager里面的sCache里面存储的 IBinder类型基本上都是BinderProxy
 * 因此, ServiceManager的使用者调用getService之后不会直接使用这个map
 * 而是先将他使用asInterface转成需要的接口
 * <p/>
 * asInterface函数的代码告诉我们, 它会先使用这个BinderPRoxy查询本进程是否有Binder对象
 * 如果有就使用本地的, 这里恰好就是一个hook点
 * <p/>
 * 我们让所有的查询都返回一个"本地Binder"对象
 * <p/>
 * 当然,这是一个假象, 我们给它返回的Binder对象自然是符合要求的(要么是本地Binder,要么是Binder代理)
 * 只不过,我们对需要hook的API做了处理
 * <p/>
 * 这个类仅仅Hook掉这个关键的 queryLocalInterface 方法
 */
public class BinderProxyHookHandler implements InvocationHandler {
    private static final String TAG = "BinderProxyHookHandler";

    // 绝大部分情况下,这是一个BinderProxy对象
    // 只有当Service和我们在同一个进程的时候才是Binder本地对象
    // 这个基本不可能
    IBinder base;

    Class<?> stub;

    Class<?> iinterface;

    public BinderProxyHookHandler(IBinder rawBinder) {
        this.base = base;
        try {
            this.stub = Class.forName("android.content.IClipboard$Stub");
            this.iinterface = Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
