package com.gezhii.fitgroup.ui.fragment.group;

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
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.SignHistoryListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardDetailFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/6.
 */
public class SignHistoryFragment extends BaseFragment {
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.sign_history_list_view)
    LoadMoreListView signHistoryListView;
    public static final String TAG_USER = "tag_user";
    SignHistoryListAdapter mSignHistoryListAdapter;
    private User user;
    SignCardDetailFragment mSignCardDetailFragment;
    public static void start(Activity activity,User user) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_USER, user);
        mainActivity.showNext(SignHistoryFragment.class,params);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_history_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "signin_history", UmengEvents.getEventMap("click", "load"));
        user = (User) getNewInstanceParams().get(TAG_USER);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signHistoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getSigninHistory(1,user.getId(), UserModel.getInstance().getUserId(),  Config.loadPageCount,
                new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                        mSignHistoryListAdapter = new SignHistoryListAdapter(getActivity(),user,0,mSignCardDetailFragment);
                        mSignHistoryListAdapter.init_data_list(signHistoryDto.data_list);
                        signHistoryListView.setLoadMoreListViewAdapter(mSignHistoryListAdapter);

                        if (signHistoryDto.data_list.size() == Config.loadPageCount) {
                            Object[] params = new Object[4];
                            params[0] = 1;
                            params[1] = user.getId();
                            params[2] = UserModel.getInstance().getUserId();
                            params[3] = Config.loadPageCount;
                            mSignHistoryListAdapter.setIsHasMore(true);
                            signHistoryListView.setApiAutoInvoker("getSigninHistory", params, SignHistoryDto.class);
                        }
                    }
                });
        setView();
    }

    private void setView() {
        if (mSignHistoryListAdapter == null) {
            mSignHistoryListAdapter = new SignHistoryListAdapter(getActivity(),user,0,mSignCardDetailFragment);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
