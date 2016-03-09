package com.gezhii.fitgroup.ui.fragment.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupSquareDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ScannerQRCodeResultEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.scanner.CaptureActivity;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/19.
 */
public class GroupSquareFragment extends BaseFragment {
    public static final String TAG = GroupSquareFragment.class.getName();
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.card_list_layout)
    LinearLayout cardListLayout;
    @InjectView(R.id.search_group_input)
    TextView searchGroupInput;
    @InjectView(R.id.scanner_qr_code_btn)
    ImageView scannerQrCodeBtn;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.group_tag_list_id = mainActivity.showNext(GroupSquareFragment.class, null);
        Log.i("darren",mainActivity.group_tag_list_id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_square_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "square");
        setTitle();

        searchGroupInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "square", UmengEvents.getEventMap("click", "search_group"));
                ((MainActivity) getActivity()).showNext(SearchGroupFragment.class, null);
            }
        });
        scannerQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "square", UmengEvents.getEventMap("click", "scan_qcode"));
                CaptureActivity.start(getActivity());
            }
        });
        EventBus.getDefault().register(this);

        EventBus.getDefault().post(new ShowLoadingEvent());
        int user_id;
        if (UserModel.getInstance().isLogin()) {
            user_id = UserModel.getInstance().getUserDto().getUser().getId();
        } else {
            user_id = -1;
        }

        API.getGroupSquare(user_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                GroupSquareDTO groupSquareDTO = GroupSquareDTO.parserJson(response);
                initCardList(groupSquareDTO);

            }
        });

        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    public void setTitle() {
        titleText.setText("加入公会");
        rightText.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onEvent(final ScannerQRCodeResultEvent scannerQRCodeResultEvent) {
        MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("scan", "scan_qcode_success"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SearchGroupFragment.start(getActivity(), scannerQRCodeResultEvent.getCode());
            }
        }, 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
//        EventBus.getDefault().post(new ShowLoadingEvent());
//        int user_id;
//        if (UserModel.getInstance().isLogin()) {
//            user_id = UserModel.getInstance().getUserDto().getUser().getId();
//        } else {
//            user_id = -1;
//        }
//
//        API.getGroupSquare(user_id, new APICallBack() {
//            @Override
//            public void subRequestSuccess(String response) {
//                EventBus.getDefault().post(new CloseLoadingEvent());
//                GroupSquareDTO groupSquareDTO = GroupSquareDTO.parserJson(response);
//                initCardList(groupSquareDTO);
//
//            }
//        });
    }

    View recommendCardView = null;
    View activeCardView = null;
    View sortCardView = null;

    public void initCardList(GroupSquareDTO groupSquareDTO) {
        if (recommendCardView != null) {
            cardListLayout.removeView(recommendCardView);
            cardListLayout.removeView(activeCardView);
            cardListLayout.removeView(sortCardView);
        }
        recommendCardView = LayoutInflater.from(getActivity()).inflate(R.layout.group_square_card_view, null);
        activeCardView = LayoutInflater.from(getActivity()).inflate(R.layout.group_square_card_view, null);
        sortCardView = LayoutInflater.from(getActivity()).inflate(R.layout.group_square_card_view, null);

        CardViewHolder recommendCardViewHolder = new CardViewHolder(recommendCardView);
        recommendCardViewHolder.cardTitleText.setText("推荐公会");
        recommendCardViewHolder.checkAllGroupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "check_all_recommend"));
                ((MainActivity) getActivity()).showNext(RecommendGroupListFragment.class, null);
            }
        });

        CardViewHolder activeCardViewHolder = new CardViewHolder(activeCardView);
        activeCardViewHolder.cardTitleText.setText(R.string.active_group);
        activeCardViewHolder.checkAllGroupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "check_all_active"));
                ((MainActivity) getActivity()).showNext(ActiveGroupListFragment.class, null);
            }
        });

        CardViewHolder sortCardViewHolder = new CardViewHolder(sortCardView);
        sortCardViewHolder.cardTitleText.setText(R.string.sort_group);
        sortCardViewHolder.checkAllGroupsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "check_all_sort"));
                ((MainActivity) getActivity()).showNext(ContributionGroupListFragment.class, null);
            }
        });

        for (int i = 0; i < groupSquareDTO.recommend_groups.size(); i++) {
            final Group group = groupSquareDTO.recommend_groups.get(i);

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_list_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            QiniuHelper.bindImage(group.getLeader().getIcon(), itemViewHolder.leaderIconImg);
            itemViewHolder.groupNameText.setText(group.getGroup_name());
            itemViewHolder.groupLevelText.setText("" + group.getLevel() + "级");
            itemViewHolder.groupInfoText.setText("会长: " + group.getLeader().getNick_name() + " | " + "创建时间: " + TimeHelper.dateFormat1.format(group.getCreated_time()));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "recommend_list"));
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("group_id", group.getId());
                    params.put("leader_huanxin_id", group.getLeader().getHuanxin_id());
                    ((MainActivity) getActivity()).showNext(GroupSimpleProfileFragment.class, params);
                }
            });

            recommendCardViewHolder.itemListLayout.addView(itemView);
        }

        for (int i = 0; i < groupSquareDTO.active_groups.size(); i++) {
            final Group group = groupSquareDTO.active_groups.get(i);

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_list_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            QiniuHelper.bindAvatarImage(group.getLeader().getIcon(), itemViewHolder.leaderIconImg);
            itemViewHolder.groupNameText.setText(group.getGroup_name());
            itemViewHolder.groupLevelText.setText("" + group.getLevel() + "级");

            if (group.yesterdayGroupDailyStatistics != null)
                itemViewHolder.groupInfoText.setText("会长: " + group.getLeader().getNick_name() + " | " + "活跃度: " + (int) Math.floor(group.yesterdayGroupDailyStatistics.getActiveness() * 100) + "%");
            else
                itemViewHolder.groupInfoText.setText("会长: " + group.getLeader().getNick_name() + " | " + "活跃度: 0");


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "active_list"));
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("group_id", group.getId());
                    params.put("leader_huanxin_id", group.getLeader().getHuanxin_id());
                    ((MainActivity) getActivity()).showNext(GroupSimpleProfileFragment.class, params);
                }
            });

            activeCardViewHolder.itemListLayout.addView(itemView);

        }

        for (int i = 0; i < groupSquareDTO.sort_groups.size(); i++) {
            final Group group = groupSquareDTO.sort_groups.get(i);

            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_list_item, null);
            ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
            QiniuHelper.bindAvatarImage(group.getLeader().getIcon(), itemViewHolder.leaderIconImg);
            itemViewHolder.groupNameText.setText(group.getGroup_name());
            itemViewHolder.groupLevelText.setText("" + group.getLevel() + "级");
            itemViewHolder.groupInfoText.setText("会长: " + group.getLeader().getNick_name() + " | " + "贡献值: " + Integer.toString(group.getTotal_contribution_value()));


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "sort_list"));
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("group_id", group.getId());
                    params.put("leader_huanxin_id", group.getLeader().getHuanxin_id());
                    ((MainActivity) getActivity()).showNext(GroupSimpleProfileFragment.class, params);
                }
            });
            sortCardViewHolder.itemListLayout.addView(itemView);
        }

        cardListLayout.addView(recommendCardView);
        cardListLayout.addView(activeCardView);
        cardListLayout.addView(sortCardView);

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
}
