package com.gezhii.fitgroup.ui.fragment.me;

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

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.UserWeightModel;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/6.
 */
public class BmiFragment extends BaseFragment {


    @InjectView(R.id.close_img)
    ImageView closeImg;
    @InjectView(R.id.bmi_index_text)
    TextView bmiIndexText;
    @InjectView(R.id.figure_text)
    TextView figureText;
    @InjectView(R.id.bmi_explain_text)
    RelativeLayout bmiExplainText;
    @InjectView(R.id.bmi_contact_text)
    TextView bmiContactText;
    @InjectView(R.id.change_body_text)
    TextView changeBodyText;

    public static final String tagLightSlim = "偏瘦";
    public static final String tagStandard = "标准";
    public static final String tagLightFat = "偏胖";
    public static final String tagFat = "肥胖";
    @InjectView(R.id.empty_view)
    View emptyView;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(BmiFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bmi_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setView();
        return rootView;
    }

    public void setView() {
        float weight = UserModel.getInstance().getUserDto().getWeight_list().get(0).getWeight();
        int height = UserModel.getInstance().getUserDto().getUser().getHeight();
        float bmi = weight / (height * height) * 10000;
        float bmiShow = (float) (Math.round(bmi * 10)) / 10;
        bmiIndexText.setText(String.valueOf(bmiShow));

        if (bmi < 18.5) {
            figureText.setText(tagLightSlim);
        } else if (bmi < 24) {
            figureText.setText(tagStandard);
        } else if (bmi < 28) {
            figureText.setText(tagLightFat);
        } else {
            figureText.setText(tagFat);
        }
        changeBodyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserWeightModel.getInstance().setIsBmiChange(true);
                ChooseSexFragment.start(getActivity());
            }
        });
        if(bmi<14.5){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight=0;
            emptyView.setLayoutParams(layoutParams);
        }
        else if(bmi>=14.5 && bmi<=28){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight=bmi*2-29;
            emptyView.setLayoutParams(layoutParams);
        }else if(bmi>28){
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.weight=35;
            emptyView.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
