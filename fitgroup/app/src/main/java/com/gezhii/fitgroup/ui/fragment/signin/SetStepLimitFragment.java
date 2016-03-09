package com.gezhii.fitgroup.ui.fragment.signin;

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
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/9.
 */
public class SetStepLimitFragment extends BaseFragment {

    public static final String GROUP_TASK = "group_task";
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
    private GroupTask groupTask;

    public static void start(Activity activity, GroupTask groupTask) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(GROUP_TASK, groupTask);
        mainActivity.showNext(SetStepLimitFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.set_step_limit_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        groupTask = (GroupTask) getNewInstanceParams().get(GROUP_TASK);
        paramsInput.requestFocus();
        initTitleView();
        return rootView;
    }

    private void initTitleView() {
        titleText.setText("设置步数要求");
        rightText.setText(getString(R.string.done));
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getparamsInput() != null) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("input", "step");
                    hashMap.put("step_limit", Integer.valueOf(getparamsInput()));
                    String task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                    Log.i("task_info", task_info);
                    if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                        UserCustomerTask userCustomerTask = new UserCustomerTask();
                        userCustomerTask.setSign_type("step");
                        userCustomerTask.setStep_limit(Integer.valueOf(getparamsInput()));
                        userCustomerTask.setTask_name(groupTask.getTask_name());
                        UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                        MainActivity m = (MainActivity) getActivity();
                        EventBus.getDefault().post(new CustomTaskChangeEvent());
                        m.popToFragment(m.add_task_fragment_id);
                        EventBus.getDefault().post(new CustomTaskChangeEvent());
                    } else {
                        EventBus.getDefault().post(new ShowLoadingEvent());
                        API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), groupTask.getTask_name(), task_info, new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                EventBus.getDefault().post(new CloseLoadingEvent());
                                MainActivity m = (MainActivity) getActivity();
                                m.popToFragment(m.add_task_fragment_id);
                                EventBus.getDefault().post(new CustomTaskChangeEvent());
                                UserModel.getInstance().tryLoadRemote(true);
                            }
                        });
                    }
                } else {
                    showToast("请填写步数");
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getparamsInput() {
        String step = paramsInput.getText().toString().trim();
        if (!TextUtils.isEmpty(step)) {
            return step;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
