package com.gezhii.fitgroup.ui.fragment.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupLevelConfig;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.ProgressView;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/11/19.
 */
public class GroupLevelCardFragment extends BaseFragment {


    @InjectView(R.id.level_state_text)
    TextView levelStateText;
    @InjectView(R.id.level_text)
    TextView levelText;
    @InjectView(R.id.group_level_max_count_text)
    TextView groupLevelMaxCountText;
    @InjectView(R.id.group_upgrade_condition_text)
    TextView groupUpgradeConditionText;
    @InjectView(R.id.progress_view)
    ProgressView progressView;
    @InjectView(R.id.root_layout)
    LinearLayout rootLayout;
    @InjectView(R.id.progress_layout)
    LinearLayout progressLayout;

    GroupLevelConfig groupLevelConfig;
    @InjectView(R.id.current_view)
    View currentView;
    @InjectView(R.id.current_size_text)
    TextView currentSizeText;
    @InjectView(R.id.progress_top_layout)
    LinearLayout progressTopLayout;
    @InjectView(R.id.current_grade_text)
    TextView currentGradeText;
    @InjectView(R.id.next_grade_text)
    TextView nextGradeText;


    public void setGroupLevelConfig(GroupLevelConfig groupLevelConfig) {
        this.groupLevelConfig = groupLevelConfig;
    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_level_card, null);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }

    private void setView() {
        if (groupLevelConfig != null) {
            if (UserModel.getInstance().getMyGroup().getLevel() >= groupLevelConfig.level) {
                rootLayout.setBackgroundResource(R.drawable.rounded_rectangle_pink_ff);
                levelStateText.setText("已解锁");
                progressLayout.setVisibility(View.INVISIBLE);
                progressView.setVisibility(View.INVISIBLE);
            } else if (UserModel.getInstance().getMyGroup().getLevel() == groupLevelConfig.level - 1) {
                rootLayout.setBackgroundResource(R.drawable.rounded_rectangle_gray_97);
                levelStateText.setText("解锁中");
                progressLayout.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.VISIBLE);
                progressView.setCurrentProgress(UserModel.getInstance().getMyGroup().getContinue_days());
                progressView.setMaxProgress(groupLevelConfig.days);
                progressView.setBgColor(R.color.white_60);
                progressView.setFgColor(R.color.white);
                progressView.setProgressColor(R.color.pink_ff);
                //progressView.setCurrentProgress(UserModel.getInstance().getMyGroup().getContinue_days());
                progressView.invalidate();
                nextGradeText.setText(groupLevelConfig.days + "天");


                int windowWidth = Screen.getScreenWidth();
                int progressWidth = windowWidth - Screen.dip2px(62);
                int progressHeight = Screen.dip2px(20);
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                currentSizeText.measure(width, height);
                int measureWidth = currentSizeText.getMeasuredWidth();
                int currentViewWidth = (int) ((progressWidth - 2 * progressHeight) * (UserModel.getInstance().getMyGroup().getContinue_days() - 2) * 1.0 / (groupLevelConfig.days - 2));
                currentSizeText.setText(String.valueOf(UserModel.getInstance().getMyGroup().getContinue_days() + "天"));


                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) currentView.getLayoutParams();
                if(UserModel.getInstance().getMyGroup().getContinue_days()==groupLevelConfig.days){
                    layoutParams.width=0;
                    layoutParams.weight=1;
                }else{
                    layoutParams.width = Screen.dip2px(31) + currentViewWidth - (measureWidth + 110) / 2;
                }
                layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                Log.i("currentViewWidth", currentViewWidth);
                Log.i("progressWidth", progressWidth);
                currentView.setLayoutParams(layoutParams);
            } else {
                rootLayout.setBackgroundResource(R.drawable.rounded_rectangle_gray_97);
                levelStateText.setText("锁定");
                progressLayout.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.VISIBLE);
                progressView.setBgColor(R.color.white_60);
                progressView.setFgColor(R.color.white);
                progressView.setProgressColor(R.color.pink_ff);
                progressView.setMaxProgress(groupLevelConfig.days);
                progressView.setCurrentProgress(0);
                nextGradeText.setText(groupLevelConfig.days + "天");
            }
            levelText.setText(groupLevelConfig.level + "级工会");
            groupLevelMaxCountText.setText("公会成员上限" + groupLevelConfig.max_member_count + "人");
            if (groupLevelConfig.level == 1) {
                groupUpgradeConditionText.setText("创建公会");
            } else {
                groupUpgradeConditionText.setText("单日打卡人数超过" + groupLevelConfig.signin_count + "人,连续" + groupLevelConfig.days + "天");
            }


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
