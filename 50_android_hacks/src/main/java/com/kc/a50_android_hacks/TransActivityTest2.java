package com.kc.a50_android_hacks;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by kuangcheng01 on 2016/8/5.
 */
public class TransActivityTest2 extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);

        tv.setText("sffafewfasfefasf");

        setContentView(tv);
    }
}
