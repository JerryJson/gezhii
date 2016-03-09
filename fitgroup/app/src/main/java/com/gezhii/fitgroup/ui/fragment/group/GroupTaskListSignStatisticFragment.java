package com.gezhii.fitgroup.ui.fragment.group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.GroupTaskListSignDetailAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.OnListItemLongClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupTaskListSignStatisticFragment extends BaseFragment {


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
    @InjectView(R.id.group_task_list_view)
    LoadMoreListView groupTaskListView;

    @InjectView(R.id.add_group_task_layout)
    LinearLayout addGroupTaskLayout;

    GroupTaskListSignDetailAdapter groupTaskListSignDetailAdapter;
    @InjectView(R.id.long_click_delete_group_task_img)
    ImageView longClickDeleteGroupTaskImg;
    @InjectView(R.id.long_click_delete_group_task_layout)
    LinearLayout longClickDeleteGroupTaskLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_task_list_sign_statistic_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "group_tasks", UmengEvents.getEventMap("click", "load"));
        setView();
        groupTaskListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    public void setView() {
        titleText.setText("公会任务");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (UserModel.getInstance().isGroupLeader()) {
            addGroupTaskLayout.setVisibility(View.VISIBLE);
            addGroupTaskLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_tasks", UmengEvents.getEventMap("click", "add_task"));
                    UserCustomerTaskModel.getInstance().isFromUserCustomerTask = false;
                    AddTaskFragment.start(getActivity());
                }
            });
        } else {
            addGroupTaskLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getGroupTasks(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());

                        final GroupTaskDTO groupTaskDTO = GroupTaskDTO.parserJson(response);
                        final List<GroupTask> groupTaskList = groupTaskDTO.getGroup_tasks();
                        if (groupTaskDTO != null) {
                            groupTaskListSignDetailAdapter = new GroupTaskListSignDetailAdapter();
                            groupTaskListSignDetailAdapter.setGroupTaskList(groupTaskDTO.getGroup_tasks());
                            groupTaskListSignDetailAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                                @Override
                                public void onListItemClick(View v, int position) {
                                    MobclickAgent.onEvent(getActivity(), "group_tasks", UmengEvents.getEventMap("click", "task"));
                                    int task_id = groupTaskDTO.getGroup_tasks().get(position).getId();
                                    GroupTaskDailySigninDetailFragment.start(getActivity(), task_id);
                                }
                            });
                            if (UserModel.getInstance().isGroupLeader()) {
                                if (GroupTaskModel.getInstance().isShowDetailLongClickDeleteCustomerTask()) {
                                    longClickDeleteGroupTaskLayout.setVisibility(View.VISIBLE);
                                    longClickDeleteGroupTaskImg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            GroupTaskModel.getInstance().hideDetailLongClickDeleteCustomerTask();
                                            longClickDeleteGroupTaskLayout.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    longClickDeleteGroupTaskLayout.setVisibility(View.GONE);
                                }

                                groupTaskListSignDetailAdapter.setOnListItemLongClickListener(new OnListItemLongClickListener() {
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
                                                        groupTaskListView.setAdapter(groupTaskListSignDetailAdapter);
                                                        groupTaskListSignDetailAdapter.notifyDataSetChanged();
                                                        UserModel.getInstance().tryLoadRemote(true);
                                                    }
                                                });
                                            }
                                        });
                                        AlertHelper.showAlert(getActivity(), alertParams);
                                        return true;
                                    }
                                });
                            }

                            groupTaskListView.setAdapter(groupTaskListSignDetailAdapter);
                        }
                    }
                }

        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
