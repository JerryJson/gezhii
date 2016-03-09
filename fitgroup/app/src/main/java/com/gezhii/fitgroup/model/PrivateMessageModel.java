package com.gezhii.fitgroup.model;

import android.text.TextUtils;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.dto.PrivateMessageDto;
import com.gezhii.fitgroup.event.ApplicationUnCountChangeEvent;
import com.gezhii.fitgroup.event.ChatMessageEvent;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/8/20.
 */
public class PrivateMessageModel {
    private static final String TAG = PrivateMessageModel.class.getName();
    private static final String TAG_MESSAGE_DTO = "tag_message_dto";


    private PrivateMessageDto mMessageDto;

    private List<onMessageChangListener> mMessageChangListenerList;
    private List<OnUnReadMessageCountChangeListener> mOnUnReadMessageCountChangeListenerList;


    private PrivateMessageModel() {
        String messageDtoString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_MESSAGE_DTO, "");
        if (!TextUtils.isEmpty(messageDtoString)) {
            mMessageDto = PrivateMessageDto.parserJson(messageDtoString);
        } else {
            mMessageDto = new PrivateMessageDto();
            mMessageDto.setMessageList(new ArrayList<PrivateMessageDto.MESSAGE>());
        }
        mMessageChangListenerList = new ArrayList<>();
        mOnUnReadMessageCountChangeListenerList = new ArrayList<>();

        EventBus.getDefault().registerSticky(this);
    }

    private static class MessageModelHolder {
        public final static PrivateMessageModel sington = new PrivateMessageModel();
    }


    public void clear() {
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_MESSAGE_DTO, "");
        mMessageDto = new PrivateMessageDto();
        mMessageDto.setMessageList(new ArrayList<PrivateMessageDto.MESSAGE>());

    }


    public static PrivateMessageModel getInstance() {
        return MessageModelHolder.sington;
    }

    public void onEventMainThread(ChatMessageEvent chatMessageEvent) {
        LinkedBlockingQueue<EMMessage> queue = MyApplication.getApplication().getHuanXinHelper().getMessageQueue();
        if (queue != null) {
            for (int i = 0; i < queue.size(); i++) {
                EMMessage emMessage = queue.poll();
                addMessage(emMessage);
            }
        }
        for (onMessageChangListener onMessageChangListener : mMessageChangListenerList) {
            onMessageChangListener.onMessageChange();
        }
        save();
    }

    private void save() {
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_MESSAGE_DTO, GsonHelper.getInstance().getGson().toJson(mMessageDto));
    }

    public PrivateMessageDto.MESSAGE findMessageByHuanxinId(String huanxin_id) {
        PrivateMessageDto.MESSAGE targetMessage = null;
        for (PrivateMessageDto.MESSAGE message : mMessageDto.getMessageList()) {
            if (message.getHuanxin_id().equals(huanxin_id)) {
                targetMessage = message;
                break;
            }
        }
        return targetMessage;
    }

    public void addMessage(EMMessage emMessage) {
        PrivateMessageDto.MESSAGE targetMessage = null;
        for (PrivateMessageDto.MESSAGE message : mMessageDto.getMessageList()) {
            if (message.getHuanxin_id().equals(emMessage.getFrom())) {
                targetMessage = message;
                break;
            }
        }

        if (targetMessage == null) {
            targetMessage = new PrivateMessageDto.MESSAGE();
        } else {
            mMessageDto.getMessageList().remove(targetMessage);
        }
        String messageString;
        if (emMessage.getType() == EMMessage.Type.TXT) {
            messageString = ((TextMessageBody) emMessage.getBody()).getMessage();
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            messageString = "[图片]";
        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
            messageString = "[语音]";
        } else {
            messageString = "";
        }
//        targetMessage.setUser(ContactModel.getInstance().findUserByHuanxinID(emMessage.getFrom()));

//        UserCacheModel.UserCacheInfo userCacheInfo = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom());
//        targetMessage.setIcon(userCacheInfo.icon);
//        targetMessage.setUser_name(userCacheInfo.nickName);
        targetMessage.setHuanxin_id(emMessage.getFrom());
        targetMessage.setLast_message(messageString);
        targetMessage.setLast_message_time(new Date(emMessage.getMsgTime()));
        targetMessage.setUnread_message_count(targetMessage.getUnread_message_count() + 1);
        mMessageDto.getMessageList().add(0, targetMessage);
        save();
        updateAllUnReadCount();
//        checkContactList(emMessage.getTo());
    }


    public void addSendMessage(EMMessage emMessage) {
        PrivateMessageDto.MESSAGE targetMessage = null;
        for (PrivateMessageDto.MESSAGE message : mMessageDto.getMessageList()) {
            if (message.getHuanxin_id().equals(emMessage.getTo())) {
                targetMessage = message;
                break;
            }
        }

        if (targetMessage == null) {
            targetMessage = new PrivateMessageDto.MESSAGE();
        } else {
            mMessageDto.getMessageList().remove(targetMessage);
        }
        String messageString;
        if (emMessage.getType() == EMMessage.Type.TXT) {
            messageString = ((TextMessageBody) emMessage.getBody()).getMessage();
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            messageString = "[图片]";
        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
            messageString = "[语音]";
        } else {
            messageString = "";
        }
        targetMessage.setHuanxin_id(emMessage.getTo());
        targetMessage.setLast_message(messageString);
        targetMessage.setLast_message_time(new Date(emMessage.getMsgTime()));
        targetMessage.setUnread_message_count(targetMessage.getUnread_message_count());
        mMessageDto.getMessageList().add(0, targetMessage);
        save();
    }

    private void updateAllUnReadCount() {
        int allCount = 0;
        for (PrivateMessageDto.MESSAGE message : mMessageDto.getMessageList()) {
            allCount += message.getUnread_message_count();
        }
        mMessageDto.setAllUnReadMessageCount(allCount);
        save();
        for (OnUnReadMessageCountChangeListener onUnReadMessageCountChangeListener : mOnUnReadMessageCountChangeListenerList) {
            onUnReadMessageCountChangeListener.onUnReadMessageCountChange(allCount);
        }
    }


    public void cleanRedPoint(String user_huanxin_id) {
        for (PrivateMessageDto.MESSAGE message : mMessageDto.getMessageList()) {
            if (message.getHuanxin_id().equals(user_huanxin_id)) {
                message.setUnread_message_count(0);
            }
        }
        save();
        updateAllUnReadCount();
    }

