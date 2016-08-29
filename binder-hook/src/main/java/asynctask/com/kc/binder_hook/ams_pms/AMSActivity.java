package asynctask.com.kc.binder_hook.ams_pms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import asynctask.com.kc.binder_hook.R;

/**
 * Created by kuangcheng01 on 2016/8/26.
 */
public class AMSActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }

    // 这个方法比onCreate调用早; 在这里Hook比较好.
    @Override
    protected void attachBaseContext(Context newBase) {
        HookHelper.hookActivityManager();
        HookHelper.hookPackageManager(newBase);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn1) {// 测试AMS HOOK (调用其相关方法)
            Uri uri = Uri.parse("http://wwww.baidu.com");
            Intent t = new Intent(Intent.ACTION_VIEW);
            t.setData(uri);
            startActivity(t);

        } else if (i == R.id.btn2) {// 测试PMS HOOK (调用其相关方法)
            getPackageManager().getInstalledApplications(0);

        }
    }
}
