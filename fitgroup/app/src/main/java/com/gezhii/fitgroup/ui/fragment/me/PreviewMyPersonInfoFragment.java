package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/19.
 */
public class PreviewMyPersonInfoFragment extends BaseFragment {
    @InjectView(R.id.edit_person_info_btn)
    TextView editPersonInfoBtn;
    @InjectView(R.id.user_profile_icon)
    RectImageView userProfileIcon;
    @InjectView(R.id.user_name_text)
    TextView userNameText;
    @InjectView(R.id.vip_description_text)
    TextView vipDescriptionText;
    @InjectView(R.id.sure_to_submit_person_info_btn)
    TextView sureToSubmitPersonInfoBtn;

    private User user;
    String description = "";

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(PreviewMyPersonInfoFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.preview_my_person_info_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        user = UserModel.getInstance().getUserDto().getUser();
        setView();
        return rootView;
    }

    public void setView() {
        editPersonInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sureToSubmitPersonInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity m = (MainActivity) getActivity();
                m.popToFragment(m.my_person_info_id);
            }
        });
        QiniuHelper.bindAvatarImage(user.getIcon(), userProfileIcon);
        userNameText.setText(user.getNick_name());


        if (!TextUtils.isEmpty(user.getAge_description())) {
            String age = user.getAge_description() + ",";
            description = description + age;
        }
        if (!TextUtils.isEmpty(user.getJob())) {
            String job = user.getJob() + ",";
            description = description + job;
        }
        if (!TextUtils.isEmpty(user.getSport_frequency())) {
            String frequency = user.getSport_frequency() + ",";
            description = description + frequency;
        }
        if (!TextUtils.isEmpty(user.getSport_way())) {
            String way = user.getSport_way() + ",";
            description = description + way;
        }
        if (!TextUtils.isEmpty(user.getSelf_description())) {
            description = description + user.getSelf_description();
        }
        if (description != null) {
            vipDescriptionText.setText(description);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
