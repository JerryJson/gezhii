package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.UserMessageDTO;
import com.gezhii.fitgroup.dto.basic.AboutMeMessage;
import com.gezhii.fitgroup.tools.DataKeeperHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 16/2/22.
 */
public class UserMessageModel {

    List<AboutMeMessage> emMessageList;
    private static final String TAG_USER_MESSAGE_LIST = "tag_user_message_list";
    private UserMessageDTO userMessageDTO;

    private int unreadGroupMessageCount = 0;

    private static class UserMessageModelHolder {
        public final static UserMessageModel sington = new UserMessageModel();
    }

    public static UserMessageModel getInstance() {
        return UserMessageModelHolder.sington;
    }


    public void addMessage(AboutMeMessage emMessage) {
        userMessageDTO = (UserMessageDTO) DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().get(TAG_USER_MESSAGE_LIST);
        if (userMessageDTO == null) {
            userMessageDTO = new UserMessageDTO();
        }
        emMessageList = userMessageDTO.getAboutMeMessageList();
        if (emMessageList == null) {
            emMessageList = new ArrayList<>();
        }
        userMessageDTO.setHasNewMessage(true);
        emMessageList.add(0, emMessage);
        userMessageDTO.setAboutMeMessageList(emMessageList);
        DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().put(TAG_USER_MESSAGE_LIST, userMessageDTO);
    }

    public List<AboutMeMessage> getEmMessageList() {
        userMessageDTO = (UserMessageDTO) DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().get(TAG_USER_MESSAGE_LIST);
        if (userMessageDTO == null) {
            userMessageDTO = new UserMessageDTO();
        }
        emMessageList = userMessageDTO.getAboutMeMessageList();
        if (emMessageList == null) {
            emMessageList = new ArrayList<>();
        }
        return userMessageDTO.getAboutMeMessageList();
    }

    public boolean hasNewMessage() {
        userMessageDTO = (UserMessageDTO) DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().get(TAG_USER_MESSAGE_LIST);
        if (userMessageDTO == null) {
            userMessageDTO = new UserMessageDTO();
            userMessageDTO.setHasNewMessage(false);
        }
        return userMessageDTO.getHasNewMessage();
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        userMessageDTO = (UserMessageDTO) DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().get(TAG_USER_MESSAGE_LIST);
        if (userMessageDTO == null) {
            userMessageDTO = new UserMessageDTO();
        }
        userMessageDTO.setHasNewMessage(hasNewMessage);
        DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().put(TAG_USER_MESSAGE_LIST, userMessageDTO);
    }

    public void clear() {
        DataKeeperHelper.getInstance().getUserMessageCacheDataKeeper().put(TAG_USER_MESSAGE_LIST, "");
        userMessageDTO = new UserMessageDTO();
        userMessageDTO.setAboutMeMessageList(new ArrayList<AboutMeMessage>());

    }

    public int getUnreadGroupMessageCount() {
        return unreadGroupMessageCount;
    }

    public void setUnreadGroupMessageCount(int unreadGroupMessageCount) {
        this.unreadGroupMessageCount = unreadGroupMessageCount;
    }
}
