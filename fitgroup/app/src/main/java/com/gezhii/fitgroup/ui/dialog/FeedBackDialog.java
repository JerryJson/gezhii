package com.gezhii.fitgroup.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.qrcode.EmailHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/16.
 */
public class FeedBackDialog extends Dialog {
    Context mContext;
    @InjectView(R.id.send_email_text)
    TextView sendEmailText;
    @InjectView(R.id.customer_service_qq)
    TextView customerServiceQq;
    @InjectView(R.id.cancel)
    TextView cancel;

    public FeedBackDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public FeedBackDialog(Context context) {

        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_back_dialog_ui);
        ButterKnife.inject(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sendEmailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailHelper.sendEmail(mContext, mContext.getString(R.string.feed_back));
                dismiss();
            }
        });
        customerServiceQq.setText(mContext.getString(R.string.customer_service_qq) + mContext.getString(R.string.service_qq_num));
        customerServiceQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + mContext.getString(R.string.service_qq_num);
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                dismiss();
            }
        });
    }
}
