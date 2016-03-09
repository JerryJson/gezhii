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
import com.gezhii.fitgroup.model.UserMessageModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.AboutMeMessageAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zj on 16/2/17.
 */
public class AboutMeMessageFragment extends BaseFragment {
    public static final String TAG = AboutMeMessageFragment.class.getName();

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.message_list_view)
    LoadMoreListView messageListView;
    @InjectView(R.id.no_message_text)
    TextView noMessageText;

    private List<Object> mEMMesageList;
    private AboutMeMessageAdapter mAdapter;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(AboutMeMessageFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_me_message_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "Notification", UmengEvents.getEventMap("click", "load"));
        titleText.setText("消息");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setView();

        return rootView;
    }

    private void setView() {
        mEMMesageList = new ArrayList<>();
        if (UserMessageModel.getInstance().getEmMessageList() != null) {
            mEMMesageList.addAll(UserMessageModel.getInstance().getEmMessageList());
            messageListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAdapter = new AboutMeMessageAdapter(getActivity());
            mAdapter.init_data_list(mEMMesageList);
            messageListView.setLoadMoreListViewAdapter(mAdapter);
        } else {
            noMessageText.setVisibility(View.VISIBLE);
            noMessageText.setText("还没有消息");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
