package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupTaskDTO;
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.GroupTaskModel;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupTaskListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemLongClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/8.
 */
public class GroupTaskFragment extends BaseFragment {
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
    @InjectView(R.id.add_group_task_btn)
    RelativeLayout addGroupTaskBtn;
    @InjectView(R.id.group_task_list_view)
    LoadMoreListView groupTaskListView;
    GroupTaskListAdapter groupTaskListAdapter;
    @InjectView(R.id.no_group_task_layout)
    LinearLayout noGroupTaskLayout;
    @InjectView(R.id.long_click_delete_group_task_img)
    ImageView longClickDeleteGroupTaskImg;
    @InjectView(R.id.long_click_delete_group_task_layout)
    LinearLayout longClickDeleteGroupTaskLayout;

    @OnClick(R.id.add_group_task_btn)
    public void onAddGroupTaskBtn(View v) {
        UserCustomerTaskModel.getInstance().isFromUserCustomerTask = false;
        AddTaskFragment.start(getActivity());
    }

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(GroupTaskFragment.class, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_task_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        initTitleView();
        groupTaskListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    private void initTitleView() {
        backBtn.setVisibility(View.INVISIBLE);
        titleText.setText("公会任务");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if (UserModel.getInstance().getGroupId() != -1) {
            EventBus.getDefault().post(new ShowLoadingEvent());
            API.getGroupTasks(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    EventBus.getDefault().post(new CloseLoadingEvent());
                    GroupTaskDTO groupTaskDTO = GroupTaskDTO.parserJson(response);
                    final List<GroupTask> groupTaskList = groupTaskDTO.getGroup_tasks();
                    if (groupTaskList != null && groupTaskList.size() != 0) {
                        groupTaskListView.setVisibility(View.VISIBLE);
                        noGroupTaskLayout.setVisibility(View.INVISIBLE);
                        rightText.setVisibility(View.VISIBLE);
                        rightText.setText(getString(R.string.done));
                        if (GroupTaskModel.getInstance().isShowCreateLongClickDeleteCustomerTask()) {
                            longClickDeleteGroupTaskLayout.setVisibility(View.VISIBLE);
                            longClickDeleteGroupTaskImg.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GroupTaskModel.getInstance().hideCreateLongClickDeleteCustomerTask();
                                    longClickDeleteGroupTaskLayout.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            longClickDeleteGroupTaskLayout.setVisibility(View.GONE);
                        }
                    } else {
                        groupTaskListView.setVisibility(View.INVISIBLE);
                        noGroupTaskLayout.setVisibility(View.VISIBLE);
                    }
                    if (groupTaskListAdapter == null) {
                        groupTaskListAdapter = new GroupTaskListAdapter();
                    }
                    groupTaskListAdapter.setOnListItemLongClickListener(new OnListItemLongClickListener() {
                        @Override
                        public boolean onListItemLongClick(View v, final int position) {
                            final GroupTask groupTask = groupTaskList.get(position);
                            AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                            alertParams.setTitle("删除任务");
                            alertParams.setMessage("确定要删除该公会任务吗?");
                            alertParams.setCancelString("取消");
                            alertParams.setConfirmString("确定");
                            alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    EventBus.getDefault().post(new ShowLoadingEvent());
                                    API.deleteGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), groupTask.getId(), new APICallBack() {
                                        @Override
                                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                                            EventBus.getDefault().post(new CloseLoadingEvent());
                                            groupTaskList.remove(position);
                                            showToast("该公会任务已被删除");
                                            if (groupTaskList == null || groupTaskList.size() == 0) {
                                                groupTaskListView.setVisibility(View.INVISIBLE);
                                                noGroupTaskLayout.setVisibility(View.VISIBLE);
                                                rightText.setVisibility(View.INVISIBLE);
                                            } else {
                                                rightText.setVisibility(View.VISIBLE);
                                                rightText.setText(getString(R.string.done));
                                            }
                                            groupTaskListView.setAdapter(groupTaskListAdapter);
                                            groupTaskListAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            });
                            AlertHelper.showAlert(getActivity(), alertParams);
                            return true;
                        }
                    });
                    groupTaskListAdapter.setGroupTaskList(groupTaskList);
                    groupTaskListView.setAdapter(groupTaskListAdapter);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
