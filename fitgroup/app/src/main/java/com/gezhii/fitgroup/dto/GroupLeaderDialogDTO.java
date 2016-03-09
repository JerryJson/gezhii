package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fantasy on 15/11/10.
 */
public class GroupLeaderDialogDTO extends BaseDto{
    public List<DialogContent> dialog;


    public List<DialogContent> getDialog() {
        return dialog;
    }

    public void setDialog(List<DialogContent> dialog) {
        this.dialog = dialog;
    }

    public static class DialogContent {

        public HashMap q;
        public String a;

    }

    public static class Q {
        private String content;
        private int type;

        public void setContent(String content) {
            this.content = content;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public int getType() {
            return type;
        }
    }

    public static GroupLeaderDialogDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupLeaderDialogDTO) gson.fromJson(jsonString, new TypeToken<GroupLeaderDialogDTO>() {
        }.getType());
    }
}
