package com.kc.a50_android_hacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/**
 * Created by kuangcheng on 16/8/31.
 */
public class ScaleTypeTestActivity extends FragmentActivity {
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sacletype_test_activity);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        findViewById(R.id.center).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setScaleType(adapter, ImageView.ScaleType.CENTER);
            }
        });

        findViewById(R.id.fit_center).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setScaleType(adapter, ImageView.ScaleType.FIT_CENTER);
            }
        });

        findViewById(R.id.fitstart).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setScaleType(adapter, ImageView.ScaleType.FIT_START);
            }
        });

        findViewById(R.id.center_crop).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setScaleType(adapter, ImageView.ScaleType.CENTER_CROP);
            }
        });

        findViewById(R.id.center_inside).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                setScaleType(adapter, ImageView.ScaleType.CENTER_INSIDE);
            }
        });

    }


    public void setScaleType(ViewPagerAdapter adapter, ImageView.ScaleType scaleType) {
        for (int i =0; i<adapter.getCount(); i++) {
            ImageFragment imageFragment = (ImageFragment) adapter.getItem(i);
            imageFragment.setScaleType(scaleType);
        }
    }



    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<ImageFragment> fragmentSparseArrays = new SparseArray<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment imageFragmentCached = fragmentSparseArrays.get(position);
            if (imageFragmentCached != null) {
                return imageFragmentCached;
            }

            ImageFragment imageFragment = new ImageFragment();
            Bundle bunder = new Bundle();
            bunder.putInt("pos", position);
            imageFragment.setArguments(bunder);
            fragmentSparseArrays.put(position, imageFragment);
            return imageFragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class ImageFragment extends Fragment {

        private ImageView mImageView;
        private ImageView.ScaleType scaleType;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mImageView = new ImageView(getContext());
            if (scaleType != null) {
                mImageView.setScaleType(scaleType);
            }
            return mImageView;

        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            int pos = getArguments().getInt("pos");
            if (pos == 0) {
                mImageView.setImageResource(R.drawable.small_pic);
            } else if (pos == 1) {
                mImageView.setImageResource(R.drawable.big_pic);
            }else if (pos == 2) {
                mImageView.setImageResource(R.drawable.width_pic);
            }
        }


        public void setScaleType(ImageView.ScaleType scaleType) {
            if (mImageView != null) {
                mImageView.setScaleType(scaleType);
                this.scaleType = scaleType;
            }
        }
    }

}
