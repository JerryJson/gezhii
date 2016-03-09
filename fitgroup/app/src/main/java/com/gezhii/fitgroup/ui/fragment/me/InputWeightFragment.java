package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.UserWeightModel;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/7.
 */
public class InputWeightFragment extends BaseFragment {
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
    public static boolean TAG_ISFIRSTSETWEIGHT;
    @InjectView(R.id.weight_img)
    ImageView weightImg;

    public static void start(Activity activity, boolean isFirstSet) {

        MainActivity mainActivity = (MainActivity) activity;
        if (isFirstSet) {
            TAG_ISFIRSTSETWEIGHT = true;
        } else {
            TAG_ISFIRSTSETWEIGHT = false;
        }
        mainActivity.showNext(InputWeightFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.input_weight_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        paramsInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), paramsInput);
        setTitle();
        return rootView;
    }

    public void setTitle() {
        if (TAG_ISFIRSTSETWEIGHT) {
            MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("set", "current_weight"));
            weightImg.setVisibility(View.GONE);
            rightImg.setVisibility(View.GONE);
            rightText.setText("确定");
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("add", "weight"));
            weightImg.setVisibility(View.VISIBLE);
            rightImg.setVisibility(View.GONE);
            rightText.setText("确定");
            titleText.setText("记录体重");
            backText.setText("取消");
            backBtn.setVisibility(View.INVISIBLE);
            backText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }


        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(paramsInput.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "请输入体重", Toast.LENGTH_SHORT).show();
                } else {
                    float weight = Float.valueOf(paramsInput.getText().toString().trim());
                    if (TAG_ISFIRSTSETWEIGHT) {
                        UserWeightModel.getInstance().setWeight(weight);
                        TargetWeightFragment.start(getActivity());
                    } else {
                        if (weight > 0) {
                            EventBus.getDefault().post(new ShowLoadingEvent());
                            API.addTodayWeight(UserModel.getInstance().getUserId(), weight, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new CloseLoadingEvent());
                                    UserModel.getInstance().tryLoadRemote(true);
                                    finish();
                                }
                            });
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), paramsInput);
        ButterKnife.reset(this);
    }
}
