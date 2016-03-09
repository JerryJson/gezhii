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
 * Created by zj on 16/2/24.
 */
public class MyMenteesFragment extends BaseFragment {
    public static final String TAG = MyMenteesFragment.class.getName();

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
    @InjectView(R.id.my_mentees_list)
    LoadMoreListView myMenteesList;
    @InjectView(R.id.no_mentees_text)
    TextView noMenteesText;

    private MyFollowerAdapter myMenteesAdapter;
    private int user_id;

    public static void start(Activity activity) {
        start(activity, -1);
    }

    public static void start(Activity activity, int user_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        mainActivity.showNext(MyMenteesFragment.class,params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_mentees_fragment, null);
        ButterKnife.inject(this, rootView);

        myMenteesList.setLayoutManager(new LinearLayoutManager(getActivity()));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if ((int)getNewInstanceParams().get("user_id") == -1) {
            user_id = UserModel.getInstance().getUserId();
            titleText.setText("我的跟随者");
            MobclickAgent.onEvent(getActivity(), "MenteeList", UmengEvents.getEventMap("click", "load", "from", "my"));
        } else {
            titleText.setText("TA的跟随者");
            MobclickAgent.onEvent(getActivity(), "MenteeList", UmengEvents.getEventMap("click", "load", "from", "others"));
            user_id = (int) getNewInstanceParams().get("user_id");
        }

        API.getMentees(1, user_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                FollowersDTO followersDTO = FollowersDTO.parserJson(response);
                if (followersDTO.data_list.size() == 0) {
                    noMenteesText.setVisibility(View.VISIBLE);
                    noMenteesText.setText("还没有跟随者");
                } else {
                    myMenteesAdapter = new MyFollowerAdapter(getActivity());
                    myMenteesAdapter.init_data_list(followersDTO.data_list);
                    myMenteesList.setLoadMoreListViewAdapter(myMenteesAdapter);
                    if (followersDTO.data_list.size() == Config.loadPageCount) {
                        Object[] params = new Object[3];
                        params[0] = 1;
                        params[1] = UserModel.getInstance().getUserId();
                        params[2] = Config.loadPageCount;
                        myMenteesAdapter.setIsHasMore(true);
                        myMenteesList.setApiAutoInvoker("getMentees", params, FollowersDTO.class);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
