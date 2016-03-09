package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.UserWeightModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/7.
 */
public class ChooseSexFragment extends BaseFragment {
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
    @InjectView(R.id.select_girl_layout)
    RelativeLayout selectGirlLayout;
    @InjectView(R.id.select_boy_layout)
    RelativeLayout selectBoyLayout;

    private HashMap<String, Object> hashMap = new HashMap<String, Object>();
    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(ChooseSexFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_sex_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("set", "sex"));
        setTitle();
        hashMap = getNewInstanceParams();
        selectGirlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserWeightModel.getInstance().setSex(0);
                InputHeightFragment.start(getActivity());
            }
        });
        selectBoyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserWeightModel.getInstance().setSex(1);
                InputHeightFragment.start(getActivity());
            }
        });
        return rootView;
    }

    public void setTitle() {
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
