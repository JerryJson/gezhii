package com.gezhii.fitgroup.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.StarLeaderListDTO;
import com.gezhii.fitgroup.dto.basic.StarLeaderBanner;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.StarLeaderListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/26.
 */
public class DiscoveryFragment extends BaseFragment {
    public static final String TAG = DiscoveryFragment.class.getName();
    @InjectView(R.id.baike_layout)
    LinearLayout baikeLayout;
    @InjectView(R.id.group_sort_layout)
    LinearLayout groupSortLayout;
    @InjectView(R.id.star_leader_list_view)
    LoadMoreListView starLeaderListView;
    StarLeaderListAdapter starLeaderListAdapter;


    public static int getItemHeight(View rootView, int width) {
        int HEIGHT_UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int WIDTH_UNBOUNDED = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        rootView.measure(WIDTH_UNBOUNDED, HEIGHT_UNBOUNDED);
        return rootView.getMeasuredHeight();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_fragment, null);
        ButterKnife.inject(this, view);
        MobclickAgent.onEvent(getActivity(), "discover", UmengEvents.getEventMap("click", "load"));
        starLeaderListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        baikeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "discover", UmengEvents.getEventMap("click", "减肥百科"));
                WebFragment.client(getActivity(), "http://baike.qing.am");
            }
        });

        groupSortLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "discover", UmengEvents.getEventMap("click", "square"));
                //((MainActivity) getActivity()).showNext(GroupSquareFragment.class, null);
                GroupSquareFragment.start(getActivity());
            }
        });

        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getStarLeaderBanners(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                StarLeaderListDTO tempStarLeaderList = StarLeaderListDTO.parserJson(response);

                starLeaderListAdapter = new StarLeaderListAdapter();

                starLeaderListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                    @Override
                    public void onListItemClick(View v, int position) {
                        MobclickAgent.onEvent(getActivity(), "discover", UmengEvents.getEventMap("click", "articleList"));
                        StarLeaderBanner banner = (StarLeaderBanner) starLeaderListAdapter.getTotal_data_list().get(position);
                        WebFragment.client(getActivity(), banner.img_link, 1);
                    }
                });

                starLeaderListAdapter.init_data_list(tempStarLeaderList.data_list);
                starLeaderListView.setLoadMoreListViewAdapter(starLeaderListAdapter);

                if (tempStarLeaderList.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = Config.loadPageCount;
                    starLeaderListAdapter.setIsHasMore(true);
                    starLeaderListView.setApiAutoInvoker("getStarLeaderBanners", params, StarLeaderListDTO.class);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
