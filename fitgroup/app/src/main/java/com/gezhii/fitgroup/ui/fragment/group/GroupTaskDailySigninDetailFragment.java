package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupTaskDailySigninDetailDTO;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupTaskDailySigninDetailFragment extends BaseFragment {

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
    @InjectView(R.id.signin_title_text)
    TextView signinTitleText;
    @InjectView(R.id.signin_layout)
    LinearLayout signinLayout;
    @InjectView(R.id.unsignin_title_text)
    TextView unsigninTitleText;
    @InjectView(R.id.unsignin_layout)
    LinearLayout unsigninLayout;


    public static void start(Activity activity, int task_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("task_id", task_id);
        mainActivity.showNext(GroupTaskDailySigninDetailFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_task_daily_signin_detail_fragment, null);
        ButterKnife.inject(this, rootView);
        setView();
        return rootView;
    }

    public void setView() {
        titleText.setText("完成详情");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        EventBus.getDefault().post(new ShowLoadingEvent());

        int task_id = (int) getNewInstanceParams().get("task_id");
        API.GetGroupTaskDailySignin(UserModel.getInstance().getGroupId(), task_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());

                GroupTaskDailySigninDetailDTO groupTaskDailySigninDetailDTO = GroupTaskDailySigninDetailDTO.parserJson(response);

                if (groupTaskDailySigninDetailDTO.signin_members.size() > 0) {
                    for (int i = 0; i < groupTaskDailySigninDetailDTO.signin_members.size(); i++) {
                        GroupMember member = groupTaskDailySigninDetailDTO.signin_members.get(i);

                        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_task_daily_signin_detail_item, null);
                        ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);

                        if (!TextUtils.isEmpty(member.getUser().getIcon()))
                            QiniuHelper.bindImage(member.getUser().getIcon(), itemViewHolder.groupMemberIcon);

                        itemViewHolder.groupMemberNameText.setText(member.getUser().getNick_name());
                        itemViewHolder.groupMemberLevelText.setText("" + member.getUser().getLevel() + "级");

                        itemViewHolder.badgesContainer.removeAllViews();
                        for (int j = 0; j < member.getUser().getBadges().size(); j++) {
                            if (j <= 7) {
                                ImageView badgeView = new ImageView(itemView.getContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15), Screen.dip2px(15));
                                params.setMargins(0, 0, Screen.dip2px(4), 0);
                                badgeView.setLayoutParams(params);
                                if (member.getUser().getBadges().get(j).badge != null) {
                                    QiniuHelper.bindAvatarImage(member.getUser().getBadges().get(j).badge.getIcon(), badgeView);
                                    itemViewHolder.badgesContainer.addView(badgeView);
                                }
                            }

                        }

                        itemViewHolder.groupTaskSigninDateText.setText(TimeHelper.hourFormat.format(member.getSign_time()));
                        signinLayout.addView(itemView);
                    }
                } else {
                    signinTitleText.setVisibility(View.GONE);
                    signinLayout.setVisibility(View.GONE);
                }


                if (groupTaskDailySigninDetailDTO.unsignin_members.size() > 0) {
                    for (int i = 0; i < groupTaskDailySigninDetailDTO.unsignin_members.size(); i++) {
                        GroupMember member = groupTaskDailySigninDetailDTO.unsignin_members.get(i);

                        View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_task_daily_signin_detail_item, null);
                        ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);

                        if (!TextUtils.isEmpty(member.getUser().getIcon()))
                            QiniuHelper.bindImage(member.getUser().getIcon(), itemViewHolder.groupMemberIcon);

                        itemViewHolder.groupMemberNameText.setText(member.getUser().getNick_name());
                        itemViewHolder.groupMemberLevelText.setText("" + member.getUser().getLevel() + "级");

                        itemViewHolder.badgesContainer.removeAllViews();
                        for (int j = 0; j < member.getUser().getBadges().size(); j++) {
                            if (j <= 7) {
                                ImageView badgeView = new ImageView(itemView.getContext());
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15), Screen.dip2px(15));
                                params.setMargins(0, 0, Screen.dip2px(4), 0);
                                badgeView.setLayoutParams(params);
                                if (member.getUser().getBadges().get(j).badge != null) {
                                    QiniuHelper.bindAvatarImage(member.getUser().getBadges().get(j).badge.getIcon(), badgeView);
                                    itemViewHolder.badgesContainer.addView(badgeView);
                                }
                            }

                        }

                        itemViewHolder.groupTaskSigninDateLayout.setVisibility(View.GONE);

                        unsigninLayout.addView(itemView);
                    }
                } else {
                    unsigninTitleText.setVisibility(View.GONE);
                    unsigninLayout.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    static class ItemViewHolder {
        @InjectView(R.id.group_member_icon)
        RectImageView groupMemberIcon;
        @InjectView(R.id.group_member_name_text)
        TextView groupMemberNameText;
        @InjectView(R.id.group_member_level_text)
        TextView groupMemberLevelText;
        @InjectView(R.id.badges_container)
        LinearLayout badgesContainer;
        @InjectView(R.id.group_task_signin_date_text)
        TextView groupTaskSigninDateText;
        @InjectView(R.id.group_task_signin_date_layout)
        LinearLayout groupTaskSigninDateLayout;

        ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

    }
}
