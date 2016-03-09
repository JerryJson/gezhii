package com.gezhii.fitgroup.ui.fragment.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SearchGroupsDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.SearchGroupListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/19.
 */
public class SearchGroupFragment extends BaseFragment {

    public static final String TAG_KEY_WORD = "tag_key_word";

    public static final String TAG = SearchGroupFragment.class.getName();
    @InjectView(R.id.search_group_input)
    EditText searchGroupInput;
    @InjectView(R.id.cancel_search_text)
    TextView cancelSearchText;
    @InjectView(R.id.search_group_result_list_view)
    LoadMoreListView searchGroupResultListView;

    LoadMoreListViewAdapter searchGroupLoadMoreListViewAdapter;
    @InjectView(R.id.no_result_layout)
    RelativeLayout noResultLayout;


    public static void start(Activity activity) {
        start(activity, "");
    }

    public static void start(Activity activity, String keyWord) {
        MainActivity mainActivity = (MainActivity) activity;
        if (TextUtils.isEmpty(keyWord)) {
            mainActivity.showNext(GroupSquareFragment.class);
        } else {
            HashMap<String, Object> params = new HashMap<>();
            params.put(TAG_KEY_WORD, keyWord);
            Log.i("keyWord------>", keyWord);
            mainActivity.showNext(SearchGroupFragment.class, params);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_group_fragment, null);
        ButterKnife.inject(this, rootView);

        searchGroupResultListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cancelSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchGroupInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    search(textView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return rootView;

    }

    void search(String key) {
        EventBus.getDefault().post(new ShowLoadingEvent());

        API.searchGroups(key, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                Log.i("response------>", response);
                EventBus.getDefault().post(new CloseLoadingEvent());

                final SearchGroupsDTO searchGroupsDTO = SearchGroupsDTO.parserJson(response);
                if (searchGroupsDTO == null || searchGroupsDTO.data_list.size() == 0) {
                    noResultLayout.setVisibility(View.VISIBLE);
                    searchGroupResultListView.setVisibility(View.INVISIBLE);
                } else {
                    searchGroupResultListView.setVisibility(View.VISIBLE);
                    noResultLayout.setVisibility(View.GONE);
                    if (searchGroupLoadMoreListViewAdapter == null) {
                        searchGroupLoadMoreListViewAdapter = new SearchGroupListAdapter();
                    }

                    searchGroupLoadMoreListViewAdapter.init_data_list(searchGroupsDTO.data_list);
                    searchGroupResultListView.setLoadMoreListViewAdapter(searchGroupLoadMoreListViewAdapter);
                    searchGroupLoadMoreListViewAdapter.notifyDataSetChanged();

                    searchGroupLoadMoreListViewAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                        @Override
                        public void onListItemClick(View v, int position) {
                            MobclickAgent.onEvent(getActivity(), "square", UmengEvents.getEventMap("click", "search_result_list"));
                            MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "search_result_list"));
                            Group group = (Group) searchGroupsDTO.data_list.get(position);
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("group_id", group.getId());
                            params.put("leader_huanxin_id", group.getLeader().getHuanxin_id());
                            ((MainActivity) getActivity()).showNext(GroupSimpleProfileFragment.class, params);
                        }
                    });
                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        String keyWord = (String) getNewInstanceParams().get(TAG_KEY_WORD);
        if (!TextUtils.isEmpty(keyWord)) {
            Log.i("key-------->", keyWord.substring(keyWord.length() - 6, keyWord.length()));
            search(keyWord.substring(keyWord.length() - 6, keyWord.length()));
        }

    }


}
