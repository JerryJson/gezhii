package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.UserStepModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/10.
 */
public class SignFragment extends BaseFragment {

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
    @InjectView(R.id.group_task_layout)
    LinearLayout groupTaskLayout;
    @InjectView(R.id.custom_task_layout)
    LinearLayout customTaskLayout;
    @InjectView(R.id.add_task_btn)
    RelativeLayout addTaskBtn;
    @InjectView(R.id.long_click_delete_customer_task_img)
    ImageView longClickDeleteCustomerTaskImg;
    @InjectView(R.id.long_click_delete_customer_task_layout)
    LinearLayout longClickDeleteCustomerTaskLayout;


    private static final String TAG_USER_CUSTOMER_TASK_DTO = "tag_user_customer_task_dto";
    List<GroupTask> groupTaskList;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(SignFragment.class, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "load"));
        rootView.setOnTouchListener(this);
        initTitleView();

        return rootView;
    }

    public void initTitleView() {
        backBtn.setVisibility(View.INVISIBLE);
        backText.setText("取消");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("打卡");
        rightText.setText("请假");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignLeaveFragment.start(getActivity());
            }
        });
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "add_task"));
                UserCustomerTaskModel.getInstance().isFromUserCustomerTask = true;
                AddTaskFragment.start(getActivity());
            }
        });
    }


    private void setGroupTaskView() {
        groupTaskLayout.removeAllViews();
        for (final GroupTask groupTask : groupTaskList) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.sign_task_list_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0,
                    getResources().getDimensionPixelOffset(R.dimen.spacing_small));
            itemView.setLayoutParams(layoutParams);
            ViewHolder viewHolder = new ViewHolder(itemView);
            viewHolder.taskName.setText(groupTask.getTask_name());
            final String task_info = groupTask.getTask_info();
            if (!"{}".equals(task_info)) {
                String input = null;
                try {
                    JSONObject jsonObject = new JSONObject(task_info);
                    input = jsonObject.optString("input");
                    if ("body_weight".equals(input)) {
                        viewHolder.signWeightFlagLayout.setVisibility(View.VISIBLE);
                    } else if ("image".equals(input)) {
                        viewHolder.signImgFlagLayout.setVisibility(View.VISIBLE);
                    } else if ("step".equals(input)) {
                        int stepLimit = jsonObject.optInt("step_limit");
                        viewHolder.signStepFlagLayout.setVisibility(View.VISIBLE);
                        viewHolder.stepText.setText("已完成：" + UserStepModel.getInstance().getUserTodayStep() + "步   目标：" + stepLimit + "步");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                viewHolder.signOnceFlagLayout.setVisibility(View.VISIBLE);
            }
            Log.i("darren", task_info);
            if (groupTask.getSignin() == 0) {
                viewHolder.taskFinishImg.setVisibility(View.VISIBLE);
                viewHolder.taskFinishText.setVisibility(View.INVISIBLE);
                viewHolder.taskLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserModel.getInstance().isFromGroupSignin = true;
                        if (!"{}".equals(task_info)) {
                            String input = null;
                            try {
                                JSONObject jsonObject = new JSONObject(task_info);
                                input = jsonObject.optString("input");
                                if ("body_weight".equals(input)) {
                                    MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "do_task_weight"));
                                    AddTaskWeightFragment.start(getActivity(), groupTask.getTask_name(), "");
                                } else if ("image".equals(input)) {
                                    SignAddContentFragment.start(getActivity(), groupTask.getTask_name(), "", "", true);
                                } else if ("step".equals(input)) {
                                    int stepLimit = jsonObject.optInt("step_limit");
                                    if (UserStepModel.getInstance().getUserTodayStep() >= stepLimit) {
                                        SignAddContentFragment.start(getActivity(), groupTask.getTask_name(), "", "", false);
                                    } else {
                                        showToast("未完成目标步数");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "do_task_normal"));
                            SignAddContentFragment.start(getActivity(), groupTask.getTask_name(), "", "", false);
                        }

                    }
                });
            } else {
                viewHolder.taskFinishImg.setVisibility(View.INVISIBLE);
                viewHolder.taskFinishText.setVisibility(View.VISIBLE);
            }
            groupTaskLayout.addView(itemView);
        }

    }

    private void setCustomTaskView() {
        customTaskLayout.removeAllViews();
        final List<UserCustomerTask> userCustomerTaskList = UserCustomerTaskModel.getInstance().getUserCustomerTaskListModel();
        if (userCustomerTaskList != null && userCustomerTaskList.size() != 0) {
            if (UserCustomerTaskModel.getInstance().isShowLongClickDeleteCustomerTask()) {
                longClickDeleteCustomerTaskLayout.setVisibility(View.VISIBLE);
                longClickDeleteCustomerTaskImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserCustomerTaskModel.getInstance().hideLongClickDeleteCustomerTask();
                        longClickDeleteCustomerTaskLayout.setVisibility(View.INVISIBLE);
                    }
                });
            } else {
                longClickDeleteCustomerTaskLayout.setVisibility(View.INVISIBLE);
            }
            for (final UserCustomerTask customerTask : userCustomerTaskList) {
                final View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.sign_task_list_item, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0,
                        getResources().getDimensionPixelOffset(R.dimen.spacing_small));
                itemView.setLayoutParams(layoutParams);
                Log.i("darrens", customerTask.getSign_type());
                final ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.taskName.setText(customerTask.getTask_name());

                if ("body_weight".equals(customerTask.getSign_type())) {
                    viewHolder.signWeightFlagLayout.setVisibility(View.VISIBLE);
                } else if ("image".equals(customerTask.getSign_type())) {
                    viewHolder.signImgFlagLayout.setVisibility(View.VISIBLE);
                } else if ("step".equals(customerTask.getSign_type())) {
                    viewHolder.signStepFlagLayout.setVisibility(View.VISIBLE);
                    viewHolder.stepText.setText("已完成：" + UserStepModel.getInstance().getUserTodayStep() + "步   目标：" + customerTask.getStep_limit() + "步");
                } else {
                    viewHolder.signOnceFlagLayout.setVisibility(View.VISIBLE);
                }
                viewHolder.taskLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View v) {
                        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                        alertParams.setTitle("删除任务");
                        alertParams.setMessage("确定要删除该自定义任务吗?");
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
                                UserCustomerTaskModel.getInstance().deleteUserCustomerTask(customerTask.getTask_id());
                                customTaskLayout.removeView(itemView);
                            }
                        });
                        AlertHelper.showAlert(getActivity(), alertParams);
                        return true;
                    }
                });
                if (!TimeHelper.getInstance().getTodayString().equals(customerTask.getFinish_date())) {
                    viewHolder.taskFinishImg.setVisibility(View.VISIBLE);
                    viewHolder.taskFinishText.setVisibility(View.INVISIBLE);
                    viewHolder.taskLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserModel.getInstance().isFromGroupSignin = true;
                            if ("body_weight".equals(customerTask.getSign_type())) {
                                MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "do_task_weight"));
                                AddTaskWeightFragment.start(getActivity(), customerTask.getTask_name(), customerTask.getTask_id());
                            } else if ("step".equals(customerTask.getSign_type())) {
                                int stepLimit = customerTask.getStep_limit();
                                if (UserStepModel.getInstance().getUserTodayStep() >= stepLimit) {
                                    MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "do_task_step"));
                                    SignAddContentFragment.start(getActivity(), customerTask.getTask_name(), "", customerTask.getTask_id(), customerTask.getSign_type().equals("image"));
                                } else {
                                    showToast("未完成目标步数");
                                }

                            } else {
                                MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "do_task_normal"));
                                SignAddContentFragment.start(getActivity(), customerTask.getTask_name(), "", customerTask.getTask_id(), customerTask.getSign_type().equals("image"));
                            }
                        }
                    });

                } else {
                    viewHolder.taskFinishImg.setVisibility(View.INVISIBLE);
                    viewHolder.taskFinishText.setVisibility(View.VISIBLE);
                }
                customTaskLayout.addView(itemView);
            }
        }

    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getGroupTasksForUser(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                GroupTaskDTO groupTaskDTO = GroupTaskDTO.parserJson(response);
                groupTaskList = groupTaskDTO.getGroup_tasks();
                setGroupTaskView();
            }
        });
        setCustomTaskView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'sign_task_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.step_text)
        TextView stepText;
        @InjectView(R.id.task_name)
        TextView taskName;
        @InjectView(R.id.sign_step_flag_layout)
        LinearLayout signStepFlagLayout;
        @InjectView(R.id.sign_img_flag_layout)
        LinearLayout signImgFlagLayout;
        @InjectView(R.id.sign_once_flag_layout)
        LinearLayout signOnceFlagLayout;
        @InjectView(R.id.sign_weight_flag_layout)
        LinearLayout signWeightFlagLayout;
        @InjectView(R.id.task_finish_img)
        ImageView taskFinishImg;
        @InjectView(R.id.task_finish_text)
        TextView taskFinishText;
        @InjectView(R.id.task_layout)
        RelativeLayout taskLayout;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
