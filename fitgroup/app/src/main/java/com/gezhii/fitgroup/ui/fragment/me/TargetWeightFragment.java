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
import android.widget.Toast;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.UserWeightModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/7.
 */
public class TargetWeightFragment extends BaseFragment {
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
    @InjectView(R.id.current_figure_text)
    TextView currentFigureText;
    @InjectView(R.id.params_input)
    EditText paramsInput;
    @InjectView(R.id.unit)
    TextView unit;
    @InjectView(R.id.sex_img)
    ImageView sexImg;
    public static final String tagLightSlim = "偏瘦";
    public static final String tagStandard = "标准";
    public static final String tagLightFat = "偏胖";
    public static final String tagFat = "肥胖";
    @InjectView(R.id.stand_weight_text)
    TextView standWeightText;
    private int height;
    private float weight;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(TargetWeightFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.target_weight_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("set", "goal_weight"));
        setTitle();
        setSexView();
        judgeFitness();
        return rootView;
    }

    public void setSexView() {
        if (UserWeightModel.getInstance().getSex() == 0) {
            sexImg.setImageResource(R.mipmap.girl);
        } else if (UserWeightModel.getInstance().getSex() == 1) {
            sexImg.setImageResource(R.mipmap.boy);
        }
    }

    public void judgeFitness() {
        height = UserWeightModel.getInstance().getHeight();
        weight = UserWeightModel.getInstance().getWeight();
        float bmi;
        bmi = weight / (height * height) * 10000;
        Log.i("bmi", bmi);
        if (bmi < 18.5) {
            currentFigureText.setText(tagLightSlim);
        } else if (bmi < 24) {
            currentFigureText.setText(tagStandard);
        } else if (bmi < 28) {
            currentFigureText.setText(tagLightFat);
        } else {
            currentFigureText.setText(tagFat);
        }
        float standMin = 24 * height * height / 10000;
        float standMax = 28 * height * height / 10000;
        Log.i("standMin", standMin);
        Log.i("standMax", standMax);
        standWeightText.setText(String.format(getString(R.string.suggest_weight), standMin, standMax));
    }

    public void setTitle() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightImg.setVisibility(View.GONE);
        rightText.setText("完成");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(paramsInput.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入目标体重", Toast.LENGTH_SHORT).show();
                } else {
                    UserWeightModel.getInstance().setGoalWeight(getTargetWeight());
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.uploadUserGuide(UserModel.getInstance().getUserId(), UserWeightModel.getInstance().getSex(), height, weight, UserWeightModel.getInstance().getGoalWeight(), "",
                            new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    UserModel.getInstance().setFirstSetWeightFlag(0);
                                    EventBus.getDefault().post(new CloseLoadingEvent());
                                    UserModel.getInstance().tryLoadRemote(true);
                                    finishAll();
                                }
                            });
                }

            }
        });
    }

    public float getTargetWeight() {
        float targetWeight = Float.valueOf(paramsInput.getText().toString().trim());
        return targetWeight;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
