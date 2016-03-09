package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

/**
 * Created by xianrui on 15/8/20.
 */
public class PrivateMessageDto {

    private List<MESSAGE> messageList;

    private int allUnReadMessageCount;
    private int applicationUnReadCount;

    public List<MESSAGE> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<MESSAGE> messageList) {
        this.messageList = messageList;
    }

    public int getAllUnReadMessageCount() {
        return allUnReadMessageCount;
    }

    public void setAllUnReadMessageCount(int allUnReadMessageCount) {
        this.allUnReadMessageCount = allUnReadMessageCount;
    }

    public int getApplicationUnReadCount() {
        return applicationUnReadCount;
    }

    public void setApplicationUnReadCount(int applicationUnReadCount) {
        this.applicationUnReadCount = applicationUnReadCount;
    }

    public static class MESSAGE {
        private Date last_message_time;
        private String last_message;
        private String huanxin_id;
        private int unread_message_count;

        public Date getLast_message_time() {
            return last_message_time;
        }

        public void setLast_message_time(Date last_message_time) {
            this.last_message_time = last_message_time;
        }

        public String getLast_message() {
            return last_message;
        }

        public void setLast_message(String last_message) {
            this.last_message = last_message;
        }

        public int getUnread_message_count() {
            return unread_message_count;
        }

        public void setUnread_message_count(int unread_message_count) {
            this.unread_message_count = unread_message_count;
        }

        public String getHuanxin_id() {
            return huanxin_id;
        }

        public void setHuanxin_id(String huanxin_id) {
            this.huanxin_id = huanxin_id;
        }


    }

    public static PrivateMessageDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (PrivateMessageDto) gson.fromJson(jsonString, new TypeToken<PrivateMessageDto>() {
        }.getType());
    }

}
