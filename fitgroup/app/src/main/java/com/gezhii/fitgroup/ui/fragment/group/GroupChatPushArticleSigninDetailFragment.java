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

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupSimpleProfileSignHistoryListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by isansmith on 15/12/31.
 */
public class GroupChatPushArticleSigninDetailFragment extends BaseFragment {
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.push_article_signin_detail_list_view)
    LoadMoreListView pushArticleSigninDetailListView;

    List<Integer> sign_ids;
    Activity mContext;

    public static void start(Activity activity, List<Integer> sign_ids) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("activity", activity);
        params.put("sign_ids", sign_ids);
        mainActivity.showNext(GroupChatPushArticleSigninDetailFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_chat_push_article_signin_detail, null);
        ButterKnife.inject(this, rootView);
        mContext = (Activity) getNewInstanceParams().get("activity");
        setView();
        pushArticleSigninDetailListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private void setView() {
        titleText.setText("打卡详情");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sign_ids = (List<Integer>) getNewInstanceParams().get("sign_ids");
        if (null != sign_ids && sign_ids.size() != 0) {
            EventBus.getDefault().post(new ShowLoadingEvent());
            API.getSigninHistoryByIds(sign_ids, new OnRequestEnd() {
                @Override
                public void onRequestSuccess(String response) {
                    EventBus.getDefault().post(new CloseLoadingEvent());
                    Log.i("logger------getSigninHistoryByIds---response", response);
                    SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);
                    Log.i("logger-----signHistoryDto.data_list.size()--size", signHistoryDto.data_list.size());
                    GroupSimpleProfileSignHistoryListAdapter groupSimpleProfileSifnHistoryListAdapter = new GroupSimpleProfileSignHistoryListAdapter(mContext, 11);
                    groupSimpleProfileSifnHistoryListAdapter.init_data_list(signHistoryDto.data_list);
                    pushArticleSigninDetailListView.setLoadMoreListViewAdapter(groupSimpleProfileSifnHistoryListAdapter);
                }

                @Override
                public void onRequestFail(VolleyError error) {

                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
