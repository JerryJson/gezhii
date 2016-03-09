package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.dto.db.DBTaskCategory;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.TaskDBModel;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupTaskParamsListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/9.
 */
public class AddTaskParamsFragment extends BaseFragment {

    public final static String DBTASKCATEGORY = "DBTaskCategory";
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
    @InjectView(R.id.add_task_params_list_view)
    RecyclerView addTaskParamsListView;

    GroupTaskParamsListAdapter groupTaskParamsListAdapter;
    DBTaskCategory dbTaskCategory;
    List<DBTask> dbTaskList;
    @InjectView(R.id.task_search_input)
    RelativeLayout taskSearchInput;
    @InjectView(R.id.search_layout)
    LinearLayout searchLayout;

    public static void start(Activity activity, DBTaskCategory dbTaskCategory) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(DBTASKCATEGORY, dbTaskCategory);
        mainActivity.showNext(AddTaskParamsFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_task_params_fragment, null);
        ButterKnife.inject(this, rootView);
        dbTaskCategory = (DBTaskCategory) getNewInstanceParams().get(DBTASKCATEGORY);
        initTitleView();
        addTaskParamsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setView();
        return rootView;
    }

    private void initTitleView() {
        titleText.setText(dbTaskCategory.name);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText.setText("新建");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "create_task"));
                SearchOrAddTaskFragment.start(getActivity());
            }
        });
    }

    private void setView() {
        taskSearchInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "searchbar"));
                SearchOrAddTaskFragment.start(getActivity());
            }
        });
        dbTaskList = TaskDBModel.getInstance().getDBTaskByCategoryId(dbTaskCategory.id);
        if (groupTaskParamsListAdapter == null) {
            groupTaskParamsListAdapter = new GroupTaskParamsListAdapter();
            groupTaskParamsListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                @Override
                public void onListItemClick(View v, int position) {
                    if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                        if (UserCustomerTaskModel.getInstance().isExistCustomerTask(dbTaskList.get(position).name)) {
                            showToast("该自定义任务已存在");
                        } else {
                            UserCustomerTask userCustomerTask = new UserCustomerTask();
                            if ("step".equals(dbTaskList.get(position).parameters)) {
                                userCustomerTask.setSign_type("step");
                                userCustomerTask.setStep_limit(dbTaskList.get(position).step);
                            } else if ("body_weight".equals(dbTaskList.get(position).parameters)) {
                                userCustomerTask.setSign_type("body_weight");
                            } else if ("image".equals(dbTaskList.get(position).parameters)) {
                                userCustomerTask.setSign_type("image");
                            } else {
                                userCustomerTask.setSign_type("sign_once");
                            }
                            userCustomerTask.setTask_name(dbTaskList.get(position).name);
                            UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                            EventBus.getDefault().post(new CustomTaskChangeEvent());
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.add_task_fragment_id);

                        }
                    } else {
                        String task_info;
                        if ("step".equals(dbTaskList.get(position).parameters)) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "step");
                            hashMap.put("step_limit", dbTaskList.get(position).step);
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        } else if ("body_weight".equals(dbTaskList.get(position).parameters)) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "body_weight");
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        } else if ("image".equals(dbTaskList.get(position).parameters)) {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("input", "image");
                            task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                        } else {
                            task_info = "{}";
                        }
                        EventBus.getDefault().post(new ShowLoadingEvent());
                        API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), dbTaskList.get(position).name, task_info, new APICallBack() {
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
            groupTaskParamsListAdapter.setmDBTaskList(dbTaskList);
            addTaskParamsListView.setAdapter(groupTaskParamsListAdapter);
        } else {
            groupTaskParamsListAdapter.setmDBTaskList(dbTaskList);
            addTaskParamsListView.setAdapter(groupTaskParamsListAdapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
