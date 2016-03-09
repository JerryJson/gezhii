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
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/11.
 */
public class AddTaskWeightFragment extends BaseFragment {
    public final static String TAG_SIGNIN_TASK_NAME = "tag_signin_task_name";
    public final static String TAG_CUSTOMER_TASK_ID = "tag_customer_task_id";
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
    String taskName;
    String customerTaskId;//用户自定义任务id

    public static void start(Activity activity, String task_name, String customer_task_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_SIGNIN_TASK_NAME, task_name);
        params.put(TAG_CUSTOMER_TASK_ID, customer_task_id);
        mainActivity.showNext(AddTaskWeightFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_task_weight_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        taskName = (String) getNewInstanceParams().get(TAG_SIGNIN_TASK_NAME);
        customerTaskId = (String) getNewInstanceParams().get(TAG_CUSTOMER_TASK_ID);
        initTitleView();
        paramsInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), paramsInput);
        return rootView;
    }

    private void initTitleView() {
        backBtn.setVisibility(View.INVISIBLE);
        titleText.setText("添加体重");
        backText.setText("取消");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText.setText("完成");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(getBodyWeight())) {
                    showToast("请输入体重");
                } else {
                    finish();
                    SignAddContentFragment.start(getActivity(), taskName, getBodyWeight(), customerTaskId, false);
                    KeyBoardHelper.hideKeyBoard(getActivity(), paramsInput);
                }

            }
        });
    }

    private String getBodyWeight() {
        String bodyWeight;
        bodyWeight = paramsInput.getText().toString().trim();
        return bodyWeight;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
