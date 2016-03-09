package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupMemberListDTO;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.event.ATailEvent;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupMemberListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/23.
 */
public class GroupChatATailMemberListFragment extends BaseFragment {
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
    @InjectView(R.id.group_member_list_view)
    LoadMoreListView groupMemberListView;
    GroupMemberListAdapter groupMemberListAdapter;

    public EditText mEditText;

    public static void start(Activity activity, EditText groupchatInput) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("editText", groupchatInput);
        mainActivity.showNext(GroupChatATailMemberListFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_chat_a_tail_member_list_fragment, null);
        ButterKnife.inject(this, rootView);
        mEditText = (EditText) getNewInstanceParams().get("editText");
        setTitle();
        groupMemberListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    public void setTitle() {
        titleText.setText("选择提醒的人");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getGroupMembers(1, UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                final GroupMemberListDTO groupMemberListDTO = GroupMemberListDTO.parserJson(response);
                groupMemberListAdapter = new GroupMemberListAdapter();
                groupMemberListAdapter.init_data_list(groupMemberListDTO.data_list);
                groupMemberListView.setLoadMoreListViewAdapter(groupMemberListAdapter);

                groupMemberListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                    @Override
                    public void onListItemClick(View v, int position) {
                        MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "at"));
                        final GroupMember groupMember = (GroupMember) groupMemberListAdapter.getTotal_data_list().get(position);
                        String nick_name = groupMember.getNick_name();
                        mEditText.getSelectionStart();
                        int index = mEditText.getSelectionStart();
                        Editable editable = mEditText.getText();
                        editable.insert(index, nick_name + " ");
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put(nick_name, groupMember.getUser().getHuanxin_id());
                        EventBus.getDefault().post(new ATailEvent(params));
                        finish();
                    }
                });
                if (groupMemberListDTO.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[4];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = UserModel.getInstance().getMyGroup().getId();
                    params[3] = Config.loadPageCount;
                    groupMemberListAdapter.setIsHasMore(true);
                    groupMemberListView.setApiAutoInvoker("getGroupMembers", params, GroupMemberListDTO.class);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
