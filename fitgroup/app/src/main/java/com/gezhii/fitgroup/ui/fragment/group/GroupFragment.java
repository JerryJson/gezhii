package com.gezhii.fitgroup.ui.fragment.group;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupSquareDTO;
import com.gezhii.fitgroup.dto.GroupTagDTO;
import com.gezhii.fitgroup.event.ApplicationUnCountChangeEvent;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.event.ScannerQRCodeResultEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.UserDataChangeEvent;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qrcode.scanner.CaptureActivity;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.activity.RegisterAndLoginActivity;
import com.gezhii.fitgroup.ui.adapter.GroupFragmentChatAdapter;
import com.gezhii.fitgroup.ui.adapter.GroupTagListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.GroupSquareFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.SearchGroupFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.TagGroupListFragment;
import com.gezhii.fitgroup.ui.fragment.group.leader.GroupLeaderReportFragment;
import com.gezhii.fitgroup.ui.fragment.group.leader.GroupMemberApplicationListFragment;
import com.gezhii.fitgroup.ui.fragment.me.InvitationCodeFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/26.
 */
public class GroupFragment extends BaseFragment {

    public static final String TAG = GroupFragment.class.getName();


    private long lastRefreshTime;

    @InjectView(R.id.search_group_input)
    TextView searchGroupInput;

    @InjectView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @InjectView(R.id.group_title)
    TextView groupTitle;
    @InjectView(R.id.group_description)
    TextView groupDescription;
    @InjectView(R.id.briefing_text)
    TextView briefingText;
    @InjectView(R.id.group_application_btn)
    TextView groupApplicationBtn;
    @InjectView(R.id.group_master_layout)
    LinearLayout groupMasterLayout;
    @InjectView(R.id.into_group_btn)
    TextView intoGroupBtn;
    @InjectView(R.id.new_message_count)
    TextView newMessageCount;
    @InjectView(R.id.new_message_count_layout)
    LinearLayout newMessageCountLayout;
    @InjectView(R.id.create_group_btn)
    LinearLayout createGroupBtn;
    @InjectView(R.id.checking_empty_into_group_btn)
    FrameLayout checkingEmptyIntoGroupBtn;
    @InjectView(R.id.group_audit_layout)
    FrameLayout groupAuditLayout;
    @InjectView(R.id.new_message_list_view)
    RecyclerView newMessageListView;
    @InjectView(R.id.normal_layout)
    LinearLayout normalLayout;
    @InjectView(R.id.left_text)
    TextView leftText;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.scanner_qr_code_btn)
    ImageView scannerQrCodeBtn;
