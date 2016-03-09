package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.event.PostEditEvent;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignLeaveOrContentEditFragment;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/3/2.
 */
public class AddPostEditFragment extends BaseFragment {

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

    private String postContent;
//    private SignAddContentFragment signAddContentFragment;
//    private SignLeaveFragment signLeaveFragment;

    public static void start(Activity activity, String post_content) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("post_content", post_content);
        mainActivity.showNext(AddPostEditFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_leave_or_content_edit_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        backBtn.setVisibility(View.INVISIBLE);
        rightText.setText(getString(R.string.done));
        postContent = (String) getNewInstanceParams().get("post_content");

        if (postContent != null && postContent.length() > 0) {
            contentOrLeaveInput.setText(postContent);
        }else {
            contentOrLeaveInput.setHint("你想说什么");
        }
        contentOrLeaveInput.requestFocus();
        KeyBoardHelper.showKeyBoard(getActivity(), contentOrLeaveInput);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentOrLeaveInput.getText().toString().trim();
                EventBus.getDefault().post(new PostEditEvent(content));
                finish();
            }
        });
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), contentOrLeaveInput);
        ButterKnife.reset(this);
    }
}
