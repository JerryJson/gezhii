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
import com.gezhii.fitgroup.dto.UserDto;
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
public class InputHeightFragment extends BaseFragment {
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
        mainActivity.showNext(InputHeightFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.input_height_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("set", "height"));
        setTitle();
        paramsInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), paramsInput);
        return rootView;
    }

    public void setTitle() {
        rightImg.setVisibility(View.GONE);
        rightText.setText("下一步");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(paramsInput.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入身高", Toast.LENGTH_SHORT).show();
                } else {
                    setHeight();
                    if (UserWeightModel.getInstance().isBmiChange()) {
                        EventBus.getDefault().post(new ShowLoadingEvent());
                        API.updateUserProfileHttp(UserModel.getInstance().getUserId(), UserWeightModel.getInstance().getHeight(), UserWeightModel.getInstance().getSex(),
                                new APICallBack() {
                                    @Override
                                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                        finishAll();
                                        UserModel.getInstance().updateUserDto(UserDto.parserJson(response));
                                        BmiFragment.start(getActivity());
                                        UserWeightModel.getInstance().setIsBmiChange(false);
                                    }
                                });
                    } else {
                        InputWeightFragment.start(getActivity(), true);
                    }

                }

            }

        });
    }

    public void setHeight() {
        int height = Integer.valueOf(paramsInput.getText().toString().trim());
        UserWeightModel.getInstance().setHeight(height);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
