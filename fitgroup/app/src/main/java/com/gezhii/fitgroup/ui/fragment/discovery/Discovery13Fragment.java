package com.gezhii.fitgroup.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ChannelsDTO;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.dto.StarLeaderListDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.DiscoveryAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/15.
 */
public class Discovery13Fragment extends BaseFragment {
    public static final String TAG = Discovery13Fragment.class.getName();
    @InjectView(R.id.discovery_listview)
    LoadMoreListView discoveryListview;
    DiscoveryAdapter discoveryAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discovery13_fragment, null);
        EventBus.getDefault().post(new ShowLoadingEvent());
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "DiscoverPage", UmengEvents.getEventMap("click", "load"));
        discoveryListview.setLayoutManager(new LinearLayoutManager(getActivity()));
        discoveryAdapter = new DiscoveryAdapter(getActivity());
        getDataAndSetListView();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                getDataAndSetListView();
                discoveryAdapter.notifyDataSetChanged();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
    }

    public void getDataAndSetListView() {
        API.getStarLeaderBanners(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                StarLeaderListDTO tempStarLeaderList = StarLeaderListDTO.parserJson(response);
                final List headList = new ArrayList();
                headList.add(0, tempStarLeaderList);
                API.getRecommendChannels(UserModel.getInstance().getUserId(), new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        ChannelsDTO channelsDTO = ChannelsDTO.parserJson(response);
                        Log.i("darren", response);
                        headList.add(1, channelsDTO);
                        API.getRecommendSignins(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                EventBus.getDefault().post(new CloseLoadingEvent());
                                SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                                List allList = new ArrayList();
                                allList.addAll(signHistoryDto.data_list);
                                discoveryAdapter.init_data_list(allList);
                                discoveryListview.setLoadMoreListViewAdapter(discoveryAdapter);
                                if (signHistoryDto.data_list.size() == Config.loadPageCount) {
                                    Object[] params = new Object[3];
                                    params[0] = 1;
                                    params[1] = UserModel.getInstance().getUserId();
                                    params[2] = Config.loadPageCount;
                                    discoveryAdapter.setIsHasMore(true);
                                    discoveryListview.setApiAutoInvoker("getRecommendSignins", params, SignHistoryDto.class);
                                }
                                discoveryAdapter.getTotal_data_list().add(0, headList);
                            }
                        });
                    }
                });

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
