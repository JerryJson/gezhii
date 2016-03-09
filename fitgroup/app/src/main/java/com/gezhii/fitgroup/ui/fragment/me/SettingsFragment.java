package com.gezhii.fitgroup.ui.fragment.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.activity.RegisterAndLoginActivity;
import com.gezhii.fitgroup.ui.dialog.FeedBackDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.common.io.FileUtils;
import com.xianrui.lite_common.litesuits.common.utils.BitmapUtil;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/5.
 */
public class SettingsFragment extends BaseFragment {
    FeedBackDialog feedBackDialog;
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
    @InjectView(R.id.go_invitation_code_layout)
    LinearLayout goInvitationCodeLayout;
    @InjectView(R.id.go_recommend_to_friends_layout)
    LinearLayout goRecommendToFriendsLayout;
    @InjectView(R.id.go_about_layout)
    LinearLayout goAboutLayout;
    @InjectView(R.id.go_logout_layout)
    LinearLayout goLogoutLayout;
    @InjectView(R.id.feedback_layout)
    LinearLayout feedbackLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_fragment, null);
        MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "load"));
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        setTitle();
        goInvitationCodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "invite_code"));
                ((MainActivity) getActivity()).showNext(InvitationCodeFragment.class, null);

            }
        });
        goAboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "about"));
                ((MainActivity) getActivity()).showNext(AboutFragment.class, null);
            }
        });
        goRecommendToFriendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "recommend"));
                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                File file = new File(FileUtils.getImagePath(getActivity(), "icon" + ".jpg"));
                BitmapUtil.saveBitmap(BitmapUtil.drawableToBitmap(getActivity().getResources().getDrawable(R.mipmap.ic_launcher)), file);
                shareParams.setImagePath(file.getAbsolutePath());
                String url = "http://dl.qing.am/?type=recommend&from=singlemessage&isappinstalled=1";
                shareParams.setText(getActivity().getString(R.string.share_app_text) + url);
                shareParams.setTitle("推荐这个应用：轻元素");
                shareParams.setShareUrl(url);
                ShareHelper.share(getActivity(), null, shareParams);
            }
        });
        goLogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "switch_account"));
                dialog();
//                Intent goLoginAct = new Intent(getActivity(), RegisterAndLoginActivity.class);
//                startActivity(goLoginAct);
//                finish();
            }
        });

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "setting", UmengEvents.getEventMap("click", "service"));
                if (feedBackDialog == null) {
                    feedBackDialog = new FeedBackDialog(getActivity());
                }
                feedBackDialog.show();
            }
        });
        return rootView;

    }

    protected void dialog() {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setMessage("确定退出当前账号吗?");
        alertParams.setConfirmString("确定");
        alertParams.setCancelString("取消");
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserModel.getInstance().logout();
                dialog.dismiss();

                Intent goLoginAct = new Intent(getActivity(), RegisterAndLoginActivity.class);
                startActivity(goLoginAct);
                finish();
                getActivity().finish();

           //     EventBus.getDefault().post(new JumpPageEvent(GroupFragment.class));
            }
        });
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
    }

    public void setTitle() {
        titleText.setText("设置");
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
}
