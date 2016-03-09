package com.gezhii.fitgroup.ui.fragment.me;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.SystemUtils;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/5.
 */
public class AboutFragment extends BaseFragment {
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
    @InjectView(R.id.vision_text)
    TextView visionText;
    @InjectView(R.id.update_text)
    TextView updateText;
    @InjectView(R.id.update_layout)
    LinearLayout updateLayout;
    @InjectView(R.id.allrights_text)
    TextView allrightsText;
    @InjectView(R.id.gezhi_text)
    TextView gezhiText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.about_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        setTitle();
        visionText.setText(getString(R.string.app_name) + " " + SystemUtils.getAppVersion(getActivity()));
        return rootView;
    }

    public void setTitle() {
        titleText.setText("关于");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
