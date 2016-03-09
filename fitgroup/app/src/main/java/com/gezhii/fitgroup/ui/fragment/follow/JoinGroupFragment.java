package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupTagDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.activity.RegisterAndLoginActivity;
import com.gezhii.fitgroup.ui.adapter.GroupTagListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.TagGroupListFragment;
import com.gezhii.fitgroup.ui.fragment.group.CreateGroupFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/22.
 */
public class JoinGroupFragment extends BaseFragment {
    public static final String TAG = JoinGroupFragment.class.getName();

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
    @InjectView(R.id.group_tags_list_view)
    LoadMoreListView groupTagsListView;

    GroupTagListAdapter groupTagListAdapter;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(JoinGroupFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.join_group_fragment, null);
        ButterKnife.inject(this, rootView);
        setView();
        return rootView;
    }

    private void setView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleText.setText("加入公会");
        rightText.setText("创建公会");
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
        groupTagsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                groupTagsListView.setAdapter(groupTagListAdapter);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


}
