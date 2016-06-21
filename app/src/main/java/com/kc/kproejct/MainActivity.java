package com.kc.kproejct;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView ll = new ListView(this);
        ll.setAdapter(new MainAdapter(this));
        setContentView(ll);
        Log.i("kcc", "MainActivity onCreate6 ");

        Log.i("kcc", "MainActivity onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("kcc", "MainActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("kcc", "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("kcc", "MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("kcc", "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("kcc", "MainActivity onDestroy");
    }

    public static class MainAdapter extends BaseAdapter {
        private List<ActivityInfo> mAllActivity = null;
        private Context mContext = null;

        public MainAdapter(Context context) {
            mContext = context;
            mAllActivity = ActivityFinder.getAllActivity(mContext);
        }

        @Override
        public int getCount() {
            return mAllActivity != null ? mAllActivity.size() : 0;
        }

        @Override
        public ActivityInfo getItem(int position) {
            return mAllActivity.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null);
                holder.listTitleTV = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ActivityInfo info = getItem(position);
            holder.listTitleTV.setText(info.loadLabel(mContext.getPackageManager()));
            holder.listTitleTV.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(info.packageName,
                            info.name));
                    mContext.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView listTitleTV;
    }
}
