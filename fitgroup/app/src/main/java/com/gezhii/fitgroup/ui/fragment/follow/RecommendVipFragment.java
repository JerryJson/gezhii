package com.gezhii.fitgroup.ui.fragment.follow;

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
import com.gezhii.fitgroup.dto.FollowersDTO;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.RecommendVipUserAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zj on 16/2/17.
 */
public class RecommendVipFragment extends BaseFragment {
    public static final String TAG = RecommendVipFragment.class.getName();

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.recommend_vip_list_view)
    LoadMoreListView recommendVipListView;

    private RecommendVipUserAdapter recommendVipUserAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recommend_vip_fragment,null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "RecommendVIP", UmengEvents.getEventMap("click", "load"));
        recommendVipListView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

        titleText.setText("推荐达人");
        rightText.setText("下一步");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                FollowUserMomentsFragment.start(getActivity());
            }
        });

        API.getRecommendVipUsers(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
//                RecommendVipUsersDto recommendVipUsersDto = RecommendVipUsersDto.parserJson(response);
                FollowersDTO followersDTO = FollowersDTO.parserJson(response);

                recommendVipUserAdapter = new RecommendVipUserAdapter(getActivity());
                recommendVipUserAdapter.init_data_list(followersDTO.data_list);
                recommendVipListView.setLoadMoreListViewAdapter(recommendVipUserAdapter);

                if (followersDTO.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = Config.loadPageCount;

                    recommendVipUserAdapter.setIsHasMore(true);
                    recommendVipListView.setApiAutoInvoker("getRecommendVipUsers", params, FollowersDTO.class);
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
