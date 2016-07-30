package com.example.touchevent1;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
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
    private LinearLayout contentLL;


    private View placeView;

    private SparseArray<Integer> headersHeight = new SparseArray<>();


    private int mLastY = 0;

    private int mPressDownY = -1;
    private int mTransY = -1;

    private ObjectAnimator oa = null;


    private View headerLLEmpty;
    private View listHeaderEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_mengemng);



        lv = (ListView) findViewById(R.id.header_slider_listview);


        listHeaderEmpty = new View(this);
//        listHeaderEmpty.setBackgroundColor(Color.YELLOW);
        lv.addHeaderView(listHeaderEmpty);

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
        contentLL = (LinearLayout) findViewById(R.id.header_slider_header_content);
        headerLL.post(new Runnable() {
            @Override
            public void run() {
                int height = contentLL.getHeight();
                placeView.getLayoutParams().height = height;
                headersHeight.put(0, height);

                int height2 = lv.getChildAt(2).getHeight();
                headersHeight.put(1, height2);

                listHeaderEmpty.getLayoutParams().height = 700;
            }
        });
        headerLLEmpty = (View) findViewById(R.id.header_slider_header_empty1);
        headerLLEmpty.setBackgroundColor(Color.RED);
        headerLLEmpty.getLayoutParams().height = 700;


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
//                Log.i("kcc", "first->" + firstVisibleItem + "  visi->" + view + "  total->" + totalItemCount);

                if (headersHeight.size() ==0 ) {

                    return;
                }
//                    View childVIew = view.getChildAt(firstVisibleItem);


//                if (firstVisibleItem == 0) {
//                    View c = view.getChildAt(0);
//                    listHeaderEmpty.getLayoutParams().height = c.getBottom();
//                }

                    Log.i("kcc", "transY-" + headerLL.getTranslationY() + "  minhIe->" + getMinHeight());
                    if (headerLL.getTranslationY() ==  -getMinHeight() && isEmptyVisiable()) {
//                        lv.removeHeaderView(listHeaderEmpty);
//                        headerLL.removeView(headerLLEmpty);


                        int height222 = headerLLEmpty.getLayoutParams().height;;
                        headerLLEmpty.getLayoutParams().height = 0;
                        headerLL.removeView(headerLLEmpty);
                        listHeaderEmpty.getLayoutParams().height = 0;
                        mTransY += height222;
                        mPressDownY -= height222;
                        headerLL.setTranslationY( -getMinHeight() );


                    }



                    if (headersHeight.size() > 0) {
                        int scrollY = getScrollY(view);

//                        int minHeight = headersHeight.get(0);

                        int minHeight = getMinHeight();
                        if (scrollY < mPressDownY) {
                            int deltaY = (scrollY - mPressDownY);  //  小于0, 向下滑动
                            headerLL.setTranslationY((mTransY - deltaY) > 0 ? 0 : (mTransY - deltaY));
                                Log.i("kcc", "1111" +  ((mTransY- deltaY) > 0 ? 0 : (mTransY - deltaY) ));
                        } else {
                            int deltaY = (scrollY - mPressDownY);  //向上滑动,大于0
                            headerLL.setTranslationY((mTransY - deltaY) <= -minHeight ? -minHeight : (mTransY - deltaY));
                                Log.i("kcc", "22222" +  ((mTransY - deltaY) <= -minHeight ? -minHeight : (mTransY - deltaY)) );
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

                    Log.i("kcc", " pressDownY->" + mPressDownY  + " tranY->" + mTransY);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE)  {
                    if (headerLL.getTranslationY() == 0 || headerLL.getTranslationY() ==  -getMinHeight()) {
                        mPressDownY = getScrollY((AbsListView) v);
                        mTransY = (int) headerLL.getTranslationY();

                        Log.i("kcc", "222 pressDownY->" + mPressDownY + " tarnsY->" + mTransY);
                    }
                }

                return false;
            }
        });


    }

    private boolean isEmptyVisiable() {
        return headerLLEmpty != null && headerLLEmpty.getParent() != null;
    }


    private int getMinHeight() {

        return listHeaderEmpty.getHeight() + headersHeight.get(0);

//        if (isEmptyVisiable()) {
//            return headerLLEmpty.getHeight() + headersHeight.get(0);
//        } else {
//            return headersHeight.get(0);
//        }
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
            headerHeight += listHeaderEmpty.getLayoutParams().height;
        }

            if (firstVisiblePosition > 1) {
                headerHeight += headersHeight.get(0);

            }

            if (firstVisiblePosition > 2) {
                headerHeight += headersHeight.get(1);
            }

        int cCount = firstVisiblePosition > 3 ?  firstVisiblePosition - 3 : 0;


//        Log.i("kcc", "hellllll---" + headerHeight + "   top->" + (top) + "   cCount->" + cCount + "  bottom->" + bottom);

        return -top + (cCount) * c.getHeight() + headerHeight;
    }





}
