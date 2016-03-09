package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.PrivateMessageDto;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.PrivateMessageListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/8/18.
 */
public class PrivateMessageFragment extends BaseFragment {
    public static final String TAG = PrivateMessageFragment.class.getName();


    @InjectView(R.id.message_list)
    RecyclerView messageList;
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

    LinearLayoutManager mLinearLayoutManager;
    PrivateMessageListAdapter mMessageListAdapter;
    PrivateMessageDto mMessageDto;
    @InjectView(R.id.no_private_message_text)
    TextView noPrivateMessageText;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(PrivateMessageFragment.class);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.private_message_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("私信");
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        messageList.setLayoutManager(mLinearLayoutManager);
        mMessageDto = PrivateMessageModel.getInstance().getMessageDto();
        mMessageListAdapter = new PrivateMessageListAdapter(getActivity(), mMessageDto);
        if (mMessageDto.getMessageList().size() == 0) {
            noPrivateMessageText.setVisibility(View.VISIBLE);
        } else {
            noPrivateMessageText.setVisibility(View.INVISIBLE);
            messageList.setAdapter(mMessageListAdapter);
        }


        PrivateMessageModel.getInstance().addMessageChangeListener(new PrivateMessageModel.onMessageChangListener() {
            @Override
            public void onMessageChange() {
                mMessageDto = PrivateMessageModel.getInstance().getMessageDto();
                mMessageListAdapter.setmMessageDto(mMessageDto);
                mMessageListAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        refresh();
    }

    private void refresh() {
        mMessageDto = PrivateMessageModel.getInstance().getMessageDto();
        mMessageListAdapter.setmMessageDto(mMessageDto);
        mMessageListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                if (mMessageDto != null) {
                    PrivateChatFragment.start(getActivity(), mMessageDto.getMessageList().get(position).getHuanxin_id());
                }
            }
        });
        mMessageListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
