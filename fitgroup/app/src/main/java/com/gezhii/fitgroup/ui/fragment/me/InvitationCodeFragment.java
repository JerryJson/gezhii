package com.gezhii.fitgroup.ui.fragment.me;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/5.
 */
public class InvitationCodeFragment extends BaseFragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.invitation_code_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        setTitle();
        return rootView;
    }

    public void setTitle() {
        backBtn.setVisibility(View.GONE);
        backText.setText("取消");
        titleText.setText("达人邀请码");
        rightText.setText("确定");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)paramsInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(paramsInput.getWindowToken(),0);
                finish();
            }
        });
        paramsInput.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) paramsInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(paramsInput, 0);
            }
        }, 300);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String invitation = paramsInput.getText().toString().trim();
                if(!"".equals(invitation)){
                    API.bindInvitationCode(UserModel.getInstance().getUserId(), invitation, new APICallBack() {

                        @Override
                        public void subRequestSuccess(String response) {
                            Log.i("response==="+response);
                            finish();
                        }
                    });
                }else{
                    Toast.makeText(getActivity(),"请输入邀请码",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
