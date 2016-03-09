package com.gezhii.fitgroup.ui.fragment.plan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.NetUtils;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.VipUsersDTO;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.dto.basic.Video;
import com.gezhii.fitgroup.event.MentorChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.me.SportScheduleFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/18.
 */
public class GetVipUsersFragment extends BaseFragment {

    @InjectView(R.id.exercise_by_myself)
    TextView exerciseByMyself;
    @InjectView(R.id.vip_user_viewpager)
    ViewPager vipUserViewpager;
    ArrayList<View> viewArrayList;
    ArrayList<User> userArrayList;
    List<Tag> tagsList;

    List<Integer> tagIdsList;

    boolean isFromChangePerson = false;
    List<Tag> userOwnTags;//用户已打tags
    @InjectView(R.id.back_btn)
    ImageView backBtn;

    public static void start(Activity activity, List<Tag> list) {
        start(activity, list, false);
    }

    public static void start(Activity activity, List<Tag> list, boolean isFromChangePerson) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("tags", list);
        hashMap.put("isFromChangePerson", isFromChangePerson);
        mainActivity.showNext(GetVipUsersFragment.class, hashMap);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.get_vip_user_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        viewArrayList = new ArrayList<>();
        userArrayList = new ArrayList<>();
        isFromChangePerson = (boolean) getNewInstanceParams().get("isFromChangePerson");
        if (isFromChangePerson) {
            MobclickAgent.onEvent(getActivity(), "VIPUsers", UmengEvents.getEventMap("click", "load", "from", "userTasks"));
            userOwnTags = UserModel.getInstance().getUserDto().getUser().getTags();
            API.getVipUsers(UserModel.getInstance().getUserId(), new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    VipUsersDTO vipUsersDTO = VipUsersDTO.parserJson(response);
                    for (int i = 0; i < vipUsersDTO.getUsers().size(); i++) {
                        final User user = vipUsersDTO.getUsers().get(i);
                        String description = "";
                        View vipItem = LayoutInflater.from(getActivity()).inflate(R.layout.vip_user_item, null);
                        ViewHolder viewHolder = new ViewHolder(vipItem);
                        //------1.4--
                        if (user.getVideos() != null && user.getVideos().size() > 0) {
                            for (int x = 0; x < user.getVideos().size(); x++) {
                                if (x < 2) {
                                    View view = View.inflate(getActivity(),R.layout.video_show_layout,null);
//                                    ImageView imageView = new ImageView(getActivity());
//                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Screen.dip2px(60), Screen.dip2px(60));
//                                    layoutParams.rightMargin = Screen.dip2px(5);
//                                    imageView.setLayoutParams(layoutParams);
                                    ImageView imageView = (ImageView) view.findViewById(R.id.task_course_img);
                                    QiniuHelper.bindVideoThumbnaiImage(user.getVideos().get(x).getVideo_link(), imageView);
                                    final int finalX = x;
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(NetUtils.isWifiConnection(getActivity())){
                                                Intent intents = new Intent(Intent.ACTION_VIEW);
                                                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(user.getVideos().get(finalX).getVideo_link())), "video/*");
                                                startActivity(intents);
                                            }else{
                                                AlertHelper.AlertParams params = new AlertHelper.AlertParams();
                                                params.setMessage("您当前为非WiFi网络环境,建议您在WiFi环境下观看,是否继续？");
                                                params.setCancelString("取消");
                                                params.setCancelListener(new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                params.setConfirmString("确定");
                                                params.setConfirmListener(new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intents = new Intent(Intent.ACTION_VIEW);
                                                        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(user.getVideos().get(finalX).getVideo_link())), "video/*");
                                                        startActivity(intents);
                                                    }
                                                });
                                                AlertHelper.showAlert(getActivity(),params);
                                            }
                                        }
                                    });
                                    viewHolder.vipUserVideoCourse.addView(view);
                                }
                            }
                        }
                        //-----------
                        viewHolder.userNameText.setText(user.getNick_name());
                        QiniuHelper.bindAvatarImage(user.getIcon(), viewHolder.userProfileIcon);

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
                            viewHolder.vipDescriptionText.setText(description);
                        }

                        List<Tag> vipUserTags = user.getTags();
                        String commenTags = "";
                        for (int j = 0; j < vipUserTags.size(); j++) {
                            for (int k = 0; k < userOwnTags.size(); k++) {
                                if (vipUserTags.get(j).getName().equals(userOwnTags.get(k).getName())) {
                                    commenTags = commenTags + "#" + vipUserTags.get(j).getName();
                                }
                            }
                        }
                        String str = "相同兴趣点：" + commenTags;
                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 6, 0);
                        viewHolder.commenInsterestingText.setText(style);
                        viewArrayList.add(vipItem);
                        userArrayList.add(user);
                    }
                    vipUserViewpager.setAdapter(new VipUserAdapter(getActivity(), viewArrayList, userArrayList));
                }
            });
        } else {
            MobclickAgent.onEvent(getActivity(), "VIPUsers", UmengEvents.getEventMap("click", "load", "from", "tag"));
            tagsList = new ArrayList<>();
            tagIdsList = new ArrayList<>();
            tagsList = (List<Tag>) getNewInstanceParams().get("tags");
            for (int i = 0; i < tagsList.size(); i++) {
                tagIdsList.add(tagsList.get(i).getId());
            }
            API.updateUserTags(UserModel.getInstance().getUserId(), tagIdsList, new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {

                    VipUsersDTO vipUsersDTO = VipUsersDTO.parserJson(response);
                    for (int i = 0; i < vipUsersDTO.getUsers().size(); i++) {
                        User user = vipUsersDTO.getUsers().get(i);
                        View vipItem = LayoutInflater.from(getActivity()).inflate(R.layout.vip_user_item, null);
                        ViewHolder viewHolder = new ViewHolder(vipItem);
                        viewHolder.userNameText.setText(user.getNick_name());
                        QiniuHelper.bindAvatarImage(user.getIcon(), viewHolder.userProfileIcon);
                        viewHolder.vipDescriptionText.setText(user.getSelf_description());

                        List<Tag> vipUserTags = user.getTags();
                        String commenTags = "";
                        for (int j = 0; j < vipUserTags.size(); j++) {
                            for (int k = 0; k < tagsList.size(); k++) {
                                if (vipUserTags.get(j).getName().equals(tagsList.get(k).getName())) {
                                    commenTags = commenTags + "#" + vipUserTags.get(j).getName();
                                }
                            }
                        }
                        String str = "相同兴趣点：" + commenTags;
                        SpannableStringBuilder style = new SpannableStringBuilder(str);
                        style.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 6, 0);
                        viewHolder.commenInsterestingText.setText(style);
                        viewArrayList.add(vipItem);
                        userArrayList.add(user);
                    }
                    vipUserViewpager.setAdapter(new VipUserAdapter(getActivity(), viewArrayList, userArrayList));
                    UserModel.getInstance().tryLoadRemote(true);
                }
            });
        }

        exerciseByMyself.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                API.adoptMentor(UserModel.getInstance().getUserId(), -1, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        UserModel.getInstance().getUserDto().getUser().setMentor_id(-1);
                        EventBus.getDefault().post(new MentorChangeEvent());
                        MainActivity m = (MainActivity) getActivity();
                        m.finishAll();
                    }
                });

            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public static class VipUserAdapter extends PagerAdapter {
        ArrayList<View> viewArrayList;
        ArrayList<User> userArrayList;
        Activity mContext;

        public VipUserAdapter(Activity activity, ArrayList<View> viewArrayList, ArrayList<User> userArrayList) {
            this.viewArrayList = viewArrayList;
            this.userArrayList = userArrayList;
            this.mContext = activity;
        }

        @Override
        public int getCount() {
            return viewArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            viewArrayList.get(position).findViewById(R.id.go_sport_schedule_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SportScheduleFragment.start(mContext, userArrayList.get(position).getId(), userArrayList.get(position).getIcon());
                }
            });
            viewArrayList.get(position).findViewById(R.id.join_with_vip_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    API.adoptMentor(UserModel.getInstance().getUserId(), userArrayList.get(position).getId(), new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            Log.i("logger", "   " + userArrayList.get(position).getId());
                            EventBus.getDefault().post(new MentorChangeEvent());
                            MainActivity m = (MainActivity) mContext;
                            m.finishAll();
                        }
                    });
                }
            });
            container.addView(viewArrayList.get(position), 0);
            return viewArrayList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewArrayList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'vip_user_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.commen_insteresting_text)
        TextView commenInsterestingText;
        @InjectView(R.id.commen_insteresting_layout)
        LinearLayout commenInsterestingLayout;
        @InjectView(R.id.user_profile_icon)
        RectImageView userProfileIcon;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.vip_description_text)
        TextView vipDescriptionText;
        @InjectView(R.id.go_sport_schedule_btn)
        LinearLayout goSportScheduleBtn;
        @InjectView(R.id.join_with_vip_btn)
        RelativeLayout joinWithVipBtn;
        @InjectView(R.id.vip_user_video_course)
        LinearLayout vipUserVideoCourse;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
