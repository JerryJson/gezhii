package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/4.
 */
public class EditGoalWeightFragment extends BaseFragment {
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
    @InjectView(R.id.params_input)
    EditText paramsInput;
    @InjectView(R.id.unit)
    TextView unit;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(EditGoalWeightFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_goal_weight_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        initView();
        MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("edit", "goal_weight"));
        paramsInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), paramsInput);
        return rootView;
    }

    public void initView() {
        backBtn.setVisibility(View.INVISIBLE);
        backText.setText("取消");
        titleText.setText("目标体重");
        rightImg.setVisibility(View.INVISIBLE);
        rightText.setText("确定");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(paramsInput.getText().toString())) {
                    showToast("请输入目标体重");
                } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.updateUserProfileHttp(UserModel.getInstance().getUserId(), "", "", getGoalWeight(), null, null, null, null, null,
                            new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new CloseLoadingEvent());
                                    finish();
                                    UserModel.getInstance().updateUserDto(UserDto.parserJson(response));
                                }
                            });
                }
            }
        });
    }

    public float getGoalWeight() {
        float goalWeight = Float.valueOf(paramsInput.getText().toString());
        return goalWeight;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), paramsInput);
        ButterKnife.reset(this);
    }
}
