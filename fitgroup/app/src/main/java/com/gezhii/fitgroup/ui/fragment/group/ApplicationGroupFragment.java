package com.gezhii.fitgroup.ui.fragment.group;

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

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupStateChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.ApplyJoinMessageExt;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class ApplicationGroupFragment extends BaseFragment {

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
    @InjectView(R.id.application_group_reason_input)
    EditText applicationGroupReasonInput;

    public static void start(Activity activity, int group_id, String leader_huanxin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("group_id", group_id);
        params.put("leader_huanxin_id", leader_huanxin_id);
        mainActivity.showNext(ApplicationGroupFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.application_group_fragment, null);
        ButterKnife.inject(this, view);
        MobclickAgent.onEvent(getActivity(), "application", UmengEvents.getEventMap("click", "load"));
        setTitle();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void setTitle() {
        titleText.setText("入会申请");
        rightText.setText("提交申请");
        backBtn.setVisibility(View.GONE);
        backText.setText("取消");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "application", UmengEvents.getEventMap("click", "cancel"));
                finish();
            }
        });

        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "application", UmengEvents.getEventMap("click", "submit"));
                if (TextUtils.isEmpty(applicationGroupReasonInput.getText().toString().trim())) {
                    showToast("入会理由不可为空");
                } else {
                    int group_id = (Integer) getNewInstanceParams().get("group_id");
                    final String leader_huanxin_id = (String) getNewInstanceParams().get("leader_huanxin_id");
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.applicationGroup(UserModel.getInstance().getUserId(), group_id, applicationGroupReasonInput.getText().toString(),
                            new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) {
                                    EventBus.getDefault().post(new CloseLoadingEvent());
                                    EventBus.getDefault().post(new GroupStateChangeEvent());
                                    Toast.makeText(getActivity(), "提交成功", Toast.LENGTH_SHORT).show();
                                    HuanXinHelper.sendPrivateTextMessage(new ApplyJoinMessageExt(UserModel.getInstance().getUserNickName(),
                                                    UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())), leader_huanxin_id,
                                            UserModel.getInstance().getUserNickName() + "提交了入会申请",
                                            new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    finish();
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                    UserModel.getInstance().tryLoadRemote(true);
                                }
                            });
                }
            }
        });

    }
}