//    @InjectView(R.id.new_bie_text)
//    TextView newBieText;
//    @InjectView(R.id.add_new_bee_btn)
//    TextView addNewBeeBtn;
//    @InjectView(R.id.new_bee_layout)
//    LinearLayout newBeeLayout;

    GroupFragmentChatAdapter mGroupFragmentChatAdapter;
    @InjectView(R.id.into_group_layout)
    RelativeLayout intoGroupLayout;
    @InjectView(R.id.into_group_img)
    ImageView intoGroupImg;
    @InjectView(R.id.unread_message_text)
    TextView unreadMessageText;
    @InjectView(R.id.briefing_img)
    ImageView briefingImg;

    @InjectView(R.id.group_activeness_text)
    TextView groupActivenessText;
    @InjectView(R.id.member_contribution_text)
    TextView memberContributionText;
    @InjectView(R.id.my_ranking_text)
    TextView myRankingText;
    @InjectView(R.id.my_ranking_layout)
    LinearLayout myRankingLayout;
    @InjectView(R.id.active_layout)
    LinearLayout activeLayout;
    @InjectView(R.id.continue_sign_num_text)
    TextView continueSignNumText;
    @InjectView(R.id.continue_sign_num_layout)
    LinearLayout continueSignNumLayout;
    @InjectView(R.id.member_contribution_layout)
    LinearLayout memberContributionLayout;
    @InjectView(R.id.group_sort_layout)
    LinearLayout groupSortLayout;

    @InjectView(R.id.delete_create_group_prompt)
    ImageView deleteCreateGroupPrompt;
    @InjectView(R.id.create_own_group_layout)
    LinearLayout createOwnGroupLayout;
    @InjectView(R.id.new_message_list_fragment)
    FrameLayout newMessageListFragment;

    GroupSquareDTO groupSquareDTO;


    @InjectView(R.id.group_tags_list_view)
    LoadMoreListView groupTagsListView;

    GroupTagListAdapter groupTagListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "load"));
        newMessageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        leftText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserModel.getInstance().isLogin()) {
                    ((MainActivity) getActivity()).showNext(InvitationCodeFragment.class, null);
                } else {
                    RegisterAndLoginActivity.start(getActivity());
                }

            }
        });
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserModel.getInstance().isLogin()) {
                    if (UserModel.getInstance().getUserDto().getUser().getLevel() > 2) {
                        CreateGroupFragment.start(getActivity());
                    } else {
                        showToast("创建工会需要等级到达3级");
                    }
                } else {
                    RegisterAndLoginActivity.start(getActivity());
                }

            }
        });
        searchGroupInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "search_group"));
                ((MainActivity) getActivity()).showNext(SearchGroupFragment.class, null);
            }
        });
        scannerQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "scan_qcode"));
                CaptureActivity.start(getActivity());
            }
        });
        groupSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreGroupFragment.start(getActivity());
            }
        });
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
        setView(false);
    }

    private void setView(boolean isRefresh) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("fitGroup", 0);
        String date = sharedPreferences.getString("briefingDate", "0");

        if (UserModel.getInstance().isLogin() && UserModel.getInstance().getMyGroup() != null && UserModel.getInstance().getUserDto().getUser().getIsChecking() == 0) {
            MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "有公会的公会tab"));
            normalLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            groupAuditLayout.setVisibility(View.GONE);
            newMessageListFragment.setVisibility(View.VISIBLE);
            newMessageListView.setVisibility(View.VISIBLE);
            newMessageCountLayout.setVisibility(View.VISIBLE);
            groupTitle.setText(UserModel.getInstance().getMyGroup().getGroup_name());
            String groupDescriptionString = UserModel.getInstance().getMyGroup().getLevel() + "级公会" + " | " + String.format(getString(R.string.group_member_count), String.valueOf(UserModel.getInstance().getMyGroup().getMember_count()));
            groupDescription.setText(groupDescriptionString);

            briefingText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "简报"));
                    ((MainActivity) getActivity()).showNext(GroupLeaderReportFragment.class, null);
                }
            });
            if (UserModel.getInstance().getMyGroup().getLeader().getId() == UserModel.getInstance().getUserId()) {
                groupMasterLayout.setVisibility(View.VISIBLE);
                activeLayout.setVisibility(View.VISIBLE);
                memberContributionLayout.setVisibility(View.VISIBLE);
                myRankingLayout.setVisibility(View.INVISIBLE);
                continueSignNumLayout.setVisibility(View.INVISIBLE);
                groupActivenessText.setText((int) Math.floor(UserModel.getInstance().getUserDto().getGroup().getTodayGroupDailyStatistics().getActiveness() * 100) + "%");
                memberContributionText.setText(String.valueOf(UserModel.getInstance().getMyGroup().getTotal_contribution_value()));
            } else {
                if (UserModel.getInstance().isNeedPromptCreateGroup()) {
                    createOwnGroupLayout.setVisibility(View.VISIBLE);
                    deleteCreateGroupPrompt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserModel.getInstance().setNotNeedPromptCreateGroup();
                            createOwnGroupLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    createOwnGroupLayout.setVisibility(View.GONE);
                }
                groupMasterLayout.setVisibility(View.GONE);
                activeLayout.setVisibility(View.INVISIBLE);
                memberContributionLayout.setVisibility(View.INVISIBLE);
                myRankingLayout.setVisibility(View.VISIBLE);
                continueSignNumLayout.setVisibility(View.VISIBLE);
                myRankingText.setText(UserModel.getInstance().getUserDto().getGroup_member().getUser_sort_value() + "/" + UserModel.getInstance().getMyGroup().getMember_count());
                continueSignNumText.setText(String.valueOf(UserModel.getInstance().getUserDto().getUser().getSignin_count()));
            }
            intoGroupLayout.setEnabled(true);
            intoGroupImg.setVisibility(View.VISIBLE);
            intoGroupBtn.setText(R.string.into_group);
            if (TimeHelper.getInstance().getTodayString().equals(date)) {
                briefingImg.setVisibility(View.INVISIBLE);
            } else {
                briefingImg.setVisibility(View.VISIBLE);
            }
            refreshChatList();
        } else if (UserModel.getInstance().isLogin() && UserModel.getInstance().getUserDto().getUser().getIsChecking() == 1) {
            MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "审核中公会tab"));
            normalLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            groupAuditLayout.setVisibility(View.VISIBLE);
            newMessageListView.setVisibility(View.GONE);
            newMessageListView.setVisibility(View.GONE);
            newMessageCountLayout.setVisibility(View.GONE);
            groupTitle.setText(UserModel.getInstance().getMyGroup().getGroup_name());
            String groupDescriptionString = UserModel.getInstance().getMyGroup().getLevel() + "级公会" + " | " + String.format(getString(R.string.group_member_count), String.valueOf(UserModel.getInstance().getMyGroup().getMember_count()));
            groupDescription.setText(groupDescriptionString);
            activeLayout.setVisibility(View.INVISIBLE);
            memberContributionLayout.setVisibility(View.INVISIBLE);
            myRankingLayout.setVisibility(View.VISIBLE);
            continueSignNumLayout.setVisibility(View.VISIBLE);
            myRankingText.setText("?/" + UserModel.getInstance().getMyGroup().getMember_count());
            continueSignNumText.setText("0");
            intoGroupLayout.setEnabled(false);
            intoGroupImg.setVisibility(View.GONE);
            intoGroupBtn.setText(R.string.audit_text);
            groupMasterLayout.setVisibility(View.GONE);
        } else {
            MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "groups_tag"));
            MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "load"));
            normalLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
            if (System.currentTimeMillis() - lastRefreshTime > 2 * 60 * 1000 || isRefresh) {
                EventBus.getDefault().post(new ShowLoadingEvent());
                int user_id;
                if (UserModel.getInstance().isLogin()) {
                    user_id = UserModel.getInstance().getUserDto().getUser().getId();
                } else {
                    user_id = -1;
                }

//                API.getGroupSquare(user_id, new APICallBack() {
//                    @Override
//                    public void subRequestSuccess(String response) {
//                        EventBus.getDefault().post(new CloseLoadingEvent());
//                        groupSquareDTO = GroupSquareDTO.parserJson(response);
//                        //initCardList(groupSquareDTO);
//
//                    }
//                });

                API.GetGroupTagsConfig(new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        final GroupTagDTO groupTagDTO = GroupTagDTO.parserJson(response);
                        MobclickAgent.onEvent(getActivity(), "square", UmengEvents.getEventMap("click", "tag_list"));
                        groupTagListAdapter = new GroupTagListAdapter();
                        groupTagListAdapter.setGroup_tag_configs(groupTagDTO.getGroup_tag_configs());
                        groupTagListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                            @Override
                            public void onListItemClick(View v, int position) {

                                int tag_id = groupTagDTO.getGroup_tag_configs().get(position).getId();
                                String tag_name = groupTagDTO.getGroup_tag_configs().get(position).getName();
                                TagGroupListFragment.start(getActivity(), tag_id, tag_name);
                            }
                        });

                        groupTagsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        groupTagsListView.setAdapter(groupTagListAdapter);
                    }
                });

                lastRefreshTime = System.currentTimeMillis();
            }
        }
    }


    public void onEvent(final ScannerQRCodeResultEvent scannerQRCodeResultEvent) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("sacn", "scan_qcode_success"));
        if ((!UserModel.getInstance().isLogin() || UserModel.getInstance().getMyGroup() == null) && getActivity().getSupportFragmentManager().getBackStackEntryCount() == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SearchGroupFragment.start(getActivity(), scannerQRCodeResultEvent.getCode());
                }
            }, 1000);
        }

    }

    @OnClick(R.id.my_ranking_layout)
    public void onMyRankingLayout(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "排名"));
        ((MainActivity) getActivity()).showNext(GroupMemberListFragment.class, null);
    }

    @OnClick(R.id.continue_sign_num_layout)
    public void onContributionText(View v) {
        SignHistoryFragment.start(getActivity(), UserModel.getInstance().getUserDto().getUser());
    }

    @OnClick(R.id.active_layout)
    public void onActiveLayout(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "会长 活跃度"));
        GroupSigninDailyStatisticFragment.start(getActivity());
    }

    @OnClick(R.id.member_contribution_layout)
    public void onMemberContributionText(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "会长 贡献值"));
        ((MainActivity) getActivity()).showNext(GroupMemberListFragment.class, null);
    }

    @OnClick(R.id.create_group_btn)
    public void onCreateGroupBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "创建公会"));
        if (UserModel.getInstance().isLogin() && UserModel.getInstance().getUserDto().getUser().getLevel() > 2) {
            if (UserModel.getInstance().getUserDto().getUser().getIsChecking() == 0 && UserModel.getInstance().getUserDto().getGroup() != null) {
                showToast("你已经是" + UserModel.getInstance().getMyGroup().getGroup_name() + "的成员，如果想创建公会，请先退出该公会。");
            } else {
                CreateGroupFragment.start(getActivity());
            }
        } else {
            showToast("你的等级还不够哦，三级才能创建公会！");
        }

    }


    @OnClick(R.id.checking_empty_into_group_btn)
    public void onEmptyIntoGroupBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "公会传送门"));
        ((MainActivity) getActivity()).showNext(GroupSquareFragment.class, null);
    }


    @OnClick(R.id.into_group_layout)
    public void onIntoGroupBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "进入公会"));
        GroupChatFragment.start(getActivity());
    }


    @OnClick(R.id.group_application_btn)
    public void onGroupApplicationBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "group", UmengEvents.getEventMap("click", "申请列表"));
        GroupMemberApplicationListFragment.start(getActivity());
    }

    public void onEventMainThread(GroupMessageEvent groupMessageEvent) {
        refreshChatList();
    }

    //
    public void onEventMainThread(UserDataChangeEvent userDataChangeEvent) {
        setView(true);
    }

    private void refreshChatList() {
        Log.i("darren ---isLoginHuanxin",EMChat.getInstance().isLoggedIn());
        Log.i("darren", "huanxin_id: " + UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id());
        if (EMChat.getInstance().isLoggedIn()) {
            EMConversation conversation = EMChatManager.getInstance().getConversation(UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id());
            List<EMMessage> mEMMesageList = new ArrayList<>();

            mEMMesageList.addAll(conversation.getAllMessages());

            if (mEMMesageList.size() > 3) {
                mEMMesageList.subList(0, mEMMesageList.size() - 3).clear();
            }

            if (mGroupFragmentChatAdapter == null) {
                mGroupFragmentChatAdapter = new GroupFragmentChatAdapter(getActivity(), mEMMesageList);

            }
            mGroupFragmentChatAdapter.setEmMessageList(mEMMesageList);
            newMessageListView.setAdapter(mGroupFragmentChatAdapter);
        } else {
            registerChatServer();
        }

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

    public void onEventMainThread(ApplicationUnCountChangeEvent applicationUnCountChangeEvent) {
        int count = PrivateMessageModel.getInstance().getMessageDto().getApplicationUnReadCount();
        Log.i("count", count);
        if (count != 0) {
            unreadMessageText.setVisibility(View.VISIBLE);
            unreadMessageText.setText(count + "");
        } else {
            unreadMessageText.setVisibility(View.INVISIBLE);
        }
    }

    static class CardViewHolder {
        @InjectView(R.id.card_title_text)
        TextView cardTitleText;
        @InjectView(R.id.item_list_layout)
        LinearLayout itemListLayout;
        @InjectView(R.id.check_all_groups_layout)
        LinearLayout checkAllGroupsLayout;

        CardViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class ItemViewHolder {
        @InjectView(R.id.group_name_text)
        TextView groupNameText;
        @InjectView(R.id.group_level_text)
        TextView groupLevelText;
        @InjectView(R.id.leader_icon_img)
        ImageView leaderIconImg;
        @InjectView(R.id.group_info_text)
        TextView groupInfoText;

        ItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
