package com.gezhii.fitgroup.model;

import android.text.TextUtils;

import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupNoticeDto;
import com.gezhii.fitgroup.event.GroupNoticeEvent;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/27.
 */
public class GroupNoticeCacheModel {
    public final static String TAG_GROUP_NOTICE = "tag_group_notice";

    GroupNoticeDto mGroupNoticeDto;

    public GroupNoticeCacheModel() {
        String groupNoticeString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_GROUP_NOTICE, "");
        if (!TextUtils.isEmpty(groupNoticeString)) {
            mGroupNoticeDto = GroupNoticeDto.parserJson(groupNoticeString);
        }
    }


    private static class NoticeCacheModelHolder {
        public final static GroupNoticeCacheModel sington = new GroupNoticeCacheModel();
    }

    public static GroupNoticeCacheModel getInstance() {
        return NoticeCacheModelHolder.sington;
    }

    public void setGroupNotice(int notice_id) {
        API.getGroupNoticeHttp(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), notice_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                mGroupNoticeDto = GroupNoticeDto.parserJson(response);
                DataKeeperHelper.getInstance().getDataKeeper().put(TAG_GROUP_NOTICE, GsonHelper.getInstance().getGson().toJson(mGroupNoticeDto));
                EventBus.getDefault().post(new GroupNoticeEvent());
            }
        });
    }

    public void setGroupNotice(GroupNoticeDto groupNoticeDto) {
        mGroupNoticeDto = groupNoticeDto;
    }

    public GroupNoticeDto getGroupNoticeDto() {
        return mGroupNoticeDto;
    }

    public void clear() {
        mGroupNoticeDto = null;
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_GROUP_NOTICE, "");
    }
}
