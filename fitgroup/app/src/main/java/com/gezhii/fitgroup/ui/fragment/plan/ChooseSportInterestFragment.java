package com.gezhii.fitgroup.ui.fragment.plan;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.TagsDTO;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.UserTagsView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zj on 16/2/18.
 */
public class ChooseSportInterestFragment extends BaseFragment {
    public static final String TAG = ChooseSportInterestFragment.class.getName();


    @InjectView(R.id.choose_next_step)
    TextView chooseNextStep;
    @InjectView(R.id.choose_tags_view)
    UserTagsView chooseTagsView;
    private ArrayList<Tag> list;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(ChooseSportInterestFragment.class, null);
    }

    private List<Tag> chooseTag;

    // private List<Integer> chooseTagIdList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.choose_sport_interest_fragment, null);
        ButterKnife.inject(this, rootView);
        getAndSetTagView();
        MobclickAgent.onEvent(getActivity(), "UserTags", UmengEvents.getEventMap("click", "load"));
        chooseNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseTag.size() > 0) {
                    GetVipUsersFragment.start(getActivity(), chooseTag);
                } else {
                    showToast("请至少选择一个标签");
                }
            }
        });
        return rootView;
    }

    private void getAndSetTagView() {
        chooseTag = new ArrayList<>();
        //chooseTagIdList = new ArrayList<>();
        chooseTagsView.setOnTagsChangeListener(new UserTagsView.OnTagsChangeListener() {
            @Override
            public void onTagsChange(View view, Tag tag) {
                if (view.isSelected()) {
                    chooseTag.add(tag);
                    //chooseTagIdList.add(tag.getId());
                } else {
                    chooseTag.remove(tag);
                    //chooseTagIdList.remove(tag.getId());
                }
            }
        });
        API.getVipUserTags(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                TagsDTO tagsDTO = TagsDTO.parserJson(response);
                chooseTagsView.removeAllViews();
                chooseTagsView.addTags(tagsDTO.getTags());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
