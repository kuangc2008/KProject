package asynctask.com.kc.binder_hook.intercept_activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by kuangcheng on 16/9/1.
 */
public class TargetActivity extends Activity {



    private static final String TAG = "kc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called with " + "savedInstanceState = [" + savedInstanceState + "]");
        TextView tv = new TextView(this);
        tv.setText("TargetActivity 启动成功!!!");
        setContentView(tv);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called with " + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called with " + "");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called with " + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called with " + "");
    }

}
