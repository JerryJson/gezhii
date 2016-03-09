package com.gezhii.fitgroup.ui.fragment.plan;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskWeightFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignAddContentFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignTaskLinkedFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/20.
 */
public class MySelfAddTaskFragment extends BaseFragment {


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
    @InjectView(R.id.today_sigin_layout)
    LinearLayout todaySiginLayout;
    @InjectView(R.id.add_task_btn)
    RelativeLayout addTaskBtn;
    @InjectView(R.id.long_click_delete_customer_task_img)
    ImageView longClickDeleteCustomerTaskImg;
    @InjectView(R.id.long_click_delete_customer_task_layout)
    LinearLayout longClickDeleteCustomerTaskLayout;

    private List<Signin> user_singins;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(MySelfAddTaskFragment.class);
    }

    public static void start(Activity activity,List<Signin> user_signins){
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String,Object> params = new HashMap<>();
        params.put("user_signins",user_signins);
        mainActivity.showNext(MySelfAddTaskFragment.class,params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_self_plan_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        initTitleView();
        return rootView;
    }

    public void initTitleView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("加练");
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_tasks", UmengEvents.getEventMap("click", "add_task"));
                UserCustomerTaskModel.getInstance().isFromUserCustomerTask = true;
                AddTaskFragment.start(getActivity());
            }
        });
    }


    private void setCustomTaskView() {
        user_singins = (List<Signin>) getNewInstanceParams().get("user_signins");
        todaySiginLayout.removeAllViews();
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
                final View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.plan_user_signin_item, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0, 0);
                itemView.setLayoutParams(layoutParams);
                Log.i("darrens", customerTask.getSign_type());
                final ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.userSigninName.setText(customerTask.getTask_name());
                viewHolder.userSigninEdit.setVisibility(View.GONE);
                viewHolder.userSigninLayout.setOnLongClickListener(new View.OnLongClickListener() {
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
                                todaySiginLayout.removeView(itemView);
                            }
                        });
                        AlertHelper.showAlert(getActivity(), alertParams);
                        return true;
                    }
                });
                if(user_singins != null){
                    if (getSiginUseTaskName(user_singins, customerTask.getTask_name()) != null) {
                        viewHolder.userSigninUndoneLayout.setVisibility(View.GONE);
                        viewHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                        viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showToast("该任务已完成");
                            }
                        });
                    }else{
                        viewHolder.userSigninUndoneLayout.setVisibility(View.VISIBLE);
                        viewHolder.userSigninDoneLayout.setVisibility(View.GONE);
                        viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                SignTaskLinkedFragment.start(getActivity(), customerTask.getTask_name(), false, false);
                            }
                        });
                    }
                }else{
                    if (!TimeHelper.getInstance().getTodayString().equals(customerTask.getFinish_date())) {
                        viewHolder.userSigninUndoneLayout.setVisibility(View.VISIBLE);
                        viewHolder.userSigninDoneLayout.setVisibility(View.GONE);
                        viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SignTaskLinkedFragment.start(getActivity(), customerTask.getTask_name(), false, false);
                            }
                        });

                    } else {
                        viewHolder.userSigninUndoneLayout.setVisibility(View.GONE);
                        viewHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                        viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showToast("该任务已完成");
                            }
                        });
                    }
                }
                todaySiginLayout.addView(itemView);
            }
        }
    }

    //根据打卡名字从打卡历史中获取Signin
    private Signin getSiginUseTaskName(List<Signin> list, String taskName) {
        Signin signin = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTask_name().equals(taskName))
                signin = list.get(i);
        }
        return signin;
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        setCustomTaskView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'plan_user_signin_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.user_signin_name)
        TextView userSigninName;
        @InjectView(R.id.user_signin_undone_layout)
        LinearLayout userSigninUndoneLayout;
        @InjectView(R.id.user_signin_done_layout)
        LinearLayout userSigninDoneLayout;
        @InjectView(R.id.user_signin_desc)
        TextView userSigninDesc;
        @InjectView(R.id.user_signin_img)
        ImageView userSigninImg;
        @InjectView(R.id.user_signin_edit)
        ImageView userSigninEdit;
        @InjectView(R.id.user_signin_right_bottm)
        FrameLayout userSigninRightBottm;
        @InjectView(R.id.user_signin_layout)
        LinearLayout userSigninLayout;
        @InjectView(R.id.user_rest)
        TextView userRest;
        @InjectView(R.id.user_rest_layout)
        LinearLayout userRestLayout;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
