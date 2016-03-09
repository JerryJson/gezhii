package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.GroupMemberApplication;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.AgreedJoinMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.NewMemberMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.RefusalJoinMessageExt;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.umeng.analytics.MobclickAgent;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupMemberApplicationListAdapter extends LoadMoreListViewAdapter {

    BaseFragment mFragment;
    Activity mContext;
    public GroupMemberApplicationListAdapter(Activity mContext,BaseFragment mFragment) {
        this.mFragment = mFragment;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_application_list_item, null);
        RecyclerView.ViewHolder itemViewHolder = new GroupMemberApplicationListViewHolder(rootView, getOnListItemClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final GroupMemberApplication groupMemberApplication = (GroupMemberApplication) total_data_list.get(position);

        GroupMemberApplicationListViewHolder viewHolder = (GroupMemberApplicationListViewHolder) holder;

        viewHolder.groupMemberNameText.setText(groupMemberApplication.user.getNick_name());
        QiniuHelper.bindImage(groupMemberApplication.user.getIcon(), viewHolder.groupMemberIcon);
        viewHolder.groupMemberLevelText.setText("" + groupMemberApplication.user.getLevel() + "级");
        viewHolder.applicationGroupReasonText.setText(groupMemberApplication.description);
        for(int i = 0; i < groupMemberApplication.user.getBadges().size(); i++)
        {
            ImageView badgeView = new ImageView(viewHolder.itemView.getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15),Screen.dip2px(15));
            params.setMargins(0, 0, Screen.dip2px(4), 0);
            badgeView.setLayoutParams(params);
            if(groupMemberApplication.user.getBadges().get(i).badge!=null){
                QiniuHelper.bindAvatarImage(groupMemberApplication.user.getBadges().get(i).badge.getIcon(), badgeView);
                viewHolder.badgesContainer.addView(badgeView);
            }

        }
        viewHolder.agreeApplicationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "application_list", UmengEvents.getEventMap("click", "同意"));
                EventBus.getDefault().post(new ShowLoadingEvent());
                API.checkApplication(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(),
                        groupMemberApplication.id, 2, new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) {
                                EventBus.getDefault().post(new CloseLoadingEvent());
//                                Toast.makeText(MyApplication.getApplication(), "审核通过", Toast.LENGTH_SHORT).show();
                                HuanXinHelper.sendGroupTextMessage(new NewMemberMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), groupMemberApplication.user.getNick_name()),
                                        UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), "欢迎@"+groupMemberApplication.user.getNick_name()+"加入我们，请先做一下自我介绍吧",
                                        new EMCallBack() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });
                                HuanXinHelper.sendPrivateTextMessage(new AgreedJoinMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())), groupMemberApplication.user.getHuanxin_id(),
                                        UserModel.getInstance().getUserNickName() + "同意了你的入会申请", new EMCallBack() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });

                                UserModel.getInstance().tryLoadRemote(true);
                                mFragment.finish();
                            }

                        });


            }
        });

        viewHolder.refuseApplicationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(mContext, "application_list", UmengEvents.getEventMap("click", "拒绝"));
                EventBus.getDefault().post(new ShowLoadingEvent());

                API.checkApplication(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(),
                        groupMemberApplication.id, 1, new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) {
                                EventBus.getDefault().post(new CloseLoadingEvent());
//                                Toast.makeText(MyApplication.getApplication(), "审核通过", Toast.LENGTH_SHORT).show();
                                HuanXinHelper.sendPrivateTextMessage(new RefusalJoinMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())), groupMemberApplication.user.getHuanxin_id(),
                                        UserModel.getInstance().getUserNickName() + "拒绝了你的入会申请", new EMCallBack() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });
                                mFragment.finish();
                            }
                        });
            }
        });
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_member_application_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_member_application_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupMemberApplicationListViewHolder extends BaseViewHolder {
        @InjectView(R.id.group_member_icon)
        ImageView groupMemberIcon;
        @InjectView(R.id.group_member_name_text)
        TextView groupMemberNameText;
        @InjectView(R.id.group_member_level_text)
        TextView groupMemberLevelText;
        @InjectView(R.id.application_group_reason_text)
        TextView applicationGroupReasonText;
        @InjectView(R.id.agree_application_text)
        TextView agreeApplicationText;
        @InjectView(R.id.refuse_application_text)
        TextView refuseApplicationText;
        @InjectView(R.id.badges_container)
        LinearLayout badgesContainer;
        GroupMemberApplicationListViewHolder(View view, OnListItemClickListener listItemClickListener) {
            super(view, listItemClickListener);
        }
    }
}