//    public void updateMessageUser() {
//        for (MessageDto.MESSAGE message : mMessageDto.getMessageList()) {
//            message.setUser(ContactModel.getInstance().findUserByHuanxinID(message.getUser().getHuanxin_id()));
//        }
//        save();
//    }

    public PrivateMessageDto getMessageDto() {
        return mMessageDto;
    }


    public void setApplicationUnReadCount(int count) {
        mMessageDto.setApplicationUnReadCount(count);
        save();
        EventBus.getDefault().post(new ApplicationUnCountChangeEvent());
    }


    public void addMessageChangeListener(onMessageChangListener onMessageChangListener) {
        if (mMessageChangListenerList != null) {
            if (!mMessageChangListenerList.contains(onMessageChangListener)) {
                mMessageChangListenerList.add(onMessageChangListener);
            }

        }
    }

    public void removeMessageChangeLister(onMessageChangListener onMessageChangListener) {
        if (mMessageChangListenerList != null) {
            if (mMessageChangListenerList.contains(onMessageChangListener)) {
                mMessageChangListenerList.remove(onMessageChangListener);
            }
        }
    }

    public void addMessageUnReadCountChangeListener(OnUnReadMessageCountChangeListener onUnReadMessageCountChangeListener) {
        if (mOnUnReadMessageCountChangeListenerList != null) {
            if (!mOnUnReadMessageCountChangeListenerList.contains(onUnReadMessageCountChangeListener)) {
                mOnUnReadMessageCountChangeListenerList.add(onUnReadMessageCountChangeListener);
            }
        }
    }

    public void removeMessageUnReadCountChangeListener(OnUnReadMessageCountChangeListener onUnReadMessageCountChangeListener) {
        if (mMessageChangListenerList != null) {
            if (mOnUnReadMessageCountChangeListenerList.contains(onUnReadMessageCountChangeListener)) {
                mOnUnReadMessageCountChangeListenerList.remove(onUnReadMessageCountChangeListener);
            }
        }
    }


    public interface OnUnReadMessageCountChangeListener {
        void onUnReadMessageCountChange(int count);
    }


    public interface onMessageChangListener {
        public void onMessageChange();
    }


}
