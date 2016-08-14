package com.kc.a50_android_hacks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kuangcheng01 on 2016/8/14.
 */
public class BitmapSizeTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load image
        try {
            // get input stream

            /**
             * 图片的占用空间，根据压缩方式会比较小，但是加载到内存时会根据像素来决定大小，所以会很大
             */
            InputStream ims = getAssets().open("bigimage.jpg");
            Bitmap bm = BitmapFactory.decodeStream(ims);

            // 1.57M。  原图只有355kb哦
            FileOutputStream outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "123.jpg"));
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // 2.85M
            outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "123.png"));
            bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            // 332kb
            outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "1234.png"));
            bm.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);

            // 内存18M
            Bitmap bm2 = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(), "123.png").getPath());
            Log.i("kcc", "bm size->" + bm2.getByteCount() +  "  size->" + convertSize(bm2.getByteCount()));

            // 内存18M
            Bitmap bm3 = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(), "1234.png").getPath());
            Log.i("kcc", "bm size->" + bm3.getByteCount() +  "  size->" + convertSize(bm3.getByteCount()));
        }
        catch(IOException ex) {
            Log.w("kcc", null, ex);
            return;
        }

    }

    public static String convertSize(int bytes ) {
        if(bytes < 1024)
            return bytes + " bytes";

        bytes /= 1024;
        if(bytes < 1024)
            return bytes + " KB";

        bytes /= 1024;
        if(bytes < 1024)
            return bytes + " MB";

        bytes /= 1024;
        return bytes + " GB";
    }

}
