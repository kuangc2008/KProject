package com.kc.a50_android_hacks;

import android.app.Application;
import android.net.Uri;
import android.os.Build;
import android.test.ApplicationTestCase;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);



        String a = "sfefefs<我 &#25105;11111";
        String encodeA = null;   //会转汉字和特殊字符，如<变成%3c
        try {
            encodeA = URLEncoder.encode(a, "utf-8");
            String decodeA = URLDecoder.decode(encodeA, "utf-8");  //转回来

//            Log.i("kcc", encodeA);
//            Log.i("kcc", decodeA);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //如果有<，这边会不正常
        //
//        String encodeA2 = Html.toHtml(new SpannableString(a));  //将<，转成特殊符号
        String encodeA2 = TextUtils.htmlEncode(a);
        String decodeA2 =  Html.fromHtml(encodeA2).toString();    //将特殊符号，转成<

        //如果有<会忽略很多文字
        //如果有&#25105; 会转成汉字
        String decodeA3 =  Html.fromHtml(a).toString();    //将特殊符号，转成<



//        Log.i("kcc", "-------------------");
//        Log.i("kcc", encodeA2);
//        Log.i("kcc", decodeA2);
//        Log.i("kcc", "-------------------");
//        Log.i("kcc", decodeA3);


        JSONObject b = new JSONObject();
        try {
            b.put("aaa", "'");
            b.put("bbb", "\"");
            b.put("ccc", "<");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String b2 = TextUtils.htmlEncode(b.toString());
        String b3 =  Html.fromHtml(b2).toString();    //将特殊符号，转成<

        Log.i("kcc", b.toString());
        Log.i("kcc", b2);
        Log.i("kcc", b3);


        Gson g = new GsonBuilder().disableHtmlEscaping().create();


//        String reu = StringEscapeUtils.escapeJson(b2);
//        Log.i("kcc", reu);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            Log.i("kcc", Html.escapeHtml(b2));
//        }





//
//        String http = "http://www.www.baidu.com/sfsf?a=我的&b='\"";
//
//        Log.i("kcc",  Uri.encode(http));            //这个不会转单引号
//        Log.i("kcc",  URLEncoder.encode(http));   //这个会转单引号
    }


}