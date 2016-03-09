package com.gezhii.fitgroup.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.PushExperienceDto;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.tools.Screen;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/11/16.
 */
public class PushExperienceDialog extends Dialog {


    @InjectView(R.id.close_btn)
    ImageView closeBtn;
    @InjectView(R.id.content_text)
    TextView contentText;
    @InjectView(R.id.progress_view)
    RoundCornerProgressBar progressView;


    public PushExperienceDialog(Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public PushExperienceDialog(Context context, int theme) {
        super(context, theme);
    }

    PushExperienceDto pushExperienceDto;
    String contentTextString;
    int maxValue;
    int currentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_experience_dialog);
        ButterKnife.inject(this);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
//        progressView.setFgColor(getContext().getResources().getColor(android.R.color.transparent));
        progressView.setProgressBackgroundColor(getContext().getResources().getColor(R.color.gray_c8));
        progressView.setProgressColor(getContext().getResources().getColor(R.color.colorPrimary));
        progressView.setRadius(Screen.dip2px(4));
    }

    @Override
    public void show() {
        super.show();
        if (pushExperienceDto != null) {
            contentText.setText(contentTextString);
            progressView.setMax(maxValue);
            progressView.setProgress(currentValue);
        }
    }

    public void setPushExperienceDto(PushExperienceDto pushExperienceDto) {
        this.pushExperienceDto = pushExperienceDto;
        contentTextString = "+" + pushExperienceDto.experience_inr + "轻元素";
        if (pushExperienceDto.level_upgrade > 0) {
            contentTextString += "升为" + pushExperienceDto.level_upgrade + "级！";
        }
        maxValue = GroupLevelConfigModel.getInstance().getLevelMaxCount(pushExperienceDto.level);
        currentValue = (int) (GroupLevelConfigModel.getInstance().getLevelMaxCount(pushExperienceDto.level) * pushExperienceDto.progress);
    }
}
