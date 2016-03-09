package com.gezhii.fitgroup.model;

import android.text.TextUtils;

import com.easemob.chat.EMChatManager;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.UserDataChangeEvent;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;

import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/20.
 */
public class UserModel {


    private static final String TAG_USER_DTO = "tag_user_dto";

    private UserDto mUserDto;
    private long lastRefreshTime;
    public boolean isFromGroupSignin = false;//判断是否在公会里打卡，打完卡跳转不一样


    private static class UserModelHolder {
        public final static UserModel sington = new UserModel();
    }

    public static UserModel getInstance() {
        return UserModelHolder.sington;
    }

    private UserModel() {
        tryLoadLocal();
    }


    private boolean tryLoadLocal() {
        String userDtoString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_USER_DTO, "");
        if (!TextUtils.isEmpty(userDtoString)) {
            mUserDto = UserDto.parserJson(userDtoString);
        }
        return mUserDto != null;
    }

    public void tryLoadRemote(boolean isForce) {
        if (isLogin()) {
            if (isForce) {
                sendRequest();
            } else {
                if (System.currentTimeMillis() - lastRefreshTime > 5 * 60 * 1000) {
                    sendRequest();
                }
            }

        }
    }

    private void sendRequest() {
        API.getUserProfileHttp(getUserDto().getUser().getId(), new APICallBack() {

            @Override
            public void subRequestSuccess(String response) {
                UserDto userDto = UserDto.parserJson(response);
                if (userDto != null) {
                    updateUserDto(userDto);
                }
            }
        });
        lastRefreshTime = System.currentTimeMillis();
    }

    public String getUserHuanXinName() {
        if (mUserDto != null) {
            return mUserDto.getUser().getHuanxin_id();
        }
        return null;
    }

    public String getUserNickName() {
        if (mUserDto != null) {
            return mUserDto.getUser().getNick_name();
        }
        return null;
    }

    public String getUserIcon() {
        if (mUserDto != null) {
            return mUserDto.getUser().getIcon();
        }
        return null;
    }

    public int getUserId() {
        if (mUserDto != null) {
            return mUserDto.getUser().getId();
        }
        return -1;
    }

    public int getGroupId() {
        if (mUserDto != null && mUserDto.getGroup() != null) {
            return mUserDto.getGroup().getId();
        }
        return -1;
    }

    public void updateUserDto(UserDto userDto) {
        this.mUserDto = userDto;
        updateUserInfoCache(mUserDto.getUser());
        save();
        EventBus.getDefault().post(new UserDataChangeEvent());
    }


    public UserDto getUserDto() {

        if (mUserDto != null) {
            return mUserDto;
        } else {
            if (tryLoadLocal()) {
                return mUserDto;
            } else {
                return null;
            }
        }
    }

    public boolean isLogin() {
        if (getUserDto() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        mUserDto = null;
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_USER_DTO, "");
        EMChatManager.getInstance().logout();
        GroupNoticeCacheModel.getInstance().clear();
        PrivateMessageModel.getInstance().clear();
        SignInfoModel.getInstance().clear();
        SignRecordModel.getInstance().clear();
        UserCustomerTaskModel.getInstance().clear();
        UserMessageModel.getInstance().clear();
        EventBus.getDefault().post(new UserDataChangeEvent());
    }


    private void save() {
        if (mUserDto != null) {
            DataKeeperHelper.getInstance().getDataKeeper().put(TAG_USER_DTO, GsonHelper.getInstance().getGson().toJson(mUserDto));
        }
    }

    public boolean isGroupLeader() {
        return mUserDto.getUser().getType() == 3;
    }

    public Group getMyGroup() {
        if (mUserDto != null) {
            return mUserDto.getGroup();
        }
        return null;
    }

    private static void updateUserInfoCache(User user) {
        UserCacheModel.UserCacheInfo userCacheInfo = UserCacheModel.getInstance().getUserInfo(user.getHuanxin_id());
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheModel.UserCacheInfo();
        }

        String icon = user.getIcon();
        if (!TextUtils.isEmpty(icon)) {
            userCacheInfo.icon = icon;
        }
        String nickName = user.getNick_name();
        if (!TextUtils.isEmpty(nickName)) {
            userCacheInfo.nickName = nickName;
        }
        UserCacheModel.getInstance().setUserInfo(user.getHuanxin_id(), userCacheInfo);
    }


    public boolean isFirstSigninFlag() {
        return DataKeeperHelper.getInstance().getDataKeeper().getInt("is_first_signin", 0) == 0;
    }

    public void setFirstSigninFlag() {
        DataKeeperHelper.getInstance().getDataKeeper().putInt("is_first_signin", 1);
    }

    public boolean isFristSetWeightFlag() {
        return DataKeeperHelper.getInstance().getDataKeeper().getInt("is_first_set_weight", 0) == 0;
    }

    public void setFirstSetWeightFlag(int i) {//0是重新登录
        DataKeeperHelper.getInstance().getDataKeeper().putInt("is_first_set_weight", i);
    }

    public boolean isNeedPromptCreateGroup() {// 用户达到3级的时候是否需要提示创建公会
        if (!UserModel.getInstance().isGroupLeader() && UserModel.getInstance().getUserDto().getUser().getLevel() >= 3) {
            if (!DataKeeperHelper.getInstance().getDataKeeper().get("has_prompt", false)) {//之前没有提示过
                return true;
            }

        }
        return false;
    }

    public void setNotNeedPromptCreateGroup() {//不需要提醒创建公会
        DataKeeperHelper.getInstance().getDataKeeper().put("has_prompt", true);
    }

    public boolean isNeedIntroduceMyself() {//是否需要自我介绍
        return DataKeeperHelper.getInstance().getDataKeeper().get("is_need_introduce_myself", true);
    }

    public void setHasIntroduceMyself() {//设置不需要自我介绍了
        DataKeeperHelper.getInstance().getDataKeeper().put("is_need_introduce_myself", false);
    }

    public void setHideDeleteMemberPrompt() {//会长长按踢出会员提示
        DataKeeperHelper.getInstance().getDataKeeper().put("is_need_prompt_delete_member", false);
    }

    public boolean isNeedPromptDeleteMember() {//是否需要显示长按踢出会员提示
        if (UserModel.getInstance().isGroupLeader()) {
            return DataKeeperHelper.getInstance().getDataKeeper().get("is_need_prompt_delete_member", true);
        }
        return false;
    }
}
