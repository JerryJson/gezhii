package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.AllSigninCommentsDto;
import com.gezhii.fitgroup.event.CommentATailEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.CommentsListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/18.
 */
public class AllCommentsFragment extends BaseFragment {
    public static final String TAG = AllCommentsFragment.class.getName();

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;

    @InjectView(R.id.all_comments_list)
    LoadMoreListView allCommentsList;
    @InjectView(R.id.comment_input)
    EditText commentInput;
    @InjectView(R.id.send_text_btn)
    TextView sendTextBtn;

    private CommentsListAdapter commentsListAdapter;
    int signin_id;

    public static void start(Activity activity, int signin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("signin_id", signin_id);
        mainActivity.showNext(AllCommentsFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.allcomments_fragment, null);
        ButterKnife.inject(this, rootView);
        EventBus.getDefault().register(this);
        allCommentsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        setView();
        signin_id = (int) getNewInstanceParams().get("signin_id");
        refreshComments();
        return rootView;
    }

    private void refreshComments() {
        API.getSigninComments(1, UserModel.getInstance().getUserId(), signin_id, Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                AllSigninCommentsDto dto = AllSigninCommentsDto.parserJson(response);
                commentsListAdapter = new CommentsListAdapter(getActivity());
                commentsListAdapter.init_data_list(dto.data_list);
                allCommentsList.setLoadMoreListViewAdapter(commentsListAdapter);

                if (dto.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[4];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = signin_id;
                    params[3] = Config.loadPageCount;
                    allCommentsList.setHasMore(true);
                    allCommentsList.setApiAutoInvoker("getSigninComments", params, AllSigninCommentsDto.class);
                }
            }
        });
    }

    public void onEventMainThread(CommentATailEvent commentATailEvent) {
        commentInput.setText("回复" + commentATailEvent.getUserName() + "：");
    }

    private void setView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyBoardHelper.hideKeyBoard(getActivity(),commentInput);
                finish();
            }
        });
        titleText.setText("所有评论");
        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (length > 0) {
                    sendTextBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });

        sendTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commit = commentInput.getText().toString().trim();
                if (commit.length() > 0) {
                    API.commentSignin(UserModel.getInstance().getUserId(), signin_id, commit, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            commentInput.setText("");
                            showToast("评论成功！");
                            refreshComments();
                        }
                    });
                } else {
                    showToast("评论不能为空！");
                }

            }
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
