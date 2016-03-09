package com.gezhii.fitgroup.ui.fragment.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by ycl on 15/10/30.
 */
public class GroupLevelConfigFragment extends BaseFragment {


    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.up_text)
    TextView upText;
    @InjectView(R.id.up_layout)
    LinearLayout upLayout;
    @InjectView(R.id.down_text)
    TextView downText;
    @InjectView(R.id.down_layout)
    LinearLayout downLayout;
    @InjectView(R.id.level_view_pager)
    VerticalViewPager levelViewPager;
    LevelFragmentAdapter levelFragmentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_level_fragment, null);
        ButterKnife.inject(this, view);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        levelFragmentAdapter = new LevelFragmentAdapter(getActivity().getSupportFragmentManager());
        levelViewPager.setAdapter(levelFragmentAdapter);
        levelViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("group_level", "scroll"));
            }

            @Override
            public void onPageSelected(int position) {
                if (UserModel.getInstance().getMyGroup().getLevel() == position + 1) {
                    upText.setText(position + "(已解锁)");
                    downText.setText(position + 2 + "级" + " (解锁中)");
                } else if (UserModel.getInstance().getMyGroup().getLevel() > position + 1) {
                    upText.setText(position + "级" + " (已解锁)");
                    downText.setText(position + 2 + "级" + " (已解锁)");
                } else if (UserModel.getInstance().getMyGroup().getLevel() == position) {
                    upText.setText(position + "级" + " (已解锁)");
                    downText.setText(position + 2 + "级" + " (锁定)");
                } else if (UserModel.getInstance().getMyGroup().getLevel() == position - 1) {
                    upText.setText(position + "级" + " (解锁中)");
                    downText.setText(position + 2 + "级" + " (锁定)");
                } else {
                    upText.setText(position + "级" + " (锁定)");
                    downText.setText(position + 2 + "级" + " (锁定)");
                }
                if (position == 0) {
                    upLayout.setVisibility(View.INVISIBLE);
                    downLayout.setVisibility(View.VISIBLE);
                } else if (position == GroupLevelConfigModel.getInstance().getGroupLevelConfig().size() - 1) {
                    upLayout.setVisibility(View.VISIBLE);
                    downLayout.setVisibility(View.INVISIBLE);
                } else {
                    upLayout.setVisibility(View.VISIBLE);
                    downLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        levelViewPager.setCurrentItem(UserModel.getInstance().getMyGroup().getLevel());
        levelFragmentAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
    }

    public static class LevelFragmentAdapter extends FragmentPagerAdapter {

        FragmentManager fm;


        public LevelFragmentAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getCount() {
            if (GroupLevelConfigModel.getInstance().getGroupLevelConfig() != null) {
                return GroupLevelConfigModel.getInstance().getGroupLevelConfig().size();
            }
            return 0;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            GroupLevelCardFragment groupLevelCardFragment = new GroupLevelCardFragment();
            groupLevelCardFragment.setGroupLevelConfig(GroupLevelConfigModel.getInstance().getGroupLevelConfig().get(position));

            FragmentTransaction ft = fm.beginTransaction();

            ft.add(container.getId(), groupLevelCardFragment, "");

            ft.attach(groupLevelCardFragment);

            ft.commit();

            return groupLevelCardFragment;


        }

        @Override
        public Fragment getItem(int position) {
            Log.i("xianrui", "GroupLevelConfigFragment getItem " + position);
            GroupLevelCardFragment groupLevelCardFragment = new GroupLevelCardFragment();
            groupLevelCardFragment.setGroupLevelConfig(GroupLevelConfigModel.getInstance().getGroupLevelConfig().get(position));
            return groupLevelCardFragment;
        }

    }

}
