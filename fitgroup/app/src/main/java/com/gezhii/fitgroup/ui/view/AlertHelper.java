package com.gezhii.fitgroup.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/5/8.
 */
public class AlertHelper {

    public static void showAlert(Context context, AlertParams params) {
        showAlert(context, params, 0);
    }

    public static void showAlert(Context context, final AlertParams params, int theme) {

        AlertDialog alertDialog = new AlertDialog(context);

        if (!TextUtils.isEmpty(params.getTitle())) {
            alertDialog.setTitle(params.getTitle());
        }
        if (!TextUtils.isEmpty(params.getMessage())) {
            alertDialog.setDescription(params.getMessage());
        }
        if (!TextUtils.isEmpty(params.getConfirmString()) && params.getConfirmListener() != null) {
            alertDialog.setConfirm(params.getConfirmString(), params.getConfirmListener());
        }
        if (!TextUtils.isEmpty(params.getCancelString()) && params.getCancelListener() != null) {
            alertDialog.setCancel(params.getCancelString(), params.getCancelListener());
        }

        if (params.view != null) {
            alertDialog.setView(params.view);
        }
//        Window dialogWindow = alertDialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.y = 100;
//        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
//        dialogWindow.setAttributes(lp);
        alertDialog.show();
    }

    public static AlertDialog createAlertDialog(Context context, final AlertParams params) {
        AlertDialog alertDialog = new AlertDialog(context);

        if (!TextUtils.isEmpty(params.getTitle())) {
            alertDialog.setTitle(params.getTitle());
        }
        if (!TextUtils.isEmpty(params.getMessage())) {
            alertDialog.setDescription(params.getMessage());
        }
        if (!TextUtils.isEmpty(params.getConfirmString()) && params.getConfirmListener() != null) {
            alertDialog.setConfirm(params.getConfirmString(), params.getConfirmListener());
        }
        if (!TextUtils.isEmpty(params.getCancelString()) && params.getCancelListener() != null) {
            alertDialog.setCancel(params.getCancelString(), params.getCancelListener());
        }
        if (params.view != null) {
            alertDialog.setView(params.view);
        }

        return alertDialog;
    }

    public static class AlertDialog extends Dialog {


        private OnClickListener mConfirmListener;
        private OnClickListener mCancelListener;

        @InjectView(R.id.title)
        TextView title;
        @InjectView(R.id.confirm)
        TextView confirm;
        @InjectView(R.id.cancel)
        TextView cancel;
        @InjectView(R.id.description)
        TextView description;
        @InjectView(R.id.root_layout)
        LinearLayout rootLayout;

        String titleString;
        String confirmString;
        String cancelString;
        String descriptionString;
        View v;


        public AlertDialog(Context context) {
            this(context, android.R.style.Theme_Translucent_NoTitleBar);
        }

        public AlertDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.alert_dialog_ui);
            ButterKnife.inject(this);
            if (!TextUtils.isEmpty(titleString)) {
                title.setText(titleString);
                title.setVisibility(View.VISIBLE);
            } else {
                title.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(descriptionString)) {
                description.setText(descriptionString);
                description.setVisibility(View.VISIBLE);
            } else {
                description.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(confirmString)) {
                confirm.setText(confirmString);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mConfirmListener.onClick(AlertDialog.this, 0);
                    }
                });
                confirm.setVisibility(View.VISIBLE);
            } else {
                confirm.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(cancelString)) {
                cancel.setText(cancelString);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCancelListener.onClick(AlertDialog.this, 1);
                    }
                });
                cancel.setVisibility(View.VISIBLE);
            } else {
                cancel.setVisibility(View.GONE);
            }
            if (v != null) {
                rootLayout.addView(v);
            }
        }

        public void setTitle(String title) {
            this.titleString = title;
        }

        public void setDescription(String descriptionString) {
            this.descriptionString = descriptionString;
        }


        public void setView(View v) {
            this.v = v;
        }

        public void setConfirm(String confirm, OnClickListener confirmListener) {
            this.confirmString = confirm;
            this.mConfirmListener = confirmListener;
        }

        public void setCancel(String cancel, OnClickListener cancelListener) {
            this.cancelString = cancel;
            this.mCancelListener = cancelListener;
        }
    }


    public static class AlertParams {
        private String title;
        private String message;
        private String confirmString;
        private String cancelString;
        private DialogInterface.OnClickListener confirmListener;
        private DialogInterface.OnClickListener cancelListener;
        private View view;
        private boolean isBlock;

        public boolean isBlock() {
            return isBlock;
        }

        public void setIsBlock(boolean isBlock) {
            this.isBlock = isBlock;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getConfirmString() {
            return confirmString;
        }

        public void setConfirmString(String confirmString) {
            this.confirmString = confirmString;
        }

        public String getCancelString() {
            return cancelString;
        }

        public void setCancelString(String cancelString) {
            this.cancelString = cancelString;
        }

        public DialogInterface.OnClickListener getConfirmListener() {
            return confirmListener;
        }

        public void setConfirmListener(DialogInterface.OnClickListener confirmListener) {
            this.confirmListener = confirmListener;
        }

        public DialogInterface.OnClickListener getCancelListener() {
            return cancelListener;
        }

        public void setCancelListener(DialogInterface.OnClickListener cancelListener) {
            this.cancelListener = cancelListener;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }

}


