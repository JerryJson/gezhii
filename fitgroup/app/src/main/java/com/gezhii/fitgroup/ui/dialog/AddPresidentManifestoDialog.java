package com.gezhii.fitgroup.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/10/21.
 */
public class AddPresidentManifestoDialog extends Dialog {


    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.president_manifesto_input)
    EditText presidentManifestoInput;

    Callback mCallback;

    public AddPresidentManifestoDialog(Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public AddPresidentManifestoDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_president_manifesto_dialog);
        ButterKnife.inject(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        titleText.setText(R.string.add_president_manifesto);
        rightText.setText(R.string.done);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String presidentManifestoString = presidentManifestoInput.getText().toString().trim();
                if (TextUtils.isEmpty(presidentManifestoString)) {
                    Toast.makeText(getContext(), "会长宣言不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    presidentManifestoInput.setText("");
                    if (mCallback != null) {
                        mCallback.onPresidentManifesto(presidentManifestoString);
                    }
                }
            }
        });
    }


    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    interface Callback {
        void onPresidentManifesto(String presidentManifestoString);
    }

}
