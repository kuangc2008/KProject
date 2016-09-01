package asynctask.com.kc.binder_hook.intercept_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import asynctask.com.kc.binder_hook.MainActivity;

/**
 * Created by kuangcheng on 16/9/1.
 */
public class InterceptActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("启动TragetActivity");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InterceptActivity.this, TargetActivity.class));
            }
        });
        setContentView(button);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);


        try {
            AMSHookHelper.hookActivityManagerNative();
            AMSHookHelper.hookActivityThreadHandler();
        } catch (Exception e) {
            throw new RuntimeException("hook fail", e);
        }

    }
}
