package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.TagsView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ycl on 15/10/23.
 */
public class GroupProfileSubFragment extends BaseFragment {

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
    @InjectView(R.id.group_name_text)
    TextView groupNameText;
    @InjectView(R.id.group_description_text)
    TextView groupDescriptionText;
    @InjectView(R.id.tags_view)
    TagsView tagsView;

    Group group;

    public static void start(Activity activity, Group group) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("group", group);
        mainActivity.group_profile_sub_id = mainActivity.showNext(GroupProfileSubFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_profile_sub_fragment, null);
        ButterKnife.inject(this, view);

        group = (Group) getNewInstanceParams().get("group");
        setView();
        return view;
    }


    public void setView() {
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
        titleText.setText("公会介绍");
        if (UserModel.getInstance().isGroupLeader()) {
            rightText.setText("编辑");
            rightText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditGroupFragment.start(getActivity());
                }
            });
        }

        if (group != null) {
            groupNameText.setText(group.getGroup_name());
            groupDescriptionText.setText(group.getDescription());

            if (!TextUtils.isEmpty(group.getTags())) {
                Gson gson = GsonHelper.getInstance().getGson();
                ArrayList<String> tags = gson.fromJson(UserModel.getInstance().getMyGroup().getTags(), new TypeToken<ArrayList<String>>() {
                }.getType());
                if (tags.size() > 0) {
                    tagsView.setTags(tags);
                }
            }
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);

    }
}
