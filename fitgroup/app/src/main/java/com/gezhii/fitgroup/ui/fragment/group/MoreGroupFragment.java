package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupTagDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qrcode.scanner.CaptureActivity;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupTagListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.SearchGroupFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.TagGroupListFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/16.
 */
public class MoreGroupFragment extends BaseFragment {

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
    @InjectView(R.id.search_group_input)
    TextView searchGroupInput;
    @InjectView(R.id.scanner_qr_code_btn)
    ImageView scannerQrCodeBtn;
    // @InjectView(R.id.group_tags_list_view)
    LoadMoreListView groupTagsListView;
    @InjectView(R.id.empty_layout)
    LinearLayout emptyLayout;
    GroupTagListAdapter groupTagListAdapter;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(MoreGroupFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.more_group_fragment, null);
        ButterKnife.inject(this, rootView);
        initTitleView();
        groupTagsListView = (LoadMoreListView) rootView.findViewById(R.id.group_tags_list_view);
        MobclickAgent.onEvent(getActivity(), "more_group_tags_list", UmengEvents.getEventMap("click", "load"));
        searchGroupInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "more_group_tags_list", UmengEvents.getEventMap("click", "search_group"));
                ((MainActivity) getActivity()).showNext(SearchGroupFragment.class, null);
            }
        });
        scannerQrCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "more_group_tags_list", UmengEvents.getEventMap("click", "scan_qcode"));
                CaptureActivity.start(getActivity());
            }
        });
        API.GetGroupTagsConfig(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                final GroupTagDTO groupTagDTO = GroupTagDTO.parserJson(response);
                groupTagListAdapter = new GroupTagListAdapter();
                groupTagListAdapter.setGroup_tag_configs(groupTagDTO.getGroup_tag_configs());
                groupTagListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                    @Override
                    public void onListItemClick(View v, int position) {
                        MobclickAgent.onEvent(getActivity(), "more_group_tags_list", UmengEvents.getEventMap("click", "go_to_tag_group_list"));
                        int tag_id = groupTagDTO.getGroup_tag_configs().get(position).getId();
                        String tag_name = groupTagDTO.getGroup_tag_configs().get(position).getName();
                        TagGroupListFragment.start(getActivity(), tag_id, tag_name);
                    }
                });

                groupTagsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
                groupTagsListView.setAdapter(groupTagListAdapter);
            }
        });

        return rootView;
    }

    private void initTitleView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightImg.setVisibility(View.INVISIBLE);
        rightText.setText("创建公会");
        titleText.setText("更多公会");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
