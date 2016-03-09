package com.gezhii.fitgroup.ui.fragment.discovery;

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
import com.gezhii.fitgroup.dto.RecommendGroupsDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.ui.activity.RegisterAndLoginActivity;
import com.gezhii.fitgroup.ui.adapter.GroupListByTagAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.RecommendGroupListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.GroupSimpleProfileFragment;
import com.gezhii.fitgroup.ui.fragment.group.CreateGroupFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/23.
 */
public class RecommendGroupListFragment extends BaseFragment {
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

    RecommendGroupListAdapter recommendGroupListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_group_list_fragment, null);
        ButterKnife.inject(this, view);
        setTitle();

        recommendGroupListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;

    }


    public void setTitle() {
        titleText.setText("推荐公会");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rightText.setText("创建公会");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserModel.getInstance().isLogin()) {
                    if (UserModel.getInstance().getUserDto().getUser().getLevel() > 2) {
                        if (UserModel.getInstance().getGroupId() != -1) {
                            showToast("你已经是" + UserModel.getInstance().getMyGroup().getGroup_name() + "的成员了！");
                        } else {
                            CreateGroupFragment.start(getActivity());
                        }
                    } else {
                        showToast("创建工会需要等级到达3级,您的等级还不够哦！");
                    }
                } else {
                    RegisterAndLoginActivity.start(getActivity());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().post(new ShowLoadingEvent());

        int user_id;
        if (UserModel.getInstance().isLogin()) {
            user_id = UserModel.getInstance().getUserId();
        } else {
            user_id = -1;
        }

        API.getRecommendGroups(1, user_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                RecommendGroupsDTO recommendGroupsDTO = RecommendGroupsDTO.parserJson(response);
                initListAdapter(recommendGroupsDTO);
            }
        });
    }

    void initListAdapter(RecommendGroupsDTO recommendGroupsDTO) {
        recommendGroupListAdapter = new RecommendGroupListAdapter();
        recommendGroupListAdapter.init_data_list(recommendGroupsDTO.data_list);
        recommendGroupListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                Group group = (Group) recommendGroupListAdapter.getTotal_data_list().get(position);
                GroupSimpleProfileFragment.start(getActivity(), group.getId(), group.getLeader().getHuanxin_id());
            }
        });
        recommendGroupListView.setLoadMoreListViewAdapter(recommendGroupListAdapter);

        if (recommendGroupsDTO.data_list.size() == Config.loadPageCount) {
            Object[] params = new Object[3];
            params[0] = 1;
            params[1] = UserModel.getInstance().getUserId();
            params[2] = Config.loadPageCount;
            recommendGroupListAdapter.setIsHasMore(true);
            recommendGroupListView.setApiAutoInvoker("getRecommendGroups", params, RecommendGroupsDTO.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
