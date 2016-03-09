package com.gezhii.fitgroup.ui.fragment.me;

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
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.MyFollowerAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/15.
 */
public class MyFollowFragment extends BaseFragment {

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
    @InjectView(R.id.my_follower_listview)
    LoadMoreListView myFollowerListview;

    MyFollowerAdapter myFollowerAdapter;
    @InjectView(R.id.no_follower_text)
    TextView noFollowerText;
    private int user_id;

    public static void start(Activity activity) {
        start(activity,-1);
    }

    public static void start(Activity activity, int user_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        mainActivity.showNext(MyFollowFragment.class,params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_follower_fragment, null);
        ButterKnife.inject(this, rootView);
        initTitle();

        if ((int)getNewInstanceParams().get("user_id") == -1) {
            user_id = UserModel.getInstance().getUserId();
            titleText.setText("我的关注");
            MobclickAgent.onEvent(getActivity(), "FollowingList", UmengEvents.getEventMap("click", "load", "from", "my"));
        } else {
            titleText.setText("TA的关注");
            MobclickAgent.onEvent(getActivity(), "FollowingList", UmengEvents.getEventMap("click", "load", "from", "others"));
            user_id = (int) getNewInstanceParams().get("user_id");
        }

        myFollowerListview.setLayoutManager(new LinearLayoutManager(getActivity()));
        API.getFollowingUsers(1, user_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                FollowersDTO followersDTO = FollowersDTO.parserJson(response);
                if (followersDTO.data_list.size() == 0) {
                    noFollowerText.setVisibility(View.VISIBLE);
                    noFollowerText.setText("还没有关注任何人");
                } else {
                    myFollowerAdapter = new MyFollowerAdapter(getActivity());
                    myFollowerAdapter.init_data_list(followersDTO.data_list);
                    myFollowerListview.setLoadMoreListViewAdapter(myFollowerAdapter);
                    if (followersDTO.data_list.size() == Config.loadPageCount) {
                        Object[] params = new Object[3];
                        params[0] = 1;
                        params[1] = user_id;
                        params[2] = Config.loadPageCount;
                        myFollowerAdapter.setIsHasMore(true);
                        myFollowerListview.setApiAutoInvoker("getFollowedUsers", params, FollowersDTO.class);
                    }
                }

            }
        });

        return rootView;
    }

    public void initTitle() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
