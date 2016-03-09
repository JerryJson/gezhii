package com.gezhii.fitgroup.ui.fragment.discovery;

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
import com.gezhii.fitgroup.dto.ChannelsDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.AllChannelsAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/17.
 */
public class AllChannelsFragment extends BaseFragment {
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
    @InjectView(R.id.all_channels_listview)
    LoadMoreListView allChannelsListview;
    AllChannelsAdapter allChannelsAdapter;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(AllChannelsFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_channels_fragment, null);
        view.setOnTouchListener(this);
        ButterKnife.inject(this, view);
        MobclickAgent.onEvent(getActivity(), "RecommenChannel", UmengEvents.getEventMap("click", "load"));
        initTitle();
        allChannelsListview.setLayoutManager(new LinearLayoutManager(getActivity()));
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getAllChannels(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                ChannelsDTO channelsDTO = ChannelsDTO.parserJson(response);
                allChannelsAdapter = new AllChannelsAdapter(getActivity());
                allChannelsAdapter.init_data_list(channelsDTO.data_list);
                allChannelsListview.setLoadMoreListViewAdapter(allChannelsAdapter);
                if (channelsDTO.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = Config.loadPageCount;
                    allChannelsAdapter.setIsHasMore(true);
                    allChannelsListview.setApiAutoInvoker("getAllChannels", params, ChannelsDTO.class);
                }
            }
        });

        return view;
    }

    public void initTitle() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("精选频道");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
