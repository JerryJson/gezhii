package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.FollowStateChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/17.
 */
public class RecommendVipUserAdapter extends LoadMoreListViewAdapter {

    private Activity mContext;

    public RecommendVipUserAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(mContext, R.layout.recommend_vip_item, null);
        return new UserItemHolder(view, getOnListItemClickListener());
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final UserItemHolder mHolder = (UserItemHolder) holder;
        final User user = (User) total_data_list.get(position);

        mHolder.vipUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment.start(mContext, user.getId());
            }
        });
        QiniuHelper.bindImage(user.getIcon(), mHolder.userListIcon);
        if (user.getVip() == 1) {
            mHolder.userListVip.setVisibility(View.VISIBLE);
        } else {
            mHolder.userListVip.setVisibility(View.GONE);
        }
        mHolder.userListDesc.setText(user.getSelf_description());
        if (user.getIs_following() == 1) {
            mHolder.userListFollowingCount.setText("已关注");
            mHolder.userListFollowingCount.setSelected(true);
            mHolder.userListFollowingCount.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mHolder.userListFollowingCount.setText("关注 " + user.getFollowed_count());
            mHolder.userListFollowingCount.setSelected(false);
            mHolder.userListFollowingCount.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
        }
        mHolder.userListFollowingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int changeFlag = mHolder.userListFollowingCount.isSelected() == true ? 0 : 1;
                if (changeFlag == 0) {
                    AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                    alertParams.setMessage("确定要取消关注吗");
                    alertParams.setTitle("取消关注");
                    alertParams.setCancelString("取消");
                    alertParams.setConfirmString("确定");
                    alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            API.followUser(UserModel.getInstance().getUserId(), user.getId(), changeFlag, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new FollowStateChangeEvent(1));
                                    mHolder.userListFollowingCount.setText("关注 " + user.getFollowed_count());
                                    mHolder.userListFollowingCount.setSelected(false);
                                    mHolder.userListFollowingCount.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                                    UserModel.getInstance().tryLoadRemote(true);
                                    user.setIs_following(0);
                                }
                            });
                        }
                    });
                    alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertHelper.showAlert(mContext, alertParams);
                } else {
                    API.followUser(UserModel.getInstance().getUserId(), user.getId(), changeFlag, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new FollowStateChangeEvent(0));
                            UserModel.getInstance().tryLoadRemote(true);
                            mHolder.userListFollowingCount.setText("已关注");
                            mHolder.userListFollowingCount.setSelected(true);
                            mHolder.userListFollowingCount.setTextColor(mContext.getResources().getColor(R.color.white));
                            user.setIs_following(1);
                        }
                    });
                }
            }
        });

        mHolder.userListName.setText(user.getNick_name());
    }

    static class UserItemHolder extends BaseViewHolder {
        @InjectView(R.id.user_list_icon)
        RectImageView userListIcon;
        @InjectView(R.id.user_list_vip)
        RectImageView userListVip;
        @InjectView(R.id.user_list_name)
        TextView userListName;
        @InjectView(R.id.user_list_following_count)
        TextView userListFollowingCount;
        @InjectView(R.id.user_list_desc)
        TextView userListDesc;
        @InjectView(R.id.vip_user_layout)
        LinearLayout vipUserLayout;

        public UserItemHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}
