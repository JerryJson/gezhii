package com.gezhii.fitgroup.ui.fragment.discovery;

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
import com.gezhii.fitgroup.dto.TagGroupsDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupListByTagAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/14.
 */
public class TagGroupListFragment extends BaseFragment {
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.recommend_group_list_view)
    LoadMoreListView recommendGroupListView;

    GroupListByTagAdapter groupListByTagAdapter;
    int tag_id;

    public static void start(Activity activity, int tag_id, String tag_name) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("tag_id", tag_id);
        params.put("tag_name", tag_name);
        mainActivity.showNext(TagGroupListFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_group_list_fragment, null);
        ButterKnife.inject(this, view);
        setTitle();
        MobclickAgent.onEvent(getActivity(), "group_tag_list", UmengEvents.getEventMap("click", "load"));
        recommendGroupListView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if (getNewInstanceParams().get("tag_id") != null) {
            tag_id = (int) getNewInstanceParams().get("tag_id");
            EventBus.getDefault().post(new ShowLoadingEvent());
            API.getGroupsByTag(1, tag_id, Config.loadPageCount, new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    EventBus.getDefault().post(new CloseLoadingEvent());
                    TagGroupsDTO tagGroupsDTO = TagGroupsDTO.parserJson(response);
                    initListAdapter(tagGroupsDTO);
                }
            });
        }

        return view;

    }


    public void setTitle() {
        titleText.setText((String) getNewInstanceParams().get("tag_name"));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initListAdapter(TagGroupsDTO tagGroupsDTO) {
        groupListByTagAdapter = new GroupListByTagAdapter();
        groupListByTagAdapter.init_data_list(tagGroupsDTO.data_list);
        groupListByTagAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                MobclickAgent.onEvent(getActivity(), "group_tag_list", UmengEvents.getEventMap("click", "go_group_simple_profile"));
                Group group = (Group) groupListByTagAdapter.getTotal_data_list().get(position);
                GroupSimpleProfileFragment.start(getActivity(), group.getId(), group.getLeader().getHuanxin_id());
            }
        });
        recommendGroupListView.setLoadMoreListViewAdapter(groupListByTagAdapter);

        if (tagGroupsDTO.data_list.size() == Config.loadPageCount) {
            Object[] params = new Object[3];
            params[0] = 1;
            params[1] = tag_id;
            params[2] = Config.loadPageCount;
            groupListByTagAdapter.setIsHasMore(true);
            recommendGroupListView.setApiAutoInvoker("getGroupsByTag", params, TagGroupsDTO.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
