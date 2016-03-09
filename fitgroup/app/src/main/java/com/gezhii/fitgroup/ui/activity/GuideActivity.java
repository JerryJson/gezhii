package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/5/13.
 */
public class GuideActivity extends Activity {

    @InjectView(R.id.guide_viewpager)
    ViewPager guideViewpager;


    ArrayList<View> viewArrayList;
    @InjectView(R.id.guide_point_layout)
    LinearLayout guidePointLayout;

    private EdgeEffectCompat leftEdge;

    private EdgeEffectCompat rightEdge;


    private int nowPage = 0;
    public static final String TAG_FIRST_LOGIN = "tag_first_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_ui);
        ButterKnife.inject(this);
        MobclickAgent.onEvent(GuideActivity.this, "guide_page", UmengEvents.getEventMap("click", "load"));
        viewArrayList = new ArrayList<>();
        viewArrayList.add(LayoutInflater.from(this).inflate(R.layout.guide_first_page, null));
        viewArrayList.add(LayoutInflater.from(this).inflate(R.layout.guide_second_page, null));
        View thirdView = LayoutInflater.from(this).inflate(R.layout.guide_third_page, null);
        QiniuHelper.bindLocalImage(R.mipmap.page_third, (ImageView) thirdView.findViewById(R.id.third_page_image));
        thirdView.findViewById(R.id.into_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(GuideActivity.this, "guide_page", UmengEvents.getEventMap("click", "start"));
                SharedPreferences sharedPreferences = getSharedPreferences("fitGroup", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(TAG_FIRST_LOGIN, false);
                editor.commit();
                Intent goLoginAct = new Intent(GuideActivity.this, RegisterAndLoginActivity.class);
                startActivity(goLoginAct);
                finish();
            }
        });

        viewArrayList.add(thirdView);


        for (int i = 0; i < viewArrayList.size(); i++) {
            ImageView view = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.spacing_small), getResources().getDimensionPixelOffset(R.dimen.spacing_small));
            int margins = getResources().getDimensionPixelOffset(R.dimen.spacing_small);
            params.setMargins(margins, margins, margins, margins);
            view.setLayoutParams(params);
            guidePointLayout.addView(view);
        }


        guideViewpager.setAdapter(new GuideAdapter(viewArrayList));

        guideViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (leftEdge != null && rightEdge != null) {
                    leftEdge.finish();
                    rightEdge.finish();
                    leftEdge.setSize(0, 0);
                    rightEdge.setSize(0, 0);
                }
                if (position != nowPage) {
//                    nowPage = position;
//                    setImgBg(nowPage);
                }
//                AnimatorProxy.wrap(bgViewTop).setAlpha(positionOffset);
//                AnimatorProxy.wrap(bgViewBottom).setAlpha(1 - positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                selectPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        setImgBg(nowPage);
        selectPoint(nowPage);
        getEdge();
    }

    private void selectPoint(int nowPage) {
        for (int i = 0; i < guidePointLayout.getChildCount(); i++) {
            ImageView view = (ImageView) guidePointLayout.getChildAt(i);
            if (i == nowPage) {
                view.setImageResource(R.drawable.oval_black);
            } else {
                view.setImageResource(R.drawable.hollow_circle_black);
            }
        }
    }


    private void getEdge() {
        try {
            Field leftEdgeField = guideViewpager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = guideViewpager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(guideViewpager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(guideViewpager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class GuideAdapter extends PagerAdapter {

        ArrayList<View> viewArrayList;

        public GuideAdapter(ArrayList<View> viewArrayList) {
            this.viewArrayList = viewArrayList;
        }

        @Override
        public int getCount() {
            return viewArrayList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewArrayList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewArrayList.get(position), 0);
            return viewArrayList.get(position);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }
    }

}
