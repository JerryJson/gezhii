package com.gezhii.fitgroup.ui.fragment.me;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.dto.basic.UserLevelConfig;
import com.gezhii.fitgroup.dto.db.UserLevelConfigDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/7.
 */
public class RoadToUpgradeFragment extends BaseFragment {


    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.current_view)
    View currentView;
    @InjectView(R.id.current_size_text)
    TextView currentSizeText;
//    @InjectView(R.id.need_view)
//    View needView;
    @InjectView(R.id.progress_view)
    ProgressView progressView;
    @InjectView(R.id.current_grade_text)
    TextView currentGradeText;
    @InjectView(R.id.next_grade_text)
    TextView nextGradeText;
    @InjectView(R.id.prompt_text)
    TextView promptText;
    @InjectView(R.id.upgrade_list_layout)
    LinearLayout upgradeListLayout;
    //private List<HashMap<String, Object>> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.road_to_upgrade, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        setTitle();
        return rootView;
    }

    public void setTitle() {
        titleText.setText("升级之路");
        rightText.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getUserLevelConfigs(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                User user = UserModel.getInstance().getUserDto().getUser();
                UserLevelConfigDTO userLevelConfigDTO = new UserLevelConfigDTO();
                userLevelConfigDTO = UserLevelConfigDTO.parserJson(response);
                List<UserLevelConfig> levelConfigList = new ArrayList<UserLevelConfig>();
                levelConfigList = userLevelConfigDTO.level_configs;
                int nextExperience = levelConfigList.get(user.getLevel()).getExpericence();
                initRoadList(userLevelConfigDTO, user.getLevel() + 1);
                initProgressView(user.getExperience(), nextExperience, user.getLevel());
            }
        });
    }


    public void initProgressView(int current, int max, int level) {
        progressView.setCurrentProgress(current);
        progressView.setMaxProgress(max);
        progressView.setProgressColor(R.color.pink_ff);
        progressView.setBgColor(R.color.blue_d7);
        progressView.invalidate();
        WindowManager wm = getActivity().getWindowManager();

        int windowWidth = wm.getDefaultDisplay().getWidth();
        int progressWidth = progressView.getMeasuredWidth();
        int currentViewWidth = progressView.getProgressWidth();
        currentSizeText.setText(String.valueOf(current));

        int width =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height =View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        currentSizeText.measure(width, height);
        int measureWidth = currentSizeText.getMeasuredWidth();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)currentView.getLayoutParams();
        layoutParams.width=(windowWidth-progressWidth)/2+currentViewWidth-(measureWidth-50)/2;
        layoutParams.height= LinearLayout.LayoutParams.MATCH_PARENT;
        currentView.setLayoutParams(layoutParams);

        currentGradeText.setText(String.valueOf(level) + "级");
        nextGradeText.setText(String.valueOf(level + 1) + "级 " + String.valueOf(max));
        promptText.setText("下次升级还需要" + String.valueOf(max - current) + "轻元素≈打卡" + String.valueOf((max - current) / 20+1) + "天");
    }

    View roadToUpgrade = null;

    @SuppressLint("NewApi")
    public void initRoadList(UserLevelConfigDTO userLevelConfigDTO, int level) {
        if (roadToUpgrade != null) {
            upgradeListLayout.removeAllViews();
        }

        for (int i = level - 1; i >= 0; i--) {
            UserLevelConfig userLevelConfig = userLevelConfigDTO.level_configs.get(i);
            roadToUpgrade = LayoutInflater.from(getActivity()).inflate(R.layout.road_to_upgrade_item, null);
            UpgradeItemViewHolder upgradeItemViewHolder = new UpgradeItemViewHolder(roadToUpgrade);
            upgradeItemViewHolder.circleText.setText(userLevelConfig.getLevel() + "级");
            if(userLevelConfig.getLevel()==1){
                upgradeItemViewHolder.lightElementSizeText.setText("加入轻元素");
            }else {
                upgradeItemViewHolder.lightElementSizeText.setText("轻元素达到" + userLevelConfig.getExpericence());
            }
            upgradeItemViewHolder.lockedImg.setVisibility(View.GONE);
            if (i == level - 1) {
                upgradeItemViewHolder.aboveLine.setVisibility(View.GONE);
                upgradeItemViewHolder.lockedImg.setVisibility(View.VISIBLE);
                upgradeItemViewHolder.circleText.setBackgroundResource(R.mipmap.gray_97_circle);
            }
            if (i == 0) {
                upgradeItemViewHolder.belowLine.setVisibility(View.GONE);
            }
            upgradeListLayout.addView(roadToUpgrade);
        }
    }


    static class UpgradeItemViewHolder {
        @InjectView(R.id.circle_text)
        TextView circleText;
        @InjectView(R.id.above_line)
        ImageView aboveLine;
        @InjectView(R.id.below_line)
        ImageView belowLine;
        @InjectView(R.id.light_element_size_text)
        TextView lightElementSizeText;
        @InjectView(R.id.locked_img)
        ImageView lockedImg;

        UpgradeItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
