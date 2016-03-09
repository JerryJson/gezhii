package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.UserAndChannelProfileListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/19.
 */
public class FollowUserMomentsFragment extends BaseFragment {
    public static final String TAG = FollowUserMomentsFragment.class.getName();

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
    @InjectView(R.id.follow_user_moments_list)
    LoadMoreListView followUserMomentsList;
    @InjectView(R.id.follow_user_moments_refreshlayout)
    SwipeRefreshLayout followUserMomentsRefreshlayout;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(FollowUserMomentsFragment.class, null);
    }

    private UserAndChannelProfileListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.follow_user_moments_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "FollowingUsersSignin", UmengEvents.getEventMap("click", "load"));
        followUserMomentsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        followUserMomentsRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                API.getFollowingUsersSigninHistory(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new OnRequestEnd() {
                    @Override
                    public void onRequestSuccess(String response) {
                        followUserMomentsRefreshlayout.setRefreshing(false);
                        SignHistoryDto dto = SignHistoryDto.parserJson(response);

                        mAdapter = new UserAndChannelProfileListAdapter(getActivity(), UserAndChannelProfileListAdapter.FLAG_NO_HEAD);
                        mAdapter.init_data_list(dto.data_list);
                        followUserMomentsList.setLoadMoreListViewAdapter(mAdapter);

                        if (dto.data_list.size() == Config.loadPageCount) {
                            Object[] params = new Object[3];
                            params[0] = 1;
                            params[1] = UserModel.getInstance().getUserId();
                            params[2] = Config.loadPageCount;
                            mAdapter.setIsHasMore(true);
                            followUserMomentsList.setApiAutoInvoker("getFollowingUsersSigninHistory", params, SignHistoryDto.class);
                        }
                    }

                    @Override
                    public void onRequestFail(VolleyError error) {
                        followUserMomentsRefreshlayout.setRefreshing(false);
                    }
                });
            }
        });
        setView();
        return rootView;
    }

    private void setView() {
        titleText.setText("我关注的人");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getFollowingUsersSigninHistory(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                SignHistoryDto dto = SignHistoryDto.parserJson(response);

                mAdapter = new UserAndChannelProfileListAdapter(getActivity(), UserAndChannelProfileListAdapter.FLAG_NO_HEAD);
                mAdapter.init_data_list(dto.data_list);
                followUserMomentsList.setLoadMoreListViewAdapter(mAdapter);

                if (dto.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = Config.loadPageCount;
                    mAdapter.setIsHasMore(true);
                    followUserMomentsList.setApiAutoInvoker("getFollowingUsersSigninHistory", params, SignHistoryDto.class);
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
