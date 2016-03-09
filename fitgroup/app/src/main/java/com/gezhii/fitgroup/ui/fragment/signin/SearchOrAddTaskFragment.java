package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.TaskDBModel;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.SelectTaskAdapter;
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
public class SearchOrAddTaskFragment extends BaseFragment {

    @InjectView(R.id.search_task_input)
    EditText searchTaskInput;
    @InjectView(R.id.cancel_search_text)
    TextView cancelSearchText;
    @InjectView(R.id.add_task_text)
    TextView addTaskText;
    @InjectView(R.id.add_task_layout)
    RelativeLayout addTaskLayout;
    @InjectView(R.id.select_task_list_view)
    RecyclerView selectTaskListView;

    SelectTaskAdapter selectTaskAdapter;

    private String keyWord;
    List<DBTask> mDBTaskList;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(SearchOrAddTaskFragment.class, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_or_add_task_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        addTaskLayout.setVisibility(View.GONE);
        selectTaskListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cancelSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addTaskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "search_result"));
                if (UserCustomerTaskModel.getInstance().isExistCustomerTask(keyWord)) {
                    showToast("该自定义任务已存在");
                } else {
                    GroupTask groupTask = new GroupTask();
                    groupTask.setTask_name(keyWord);
                    ChooseTaskTypeFragment.start(getActivity(), groupTask);
                }

            }
        });
        searchTaskInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyWord = searchTaskInput.getText().toString().trim();
                if (!TextUtils.isEmpty(keyWord)) {
                    mDBTaskList = TaskDBModel.getInstance().findDBTaskByName(keyWord);
                    if (mDBTaskList.size() > 0) {
                        selectTaskAdapter.setmDBTaskList(mDBTaskList);
                        selectTaskListView.setAdapter(selectTaskAdapter);
                        selectTaskListView.setVisibility(View.VISIBLE);
                        addTaskLayout.setVisibility(View.GONE);
                    } else {
                        selectTaskListView.setVisibility(View.GONE);
                        addTaskLayout.setVisibility(View.VISIBLE);
                        addTaskText.setText("创建\"" + keyWord + "\"");
                    }
                } else {
                    addTaskLayout.setVisibility(View.GONE);
                    selectTaskListView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        setView();
        return rootView;
    }

    private void setView() {
        if (selectTaskAdapter == null) {
            selectTaskAdapter = new SelectTaskAdapter();
        }
        selectTaskAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                MobclickAgent.onEvent(getActivity(), "task_list", UmengEvents.getEventMap("click", "search_result"));
                String task_info;
                if (UserCustomerTaskModel.getInstance().isFromUserCustomerTask) {
                    if (UserCustomerTaskModel.getInstance().isExistCustomerTask(mDBTaskList.get(position).name)) {
                        showToast("该自定义任务已存在");
                    } else {
                        UserCustomerTask userCustomerTask = new UserCustomerTask();
                        if ("step".equals(mDBTaskList.get(position).parameters)) {
                            userCustomerTask.setSign_type("step");
                            userCustomerTask.setStep_limit(mDBTaskList.get(position).step);
                        } else if ("body_weight".equals(mDBTaskList.get(position).parameters)) {
                            userCustomerTask.setSign_type("body_weight");
                        } else if ("image".equals(mDBTaskList.get(position).parameters)) {
                            userCustomerTask.setSign_type("image");
                        }else {
                            userCustomerTask.setSign_type("sign_once");
                        }
                        userCustomerTask.setTask_name(mDBTaskList.get(position).name);
                        UserCustomerTaskModel.getInstance().addUserCustomerTask(userCustomerTask);
                        MainActivity m = (MainActivity) getActivity();
                        m.popToFragment(m.add_task_fragment_id);
                        EventBus.getDefault().post(new CustomTaskChangeEvent());
                    }

                } else {
                    if ("step".equals(mDBTaskList.get(position).parameters)) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("input", "step");
                        hashMap.put("step_limit", mDBTaskList.get(position).step);
                        task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                    } else if ("body_weight".equals(mDBTaskList.get(position).parameters)) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("input", "body_weight");
                        task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                    } else if ("image".equals(mDBTaskList.get(position).parameters)) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("input", "image");
                        task_info = GsonHelper.getInstance().getGson().toJson(hashMap);
                    }else {
                        task_info = "{}";
                    }
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.addGroupTask(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), mDBTaskList.get(position).name, task_info, new APICallBack() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), searchTaskInput);
        ButterKnife.reset(this);
    }
}
