package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupTagDTO;
import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupStateChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.TagsView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/20.
 */
public class CreateGroupFragment extends BaseFragment {


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
    @InjectView(R.id.group_name_input)
    EditText groupNameInput;
    @InjectView(R.id.group_introduce_input)
    EditText groupIntroduceInput;
    @InjectView(R.id.group_base_inf_layout)
    LinearLayout groupBaseInfLayout;
    @InjectView(R.id.group_tags_text)
    TextView groupTagsText;
    @InjectView(R.id.group_tags_view)
    TagsView groupTagsView;
    @InjectView(R.id.group_create_done)
    TextView groupCreateDone;

    List<GroupTagConfig> groupTagConfigs;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(CreateGroupFragment.class);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_group_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "group_create", UmengEvents.getEventMap("click", "load"));
        backBtn.setVisibility(View.INVISIBLE);
        backText.setText("取消");
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText(R.string.create_group);
        groupCreateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "group_create", UmengEvents.getEventMap("click", "create"));
                if (inputComplete()) {
                    String groupName = groupNameInput.getText().toString().trim();
                    String groupDescription = groupIntroduceInput.getText().toString().trim();
                    String tagString = null;

                    if (groupTagConfigs != null) {
                        tagString = GsonHelper.getInstance().getGson().toJson(getSelectList());
                    }
                    Log.i("tagString", tagString);
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.getCreateGroupHttp(UserModel.getInstance().getUserDto().getUser().getId(), groupName, groupDescription, null, tagString,
                            new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) {
                                    EventBus.getDefault().post(new CloseLoadingEvent());
                                    EventBus.getDefault().post(new GroupStateChangeEvent());
                                    UserModel.getInstance().tryLoadRemote(true);
                                    showToast("创建工会成功");
                                    finish();
                                    GroupTaskFragment.start(getActivity());
                                }
                            });
                }
            }
        });

        API.getGroupTagsConfigForGroup(-1, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                GroupTagDTO groupTagDTO = GroupTagDTO.parserJson(response);
                groupTagConfigs = groupTagDTO.getGroup_tag_configs();
                initTagsView(groupTagConfigs);
            }
        });
        return rootView;
    }

    public List<String> getSelectList() {
        List<String> selectList = new ArrayList<>();
        for (GroupTagConfig groupTagConfig : groupTagConfigs) {
            if (groupTagConfig.getFlag() == 1) {
                MobclickAgent.onEvent(getActivity(), "group_create", UmengEvents.getEventMap("click", "tag", "tag", groupTagConfig.getName()));
                selectList.add(groupTagConfig.getName());
            }
        }
        return selectList;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initTagsView(List<GroupTagConfig> groupTagConfigs) {
        groupTagsView.removeAllViews();
        groupTagsView.addTags(groupTagConfigs);
    }


    public boolean inputComplete() {
        String groupName = groupNameInput.getText().toString().trim();
        String groupDescription = groupIntroduceInput.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            showToast("公会名称不可为空");
        } else if (TextUtils.isEmpty(groupDescription)) {
            showToast("公会介绍不可为空");
        } else if ((groupTagConfigs == null || getSelectList() == null || getSelectList().size() == 0)) {
            showToast("请至少选择一个标签");
        }
        if (TextUtils.isEmpty(groupName) || TextUtils.isEmpty(groupDescription) || (groupTagConfigs == null || getSelectList() == null || getSelectList().size() == 0)) {
            return false;
        }

        return true;
    }


    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), groupNameInput);
        ButterKnife.reset(this);
    }
}
