package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.OtherUserProfileDto;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.UserAndChannelProfileListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/15.
 */
public class UserProfileFragment extends BaseFragment {
    public static final String TAG = UserProfileFragment.class.getName();

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;

    @InjectView(R.id.user_profile_info_list)
    LoadMoreListView userProfileInfoList;

    private UserAndChannelProfileListAdapter userAndChannelProfileListAdapter;
    private int other_user_id;

    public static void start(Activity activity, int other_user_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("other_user_id", other_user_id);
        mainActivity.showNext(UserProfileFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.user_profile_fragment, null);
        ButterKnife.inject(this, rootview);

        userProfileInfoList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getUserInfo();

        return rootview;
    }

    private void getUserInfo() {
        other_user_id = (int) getNewInstanceParams().get("other_user_id");
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getOtherUserProfile(UserModel.getInstance().getUserId(), other_user_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {

                final OtherUserProfileDto otherUserProfileDto = OtherUserProfileDto.parserJson(response);

                UserCacheModel.getInstance().setUserNickName(otherUserProfileDto.getUser().getHuanxin_id(), otherUserProfileDto.getUser().getNick_name());
                UserCacheModel.getInstance().setUserIcon(otherUserProfileDto.getUser().getHuanxin_id(), otherUserProfileDto.getUser().getIcon());

                titleText.setText(otherUserProfileDto.getUser().getNick_name());
                rightImg.setImageResource(R.mipmap.private_message_icon);
                rightImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PrivateChatFragment.start(getActivity(), otherUserProfileDto.getUser().getHuanxin_id(), otherUserProfileDto.getUser().getNick_name());
                    }
                });
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

                API.getSigninAndPostHistory(1, other_user_id, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                        userAndChannelProfileListAdapter = new UserAndChannelProfileListAdapter(getActivity(), UserAndChannelProfileListAdapter.FLAG_USER);
                        userAndChannelProfileListAdapter.init_data_list(signHistoryDto.data_list);
                        userAndChannelProfileListAdapter.getTotal_data_list().add(0, otherUserProfileDto);
                        userProfileInfoList.setLoadMoreListViewAdapter(userAndChannelProfileListAdapter);

                        if (signHistoryDto.data_list.size() == Config.loadPageCount) {
                            Object[] params = new Object[4];
                            params[0] = 1;
                            params[1] = other_user_id;
                            params[2] = UserModel.getInstance().getUserId();
                            params[3] = Config.loadPageCount;

                            userAndChannelProfileListAdapter.setIsHasMore(true);
                            userProfileInfoList.setApiAutoInvoker("getSigninAndPostHistory", params, SignHistoryDto.class);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
