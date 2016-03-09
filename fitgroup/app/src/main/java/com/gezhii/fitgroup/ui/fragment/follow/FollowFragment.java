package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.dto.HomePageDto;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.event.AboutMeMessageEvent;
import com.gezhii.fitgroup.event.FollowStateChangeEvent;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.event.GroupStateChangeEvent;
import com.gezhii.fitgroup.model.UserMessageModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.VideoPickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.AllChannelsFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.RecommendGroupListFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;


/**
 * Created by zj on 16/2/15.
 */
public class FollowFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FollowFragment.class.getName();

    @InjectView(R.id.reminded_btn)
    ImageView remindedBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.more_img)
    ImageView moreImg;
    @InjectView(R.id.reminded_state_img)
    ImageView remindedStateImg;
    @InjectView(R.id.follow_fragment_action_bar)
    RelativeLayout followfragmentActionBar;
    @InjectView(R.id.layout_follow_content)
    LinearLayout layoutFollowContent;

    @InjectView(R.id.layout_group)
    RelativeLayout layoutGroup;
    @InjectView(R.id.group_icon_img)
    RectImageView groupIconImg;
    @InjectView(R.id.group_name_text)
    TextView groupNameText;
    @InjectView(R.id.group_msg_unread_count_text)
    TextView groupMsgUnreadCountText;
    @InjectView(R.id.group_dynamic_text)
    TextView groupDynamicText;
    @InjectView(R.id.group_icon_null_layout)
    LinearLayout groupIconNullLayout;

    @InjectView(R.id.follow_layout)
    RelativeLayout followLayout;
    @InjectView(R.id.item_icon_img)
    ImageView itemIconImg;
    @InjectView(R.id.follow_layout_icon)
    LinearLayout followLayoutIcon;
    @InjectView(R.id.item_dynamic_state_view)
    View itemDynamicStateView;
    @InjectView(R.id.item_name_text)
    TextView itemNameText;
    @InjectView(R.id.item_describe_text)
    TextView itemDescribeText;
    @InjectView(R.id.follow_swiperefresh)
    SwipeRefreshLayout followSwiperefresh;

    private PopupWindow recommendMenu;
    private View recommendMenuView;
    private EMConversation conversation;
    private UserDto userDto;
    private VideoPickDialog videoPickDialog;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(FollowFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.follow_fragment, null);
        MobclickAgent.onEvent(getActivity(), "AttentionHome", UmengEvents.getEventMap("click", "load"));
        recommendMenuView = inflater.inflate(R.layout.recommend_menu, null);
        ButterKnife.inject(this, rootView);
        EventBus.getDefault().register(this);
        rootView.setOnTouchListener(this);
        followSwiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setView();
            }
        });
        setViewListener();
        setView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
    }

    private void setViewListener() {
        moreImg.setOnClickListener(this);
        remindedBtn.setOnClickListener(this);
        recommendMenuView.findViewById(R.id.recommend_group).setOnClickListener(this);
        recommendMenuView.findViewById(R.id.recommend_vip).setOnClickListener(this);
        recommendMenuView.findViewById(R.id.recommend_channel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_img:
                if (null == recommendMenu) {
                    initRecommedMenu();
                }
                if (recommendMenu.isShowing()) {
                    recommendMenu.dismiss();
                } else {
                    recommendMenu.showAsDropDown(followfragmentActionBar, Screen.getScreenWidth() - Screen.dip2px(160), 0);
                }
                break;
            case R.id.recommend_group:
                ((MainActivity) getActivity()).showNext(RecommendGroupListFragment.class, null);
                recommendMenu.dismiss();
                break;
            case R.id.recommend_vip:
                ((MainActivity) getActivity()).showNext(RecommendVipFragment.class, null);
                recommendMenu.dismiss();
                break;
            case R.id.recommend_channel:
                AllChannelsFragment.start(getActivity());
                MobclickAgent.onEvent(getActivity(), "RecommenChannel", UmengEvents.getEventMap("click", "load", "from", "recommend"));
                recommendMenu.dismiss();
                break;
            case R.id.reminded_btn:
                AboutMeMessageFragment.start(getActivity());
                remindedStateImg.setVisibility(View.INVISIBLE);
                UserMessageModel.getInstance().setHasNewMessage(false);
                break;
        }
    }

    private void setView() {
        userDto = UserModel.getInstance().getUserDto();
        if (UserMessageModel.getInstance().hasNewMessage()) {
            remindedStateImg.setVisibility(View.VISIBLE);
        } else {
            remindedStateImg.setVisibility(View.INVISIBLE);
        }
        if (UserModel.getInstance().isLogin()) {
            refreshGroup();
        }
        refreshFollowData();
    }

    private void refreshGroup() {
        API.getUserProfileHttp(UserModel.getInstance().getUserId(), new OnRequestEnd() {
            @Override
            public void onRequestSuccess(String response) {
                userDto = UserDto.parserJson(response);
                refreshGroupData(userDto);
            }

            @Override
            public void onRequestFail(VolleyError error) {
                userDto = UserModel.getInstance().getUserDto();
                refreshGroupData(userDto);
            }
        });
    }

    private void refreshGroupData(UserDto userDto) {
        groupNameText.setText("我的公会");
        if (userDto.getGroup() == null && userDto.getUser().getIsChecking() == 0) {
            groupDynamicText.setText("还没有加入公会");
            groupMsgUnreadCountText.setVisibility(View.GONE);
            groupIconNullLayout.setVisibility(View.VISIBLE);
            groupIconImg.setVisibility(View.GONE);
            layoutGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JoinGroupFragment.start(getActivity());
                }
            });
        } else if (userDto.getUser().getIsChecking() == 1) {
            groupDynamicText.setText("申请的公会还在审核中..");
            groupMsgUnreadCountText.setVisibility(View.GONE);
            groupIconNullLayout.setVisibility(View.VISIBLE);
            groupIconImg.setVisibility(View.GONE);
            layoutGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        } else {
            groupIconNullLayout.setVisibility(View.GONE);
            Group myGroup = UserModel.getInstance().getMyGroup();
            groupIconNullLayout.setVisibility(View.VISIBLE);
            groupIconImg.setVisibility(View.GONE);
            groupDynamicText.setText("");
//            QiniuHelper.bindImage(myGroup.getLeader().getIcon(), groupIconImg);
            refreshMessageCount();
            layoutGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupMsgUnreadCountText.setVisibility(View.GONE);
                    UserMessageModel.getInstance().setUnreadGroupMessageCount(0);
                    GroupChatFragment.start(getActivity());
                }
            });
        }
    }

    private void refreshMessageCount() {
        if (EMChat.getInstance().isLoggedIn()) {
            if(UserModel.getInstance().getUserDto().getGroup() != null){
                conversation = EMChatManager.getInstance().getConversation(UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id());
                if (UserMessageModel.getInstance().getUnreadGroupMessageCount() > 0) {
                    groupMsgUnreadCountText.setVisibility(View.VISIBLE);
                    if (UserMessageModel.getInstance().getUnreadGroupMessageCount() > 99) {
                        groupMsgUnreadCountText.setText("99+");
                    } else {
                        groupMsgUnreadCountText.setText(String.valueOf(UserMessageModel.getInstance().getUnreadGroupMessageCount()));
                    }
                } else {
                    groupMsgUnreadCountText.setVisibility(View.INVISIBLE);
                }
                if (conversation.getAllMessages() != null && conversation.getAllMessages().size() > 0) {
                    EMMessage emMessage = conversation.getMessage(conversation.getAllMessages().size() - 1);
                    if (emMessage.getType() == EMMessage.Type.TXT) {
                        groupDynamicText.setText(emMessage.getStringAttribute("sender_nick_name", "") + ":" +
                                ((TextMessageBody) emMessage.getBody()).getMessage());
                    } else {
                        groupDynamicText.setText(emMessage.getStringAttribute("sender_nick_name", "") +
                                " : [图片]");
                    }
                }
            }
        } else {
            registerChatServer();
        }
        layoutGroup.invalidate();
    }

    private void refreshFollowData() {
        API.getHomePage(UserModel.getInstance().getUserId(), new OnRequestEnd() {
            @Override
            public void onRequestSuccess(String response) {
                final HomePageDto homePageDto = HomePageDto.parseJson(response);
                followLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UserModel.getInstance().getUserDto().getUser().getFollowing_count() > 0) {
                            FollowUserMomentsFragment.start(getActivity());
                        } else {
                            ((MainActivity) getActivity()).showNext(RecommendVipFragment.class, null);
                        }
                    }
                });
                showFollowInfos(homePageDto, userDto);
            }

            @Override
            public void onRequestFail(VolleyError error) {

            }
        });
    }

    private void showFollowInfos(HomePageDto homePageDto, UserDto userDto) {
        layoutFollowContent.removeAllViews();
        if (userDto.getUser().getFollowing_count() > 0) {
            itemDescribeText.setText(homePageDto.getNewest_following_user_feeds());
        } else {
            itemDescribeText.setText("这些达人你会喜欢");
        }
        final List<Tag> followChannelList = homePageDto.getFollowing_channels();
        for (int i = 0; i < followChannelList.size(); i++) {
            View view = View.inflate(getActivity(), R.layout.follow_fragment_item, null);
            ChannelViewHolder channelViewHolder = new ChannelViewHolder(view);
            channelViewHolder.itemNameText.setText(followChannelList.get(i).getName());
            channelViewHolder.itemContentCountText.setText("已产生" + followChannelList.get(i).getContent_count() + "条内容");
            if (followChannelList.get(i).getUnread_count() != 0) {
                channelViewHolder.itemDynamicStateView.setVisibility(View.VISIBLE);
            } else {
                channelViewHolder.itemDynamicStateView.setVisibility(View.GONE);
            }
            QiniuHelper.bindChannelImage(followChannelList.get(i).getImg(),channelViewHolder.itemIconImg);
            view.setTag(followChannelList.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tag tag = (Tag) view.getTag();
                    tag.setUnread_count(0);
                    view.findViewById(R.id.item_dynamic_state_view).setVisibility(View.GONE);
                    ChannelProfileFragment.start(getActivity(), tag ,tag.getId());
                }
            });
            layoutFollowContent.addView(view);
        }
        followSwiperefresh.setRefreshing(false);
    }

    private void initRecommedMenu() {
        recommendMenu = new PopupWindow(recommendMenuView, Screen.dip2px(160), Screen.dip2px(159));
        recommendMenu.setOutsideTouchable(true);
        recommendMenu.setFocusable(true);
        recommendMenu.setBackgroundDrawable(new BitmapDrawable());
    }

    public void registerChatServer() {
        Log.i("darren ---huanxin_name", UserModel.getInstance().getUserHuanXinName());
        Log.i("darren ---huanxin_pwd", UserModel.getInstance().getUserDto().getUser().getHuanxin_password());
        EMChatManager.getInstance().login(UserModel.getInstance().getUserHuanXinName()
                , UserModel.getInstance().getUserDto().getUser().getHuanxin_password()
                , new EMCallBack() {
            @Override
            public void onSuccess() {
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                Log.d(getClass().getSimpleName(), "登陆聊天服务器成功！");
            }

            @Override
            public void onError(int i, String s) {
                Log.d(getClass().getSimpleName(), "registerChatServer onError " + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    public void onEventMainThread(FollowStateChangeEvent followStateChangeEvent) {
        refreshFollowData();
    }

    public void onEventMainThread(GroupStateChangeEvent groupStateChangeEvent) {
        refreshGroup();
    }

    public void onEventMainThread(GroupMessageEvent groupMessageEvent) {
        refreshMessageCount();
    }

    public void onEventMainThread(AboutMeMessageEvent aboutMeMessageEvent) {
        if (UserMessageModel.getInstance().hasNewMessage()) {
            remindedStateImg.setVisibility(View.VISIBLE);
        } else {
            remindedStateImg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    static class ChannelViewHolder {
        @InjectView(R.id.item_icon_img)
        RectImageView itemIconImg;
        @InjectView(R.id.item_dynamic_state_view)
        View itemDynamicStateView;
        @InjectView(R.id.item_name_text)
        TextView itemNameText;
        @InjectView(R.id.item_content_count_text)
        TextView itemContentCountText;

        public ChannelViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
