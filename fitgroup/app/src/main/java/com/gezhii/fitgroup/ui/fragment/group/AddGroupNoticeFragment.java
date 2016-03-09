package com.gezhii.fitgroup.ui.fragment.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupNoticeDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.NoticeMessageExt;
import com.gezhii.fitgroup.model.GroupNoticeCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class AddGroupNoticeFragment extends BaseFragment {
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
    @InjectView(R.id.add_group_notice_input)
    EditText addGroupNoticeInput;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_group_notice_fragment, null);
        ButterKnife.inject(this, view);

        setTitle();

        return view;
    }

    public void setTitle() {
        backBtn.setVisibility(View.GONE);

        backText.setText("取消");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        rightText.setText("发布");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addGroupNoticeInput.getText().toString().equals("")) {
                    Toast.makeText(MyApplication.getApplication(), "发布内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

//                EventBus.getDefault().post(new ShowLoadingEvent());
//                API.publishGroupNoticeHttp(UserModel.getInstance().getGroupId(), addGroupNoticeInput.getText().toString(), new APICallBack() {
//                    @Override
//                    public void subRequestSuccess(String response) {
//                        EventBus.getDefault().post(new CloseLoadingEvent());
//                        finish();
//                    }
//                });

                EventBus.getDefault().post(new ShowLoadingEvent());
                API.publishGroupNoticeHttp(UserModel.getInstance().getGroupId(),  addGroupNoticeInput.getText().toString(), new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) {
                        final GroupNoticeDto groupNoticeDto = GroupNoticeDto.parserJson(response);
                        HuanXinHelper.sendGroupTextMessage(new NoticeMessageExt(UserModel.getInstance().getUserNickName(),
                                        UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), String.valueOf(groupNoticeDto.getGroup_notice().id)),
                                UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), groupNoticeDto.getGroup_notice().description,
                                new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                EventBus.getDefault().post(new CloseLoadingEvent());
                                                showToast("发送公告成功");
                                                GroupNoticeCacheModel.getInstance().setGroupNotice(groupNoticeDto);
                                            }
                                        });
                                        finish();
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                    }
                });


            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
