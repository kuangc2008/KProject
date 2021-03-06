package com.kc.kproejct;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActivityFinder {

    private static final String MAIN_ACTIVITY_ACTION = "com.kc.life.rise";

    public static List<ActivityInfo> getAllActivity(final Context context) {

        PackageManager pm = context.getApplicationContext().getPackageManager();
        Intent intent = new Intent(MAIN_ACTIVITY_ACTION);
        intent.setPackage(context.getPackageName());
        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
        ArrayList<ActivityInfo> result = new ArrayList<>();
        for(int i=infos.size() -1 ; i >=0 ; i--) {
            result.add(infos.get(i).activityInfo);
        }
        Collections.sort(result, new Comparator<ActivityInfo>() {
            @Override
            public int compare(ActivityInfo lhs, ActivityInfo rhs) {
                return rhs.loadLabel(context.getPackageManager()).toString().compareTo(
                        lhs.loadLabel(context.getPackageManager()).toString()
                );
            }
        });
        return result;
    }

}