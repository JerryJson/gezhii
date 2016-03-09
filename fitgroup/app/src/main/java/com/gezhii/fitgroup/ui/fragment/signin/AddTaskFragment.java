package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.HotTaskDTO;
import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.dto.basic.HotTask;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.dto.db.DBTaskCategory;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.TaskDBModel;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/8.
 */
public class AddTaskFragment extends BaseFragment {

    DBTask dbTask;
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
    @InjectView(R.id.search_layout)
    LinearLayout searchLayout;
    @InjectView(R.id.add_task_list_view)
    LinearLayout addTaskListView;

    List<DBTaskCategory> mDBTaskCategoryList;
    @InjectView(R.id.task_search_input)
    RelativeLayout taskSearchInput;
    @InjectView(R.id.hot_task_list_view)
    LinearLayout hotTaskListView;

    List<HotTask> hotTaskList;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.add_task_fragment_id = mainActivity.showNext(AddTaskFragment.class, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_task_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "load"));
        rootView.setOnTouchListener(this);
        initTitleView();
        setView();
        return rootView;
    }

    private void initTitleView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "back"));
                finish();
            }
        });
        titleText.setText("添加任务");
        rightText.setText("新建");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "create_task"));
                SearchOrAddTaskFragment.start(getActivity());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        API.getHotTasks(UserModel.getInstance().getUserId(), new OnRequestEnd() {
            @Override
            public void onRequestSuccess(String response) {
                Log.i("logger",response);
                HotTaskDTO hotTaskDTO = HotTaskDTO.parseJson(response);
                hotTaskList = hotTaskDTO.getTasks();
                setHotTaskListView();
            }

            @Override
            public void onRequestFail(VolleyError error) {

            }
        });
    }

    private void setHotTaskListView() {
        hotTaskListView.removeAllViews();
        for (final HotTask hotTask : hotTaskList) {
            final View hotTaskItemView = LayoutInflater.from(getActivity()).inflate(R.layout.hot_task_list_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            hotTaskItemView.setLayoutParams(layoutParams);
            final HotTaskViewHolder hotTaskViewHolder = new HotTaskViewHolder(hotTaskItemView);
            hotTaskViewHolder.taskName.setText(hotTask.getTask_name());
            String task_info;
            task_info = hotTask.getTask_info();
            List<GroupTagConfig> groupTagConfigs = hotTask.getTask_tags();
            String tagNames = "";
            if (groupTagConfigs != null && groupTagConfigs.size() > 0) {
                for (int i = 0; i < groupTagConfigs.size(); i++) {
                    GroupTagConfig groupTagConfig = groupTagConfigs.get(i);
                    tagNames = tagNames + "「" + groupTagConfig.getName() + "」";
                }
                hotTaskViewHolder.taskTag.setText("根据" + tagNames + "推荐");
            }
            String input = null;
            if (!"{}".equals(task_info)) {
                try {
                    JSONObject jsonObject = new JSONObject(task_info);
                    input = jsonObject.optString("input");
                    if ("body_weight".equals(input)) {
                        hotTask.setInput("body_weight");
                        hotTaskViewHolder.signWeightFlagLayout.setVisibility(View.VISIBLE);
                    } else if ("image".equals(input)) {
                        hotTask.setInput("image");
                        hotTaskViewHolder.signImgFlagLayout.setVisibility(View.VISIBLE);
                    } else if ("step".equals(input)) {
                        hotTask.setInput("step");
                        hotTask.setStep(jsonObject.optInt("step_limit"));
                        hotTaskViewHolder.signStepFlagLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                hotTaskViewHolder.signOnceFlagLayout.setVisibility(View.VISIBLE);
            }
            hotTaskViewHolder.hotTaskLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                        if (UserCustomerTaskModel.getInstance().isExistCustomerTask(hotTask.getTask_name())) {
                            showToast("该自定义任务已存在");
                        } else {
                            UserCustomerTask userCustomerTask = new UserCustomerTask();
                            if ("step".equals(hotTask.getInput())) {
                                userCustomerTask.setSign_type("step");
                                userCustomerTask.setStep_limit(hotTask.getStep());
                            } else if ("body_weight".equals(hotTask.getInput())) {
                                userCustomerTask.setSign_type("body_weight");
                            } else if ("image".equals(hotTask.getInput())) {
                                userCustomerTask.setSign_type("image");
                            } else {
                                userCustomerTask.setSign_type("sign_once");
                            }
                            userCustomerTask.setTask_name(hotTask.getTask_name());
                            UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                            EventBus.getDefault().post(new CustomTaskChangeEvent());
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.add_task_fragment_id);

                        }

                    } else {
                        String task_info;
                        if ("step".equals(hotTask.getInput())) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "step");
                            hashMap.put("step_limit", hotTask.getStep());
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        } else if ("body_weight".equals(hotTask.getInput())) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "body_weight");
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        }else if ("image".equals(hotTask.getInput())) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "image");
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        } else {
                            task_info = "{}";
                        }
                        EventBus.getDefault().post(new ShowLoadingEvent());
                        API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), hotTask.getTask_name(), task_info, new APICallBack() {
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
            hotTaskListView.addView(hotTaskItemView);
        }
    }

    private void setView() {
        searchLayout.setVisibility(View.VISIBLE);
        taskSearchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "searchbar"));
                SearchOrAddTaskFragment.start(getActivity());
            }
        });
        mDBTaskCategoryList = TaskDBModel.getInstance().getDBTaskCategoryList();
        if (mDBTaskCategoryList != null && mDBTaskCategoryList.size() > 0) {
            for (int i = 0; i < mDBTaskCategoryList.size(); i++) {
                final DBTaskCategory dbTaskCategory = mDBTaskCategoryList.get(i);
                final View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.add_task_list_item, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.setMargins(0, 0, 0,
//                        getResources().getDimensionPixelOffset(R.dimen.spacing_small));
                itemView.setLayoutParams(layoutParams);
                final ViewHolder viewHolder = new ViewHolder(itemView);
                viewHolder.itemText.setText(dbTaskCategory.name);
                viewHolder.taskLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", dbTaskCategory.name));
                        AddTaskParamsFragment.start(getActivity(), dbTaskCategory);
                    }
                });
                if (i == mDBTaskCategoryList.size() - 1) {
                    viewHolder.lineView.setVisibility(View.INVISIBLE);
                }
                addTaskListView.addView(itemView);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'hot_task_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'add_task_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.task_layout)
        RelativeLayout taskLayout;
        @InjectView(R.id.item_text)
        TextView itemText;
        @InjectView(R.id.arrow)
        ImageView arrow;
        @InjectView(R.id.line_view)
        View lineView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    class HotTaskViewHolder {
        @InjectView(R.id.hot_task_layout)
        RelativeLayout hotTaskLayout;
        @InjectView(R.id.task_name)
        TextView taskName;
        @InjectView(R.id.task_tag)
        TextView taskTag;
        @InjectView(R.id.item_layout)
        LinearLayout itemLayout;
        @InjectView(R.id.sign_step_flag_layout)
        LinearLayout signStepFlagLayout;
        @InjectView(R.id.sign_img_flag_layout)
        LinearLayout signImgFlagLayout;
        @InjectView(R.id.sign_once_flag_layout)
        LinearLayout signOnceFlagLayout;
        @InjectView(R.id.sign_weight_flag_layout)
        LinearLayout signWeightFlagLayout;

        HotTaskViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
