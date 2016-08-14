package com.kc.a50_android_hacks;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by kuangcheng01 on 2016/8/5.
 */
public class TransActivityTest1 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout ff = new FrameLayout(this);
        ff.setBackgroundColor(Color.RED);
        setContentView(ff);
        ff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TransActivityTest1.this, TransActivityTest2.class);
                startActivity(i);
            }
        });


    }
}
