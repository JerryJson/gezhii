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
import com.gezhii.fitgroup.dto.FollowersDTO;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.MyFollowerAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zj on 16/2/24.
 */
public class LikeSigninUsersFragment extends BaseFragment {
    public static final String TAG = LikeSigninUsersFragment.class.getName();
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
    @InjectView(R.id.like_signin_users_list)
    LoadMoreListView likeSigninUsersList;
    private MyFollowerAdapter likeSigninUsersAdapter;

    public static void start(Activity activity, int signin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("signin_id", signin_id);
        mainActivity.showNext(LikeSigninUsersFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.like_signin_users_fragment, null);
        ButterKnife.inject(this, rootView);
        setView();
        return rootView;
    }

    private void setView() {
        likeSigninUsersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleText.setText("点赞的人");
        final int signin_id = (int) getNewInstanceParams().get("signin_id");
        API.getLikeSigninUsers(1, signin_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                FollowersDTO followersDTO = FollowersDTO.parserJson(response);
                likeSigninUsersAdapter = new MyFollowerAdapter(getActivity());
                likeSigninUsersAdapter.init_data_list(followersDTO.data_list);
                likeSigninUsersList.setLoadMoreListViewAdapter(likeSigninUsersAdapter);
                if (followersDTO.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = signin_id;
                    params[2] = Config.loadPageCount;
                    likeSigninUsersAdapter.setIsHasMore(true);
                    likeSigninUsersList.setApiAutoInvoker("getLikeSigninUsers", params, FollowersDTO.class);
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
