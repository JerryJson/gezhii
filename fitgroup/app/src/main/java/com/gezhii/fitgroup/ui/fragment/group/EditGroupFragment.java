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
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.event.AddGroupTagsEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.TagsView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/16.
 */
public class EditGroupFragment extends BaseFragment {
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

    List<String> groupTagList;
    List<GroupTagConfig> groupTagConfigs;
    GroupProfileSubFragment groupProfileSubFragment;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(EditGroupFragment.class, null);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.create_group_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText(R.string.edit_group);
        rightText.setText(R.string.done);
        groupCreateDone.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
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

    @OnClick(R.id.right_text)
    public void onDoneBtnClick(View v) {
        if (inputComplete()) {
            String groupName = groupNameInput.getText().toString().trim();
            String groupDescription = groupIntroduceInput.getText().toString().trim();
            //String tagString = GsonHelper.getInstance().getGson().toJson(groupTagList);
            String tagString = null;
            if (groupTagConfigs != null) {
                tagString = GsonHelper.getInstance().getGson().toJson(getSelectList());
            }
            API.changeGroupProfile(UserModel.getInstance().getUserDto().getGroup().getId(), groupName, null, tagString, groupDescription,
                    new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) {
                            UserModel.getInstance().tryLoadRemote(true);
                            showToast("公会信息修改成功");
                            MainActivity m = (MainActivity) getActivity();
                            m.popToFragment(m.group_profile_sub_id);
                        }
                    });
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void initTagsView(List<GroupTagConfig> groupTagConfigs) {
        groupTagsView.removeAllViews();
        Group group = UserModel.getInstance().getMyGroup();
        if (group != null && groupTagList == null) {
            groupNameInput.setText(group.getGroup_name());
            groupIntroduceInput.setText(group.getDescription());
            groupTagList = GsonHelper.getInstance().getGson().fromJson(group.getTags(), new TypeToken<List<String>>() {
            }.getType());
            if (groupTagList != null) {
                for (int i = 0; i < groupTagConfigs.size(); i++) {
                    for (int j = 0; j < groupTagList.size(); j++) {
                        if (groupTagConfigs.get(i).getName().equals(groupTagList.get(j))) {
                            groupTagConfigs.get(i).setFlag(1);
                        }
                    }
                }
            }
        }
        groupTagsView.addTags(groupTagConfigs);
    }

    public void onEvent(AddGroupTagsEvent addGroupTagsEvent) {
        groupTagList = addGroupTagsEvent.getTagsList();
    }


    public List<String> getSelectList() {
        List<String> selectList = new ArrayList<>();
        for (GroupTagConfig groupTagConfig : groupTagConfigs) {
            if (groupTagConfig.getFlag() == 1) {
                selectList.add(groupTagConfig.getName());
            }
        }
        return selectList;
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
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
