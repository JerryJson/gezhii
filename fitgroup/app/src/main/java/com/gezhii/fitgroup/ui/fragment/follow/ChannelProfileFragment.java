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

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ChannelSigninsDTO;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.RefreshChannelEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.UserAndChannelProfileListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/17.
 */
public class ChannelProfileFragment extends BaseFragment {
    public static final String TAG = ChannelProfileFragment.class.getName();

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.channel_profile_info_list)
    LoadMoreListView channelProfileInfoList;
    @InjectView(R.id.channel_swipefrefresh)
    SwipeRefreshLayout channelSwipefrefresh;

    private UserAndChannelProfileListAdapter channelProfileAdapter;
    private Tag channel;
    private int channel_id = 0;

    public static void start(Activity activity, Tag tag) {
        start(activity, tag, 0);
    }

    public static void start(Activity activity, Tag tag, int channel_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("tag", tag);
        params.put("channel_id", channel_id);
        mainActivity.showNext(ChannelProfileFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.channel_profile_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "ChannelProfile", UmengEvents.getEventMap("click", "load"));
        EventBus.getDefault().register(this);
        channelProfileInfoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        channelSwipefrefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                API.getChannelSignins(1, UserModel.getInstance().getUserId(), channel_id, Config.loadPageCount, new APICallBack() {

                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        ChannelSigninsDTO channelSigninsDTO = ChannelSigninsDTO.parserJson(response);
//                        if (channel_id == 0) {
//                            channelSigninsDTO.setTag(channel);
//                        }
                        titleText.setText(channelSigninsDTO.getTag().getName());
                        channelProfileAdapter.init_data_list(channelSigninsDTO.getData_list());
                        channelProfileAdapter.getTotal_data_list().add(0, channelSigninsDTO);
                        channelProfileInfoList.setLoadMoreListViewAdapter(channelProfileAdapter);

                        if (channelSigninsDTO.getData_list().size() == Config.loadPageCount) {
                            Object[] params = new Object[4];
                            params[0] = 1;
                            params[1] = UserModel.getInstance().getUserId();
                            params[2] = channelSigninsDTO.getTag().getId();
                            params[3] = Config.loadPageCount;

                            channelProfileAdapter.setIsHasMore(true);
                            channelProfileInfoList.setApiAutoInvoker("getChannelSignins", params, ChannelSigninsDTO.class);
                        }
                        channelSwipefrefresh.setRefreshing(false);
                    }
                });
            }
        });
        if(savedInstanceState != null){
            channel_id = savedInstanceState.getInt("channel_id");
        }else {
            channel_id = (int) getNewInstanceParams().get("channel_id");
        }
        setView();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("channel_id",channel_id);
    }

    private void setView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rightImg.setImageResource(R.mipmap.publish_post_icon);
        rightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPostFragment.start(getActivity(), channel_id);
            }
        });
        channelProfileAdapter = new UserAndChannelProfileListAdapter(getActivity(), UserAndChannelProfileListAdapter.FLAG_CHANNEL);
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getChannelSignins(1, UserModel.getInstance().getUserId(), channel_id, Config.loadPageCount, new APICallBack() {

            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                ChannelSigninsDTO channelSigninsDTO = ChannelSigninsDTO.parserJson(response);
//                if (channel_id == 0) {
//                    channelSigninsDTO.setTag(channel);
//                }
                titleText.setText(channelSigninsDTO.getTag().getName());
                channelProfileAdapter.init_data_list(channelSigninsDTO.getData_list());
                channelProfileAdapter.getTotal_data_list().add(0, channelSigninsDTO);
                channelProfileInfoList.setLoadMoreListViewAdapter(channelProfileAdapter);

                if (channelSigninsDTO.getData_list().size() == Config.loadPageCount) {
                    Object[] params = new Object[4];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = channelSigninsDTO.getTag().getId();
                    params[3] = Config.loadPageCount;

                    channelProfileAdapter.setIsHasMore(true);
                    channelProfileInfoList.setApiAutoInvoker("getChannelSignins", params, ChannelSigninsDTO.class);
                }
            }
        });
    }

    public void onEventMainThread(RefreshChannelEvent refreshChannelEvent) {
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getChannelSignins(1, UserModel.getInstance().getUserId(), channel_id, Config.loadPageCount, new APICallBack() {

            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                ChannelSigninsDTO channelSigninsDTO = ChannelSigninsDTO.parserJson(response);
//                channelSigninsDTO.setTag(channel);
                titleText.setText(channelSigninsDTO.getTag().getName());
                channelProfileAdapter.init_data_list(channelSigninsDTO.getData_list());
                channelProfileAdapter.getTotal_data_list().add(0, channelSigninsDTO);
                channelProfileInfoList.setLoadMoreListViewAdapter(channelProfileAdapter);

                if (channelSigninsDTO.getData_list().size() == Config.loadPageCount) {
                    Object[] params = new Object[4];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = channel_id;
                    params[3] = Config.loadPageCount;

                    channelProfileInfoList.setHasMore(true);
                    channelProfileInfoList.setApiAutoInvoker("getChannelSignins", params, ChannelSigninsDTO.class);
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
