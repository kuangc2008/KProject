package com.example.touchevent1;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chengkuang on 16/7/30.
 */
public class ListHeaderSlideActivity extends Activity {
    private ListView lv;
    private LinearLayout headerLL;
    private View placeView;

    private SparseArray<Integer> headersHeight = new SparseArray<>();


    private int mLastY = 0;

    private int mPressDownY = -1;
    private int mTransY = -1;

    private ObjectAnimator oa = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_mengemng);



        lv = (ListView) findViewById(R.id.header_slider_listview);

        placeView = new View(ListHeaderSlideActivity.this);
        lv.addHeaderView(placeView);

        for (int i = 0 ; i < 1; i++) {
            TextView headerView = new TextView(this);
            headerView.setTextSize(100);
            headerView.setText("this is a headrView-->" + i);
            lv.addHeaderView(headerView);
        }
        ArrayList<String> lr = new ArrayList<String>();
        for (int i = 0 ; i < 100; i++) {
            lr.add("item -->" + i);
        }

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lr));



        headerLL = (LinearLayout) findViewById(R.id.header_slider_header);
        headerLL.post(new Runnable() {
            @Override
            public void run() {
                int height = headerLL.getHeight();
                placeView.getLayoutParams().height = height;
                headersHeight.put(0, height);

                int height2 = lv.getChildAt(1).getHeight();
                headersHeight.put(1, height2);
            }
        });


        headerLL.setTranslationY(300);
        lv.setTranslationY(300);


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

//                    if (headerLL.getTranslationY() >= -headersHeight.get(0)/2) {
//                        oa = ObjectAnimator.ofFloat(headerLL, "translationY", headerLL.getTranslationY(), 0);
//                    } else {
//                        oa = ObjectAnimator.ofFloat(headerLL, "translationY", headerLL.getTranslationY(),  -headersHeight.get(0));
//                    }
//                    oa.setDuration(300000);
//                    oa.start();
//                    Log.i("kcc", "scrollState->" + scrollState + "  start-");
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("kcc", "first->" + firstVisibleItem + "  visi->" + view + "  total->" + totalItemCount);

                View childVIew = view.getChildAt(firstVisibleItem);

                if (headersHeight.size() > 0) {
                    int scrollY = getScrollY(view);

                    int minHeight = headersHeight.get(0);

//                    Log.i("kcc", "scrollY->" + scrollY + " pressY->" + mPressDownY);



//                        if (scrollY <= mTransY) {
//                            headerLL.setTranslationY(-scrollY);
//                        } else {


//                            headerLL.setTranslationY(-minHeight);




//                            int transY = (int) headerLL.getTranslationY();
                            if (scrollY < mPressDownY) {
                                int deltaY = (scrollY - mPressDownY);  //  小于0, 向下滑动

                                headerLL.setTranslationY(  (mTransY- deltaY) > 0 ? 0 : (mTransY - deltaY) );



//                                Log.i("kcc", "1111" +  ((mTransY- deltaY) > 0 ? 0 : (mTransY - deltaY) ));

                            } else {
                                int deltaY = (scrollY - mPressDownY);  //向上滑动,大于0




                                headerLL.setTranslationY(  (mTransY - deltaY) <= -minHeight ? -minHeight : (mTransY - deltaY) );

//                                Log.i("kcc", "22222" +  ((mTransY - deltaY) <= -minHeight ? -minHeight : (mTransY - deltaY)) );
//                            }




                        }



                    mLastY = scrollY;
                }




            }
        });


        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (oa != null) {
//                        oa.end();
//                        oa = null;
//                    }

                    mPressDownY = getScrollY((AbsListView) v);
                    mTransY = (int) headerLL.getTranslationY();

                    Log.i("kcc", " pressDownY->" + mPressDownY);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE)  {
                    if (headerLL.getTranslationY() == 0 || headerLL.getTranslationY() == -headersHeight.get(0)) {
                        mPressDownY = getScrollY((AbsListView) v);
                        mTransY = (int) headerLL.getTranslationY();
                    }
                }

                return false;
            }
        });


    }



    public int getScrollY(AbsListView view) {
        if (headersHeight.size() == 0) {
            return 0;
        }

        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();
        int bottom = c.getBottom();

        int headerHeight = 0;
        if (firstVisiblePosition > 0) {
            headerHeight += headersHeight.get(0);

        }

        if (firstVisiblePosition > 1) {
            headerHeight += headersHeight.get(1);
        }

        int cCount = firstVisiblePosition > 2 ?  firstVisiblePosition - 2 : 0;


//        Log.i("kcc", "hellllll---" + headerHeight + "   top->" + (top) + "   cCount->" + cCount + "  bottom->" + bottom);

        return -top + (cCount) * c.getHeight() + headerHeight;
    }





}
