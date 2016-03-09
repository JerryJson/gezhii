package com.gezhii.fitgroup.ui.fragment.signin;

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
import com.gezhii.fitgroup.dto.basic.SignLeaveOrContent;
import com.gezhii.fitgroup.event.SignLeaveOrContentEditEvent;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/10.
 */
public class SignLeaveOrContentEditFragment extends BaseFragment {

    public static final String SIGN_LEAVE_OR_CONTENT = "signLeaveOrContent";
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
    @InjectView(R.id.content_or_leave_input)
    EditText contentOrLeaveInput;

    private SignLeaveOrContent signLeaveOrContent;
//    private SignAddContentFragment signAddContentFragment;
//    private SignLeaveFragment signLeaveFragment;

    public static void start(Activity activity, SignLeaveOrContent signLeaveOrContent) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(SIGN_LEAVE_OR_CONTENT, signLeaveOrContent);
        mainActivity.showNext(SignLeaveOrContentEditFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_leave_or_content_edit_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        backBtn.setVisibility(View.INVISIBLE);
        rightText.setText(getString(R.string.done));
        signLeaveOrContent = (SignLeaveOrContent) getNewInstanceParams().get(SIGN_LEAVE_OR_CONTENT);
//        if (signLeaveOrContent.getIsLeaveSign()) {
//            signLeaveFragment = (SignLeaveFragment) getNewInstanceParams().get("fragment");
//        } else {
//            signAddContentFragment = (SignAddContentFragment) getNewInstanceParams().get("fragment");
//        }

        contentOrLeaveInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), contentOrLeaveInput);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signLeaveOrContent.setContent(contentOrLeaveInput.getText().toString().trim());
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("content", contentOrLeaveInput.getText().toString().trim());
                Log.i("darren", contentOrLeaveInput.getText().toString().trim());
                EventBus.getDefault().post(new SignLeaveOrContentEditEvent(params));
//                if (signLeaveOrContent.getIsLeaveSign()) {
//                    EventBus.getDefault().post(new SignLeaveOrContentEditEvent(params));
//                    signLeaveFragment.signContentInput.setText(signLeaveOrContent.getContent());
//                } else {
//                    EventBus.getDefault().post(new SignLeaveOrContentEditEvent(params));
//                    signAddContentFragment.signContentInput.setText(signLeaveOrContent.getContent());
//                }
                finish();
            }
        });

        if (signLeaveOrContent.getIsLeaveSign()) {
            if (TextUtils.isEmpty(signLeaveOrContent.getContent())) {
                contentOrLeaveInput.setHint(getString(R.string.sign_leave_hint_text));
            } else {
                contentOrLeaveInput.setText(signLeaveOrContent.getContent().toString());
            }

        } else {
            if (TextUtils.isEmpty(signLeaveOrContent.getContent())) {
                contentOrLeaveInput.setHint(getString(R.string.sing_content_hint_text));
            } else {
                contentOrLeaveInput.setText(signLeaveOrContent.getContent().toString());
            }
        }

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), contentOrLeaveInput);
        ButterKnife.reset(this);
    }
}
