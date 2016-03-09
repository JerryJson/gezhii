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
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.event.AddGroupTagsEvent;
import com.gezhii.fitgroup.model.GroupTagsModel;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.TagsView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/16.
 */
public class EditGroupTagFragment extends BaseFragment {
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
    @InjectView(R.id.tags_view)
    TagsView tagsView;
    @InjectView(R.id.tags_input)
    EditText tagsInput;
    @InjectView(R.id.add_tag_btn)
    ImageView addTagBtn;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(EditGroupTagFragment.class);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_group_tag_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        backBtn.setVisibility(View.GONE);
        backText.setText(R.string.cancel);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightText.setText(R.string.done);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AddGroupTagsEvent(GroupTagsModel.getInstance().getSelectList()));
                finish();
            }
        });
        titleText.setText("编辑工会标签");
        setView();
        return rootView;
    }

    private void setView() {
        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagString = tagsInput.getText().toString().trim();
                if (!TextUtils.isEmpty(tagString)) {
                    GroupTagsModel.getInstance().addTags(tagString);
                    tagsInput.setText("");
                    setView();
                }
            }
        });
        List<GroupTagsModel.GroupTags> groupTagsList = GroupTagsModel.getInstance().getGroupTagsList();
        tagsView.removeAllViews();
        for (final GroupTagsModel.GroupTags groupTags : groupTagsList) {
            groupTags.onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        groupTags.isSelect = false;
                        v.setSelected(false);
                        v.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
                    } else {
                        groupTags.isSelect = true;
                        v.setSelected(true);
                        v.setBackgroundResource(R.drawable.rounded_rectangle_primary);
                    }
                }
            };
            tagsView.addTags(groupTags);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), tagsInput);
        ButterKnife.reset(this);

    }
}
