package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/9.
 */
public class ChooseTaskTypeFragment extends BaseFragment {

    public static final String GROUP_TASK = "group_task";

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
    @InjectView(R.id.sign_once_layout)
    LinearLayout signOnceLayout;
    @InjectView(R.id.step_layout)
    LinearLayout stepLayout;
    @InjectView(R.id.weight_layout)
    LinearLayout weightLayout;
    @InjectView(R.id.image_layout)
    LinearLayout imageLayout;

    private GroupTask groupTask;

    public static void start(Activity activity, GroupTask groupTask) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(GROUP_TASK, groupTask);
        mainActivity.showNext(ChooseTaskTypeFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_task_type_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "create_task", UmengEvents.getEventMap("click", "load"));
        rootView.setOnTouchListener(this);
        initTitleView();
        groupTask = new GroupTask();
        groupTask = (GroupTask) getNewInstanceParams().get(GROUP_TASK);
        signOnceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "create_task", UmengEvents.getEventMap("click", "一键打卡"));
                if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                    UserCustomerTask userCustomerTask = new UserCustomerTask();
                    userCustomerTask.setSign_type("sign_once");
                    userCustomerTask.setTask_name(groupTask.getTask_name());
                    UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                    EventBus.getDefault().post(new CustomTaskChangeEvent());
                    MainActivity m = (MainActivity) getActivity();
                    m.popToFragment(m.add_task_fragment_id);
                } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), groupTask.getTask_name(), "{}", new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.add_task_fragment_id);

                            UserModel.getInstance().tryLoadRemote(true);
                        }
                    });
                }

            }
        });
        stepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "create_task", UmengEvents.getEventMap("click", "步数"));
                SetStepLimitFragment.start(getActivity(), groupTask);
            }
        });
        weightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "create_task", UmengEvents.getEventMap("click", "体重"));
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("input", "body_weight");
                String task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                Log.i("task_info", task_info);
                if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                    UserCustomerTask userCustomerTask = new UserCustomerTask();
                    userCustomerTask.setSign_type("body_weight");
                    userCustomerTask.setTask_name(groupTask.getTask_name());
                    UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                    EventBus.getDefault().post(new CustomTaskChangeEvent());
                    MainActivity m = (MainActivity) getActivity();
                    m.popToFragment(m.add_task_fragment_id);
                } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), groupTask.getTask_name(), task_info, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.add_task_fragment_id);

                            UserModel.getInstance().tryLoadRemote(true);
                        }
                    });
                }

            }
        });
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "create_task", UmengEvents.getEventMap("click", "图片"));
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("input", "image");
                String task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                Log.i("task_info", task_info);
                if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                    UserCustomerTask userCustomerTask = new UserCustomerTask();
                    userCustomerTask.setSign_type("image");
                    userCustomerTask.setTask_name(groupTask.getTask_name());
                    UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                    EventBus.getDefault().post(new CustomTaskChangeEvent());
                    MainActivity m = (MainActivity) getActivity();
                    m.popToFragment(m.add_task_fragment_id);
                } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), groupTask.getTask_name(), task_info, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.add_task_fragment_id);
                            UserModel.getInstance().tryLoadRemote(true);
                        }
                    });
                }
            }
        });
        return rootView;
    }

    private void initTitleView() {
        titleText.setText("选择任务类型");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
