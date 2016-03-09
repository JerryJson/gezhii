package com.gezhii.fitgroup.api;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gezhii.fitgroup.model.VersionModel;
import com.gezhii.fitgroup.network.BaseHttp;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.GsonHelper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xianrui on 15/4/20.
 */
public class API {
    //1
    public static final String HOST = Config.hostUrl;

    public static final String LOGIN = "Login.do";
    public static final String GET_USER_PROFILE = "GetUserProfile.do";
    public static final String GET_USER_INFO = "GetUserInfo.do";
    public static final String SIGNIN = "Signin.do";
    public static final String GET_SIGNIN = "GetSignin.do";
    public static final String LIKE_USER_SIGNIN = "LikeUserSignin.do";
    public static final String GET_GROUP_SQUARE = "GetGroupSquare.do";
    public static final String APPLICATION_GROUP = "ApplicationGroup.do";
    public static final String GET_APPLICATION_LIST = "GetApplicationList.do";
    public static final String CHECK_APPLICATION = "CheckApplication.do";
    public static final String QUIT_GROUP = "QuitGroup.do";
    public static final String KICKOFF_GROUP = "KickoffGroup.do";
    public static final String PUBLISH_GROUP_NOTICE = "PublishGroupNotice.do";
    public static final String GET_GROUP_NOTICE = "GetGroupNotice.do";
    public static final String GET_GROUP_MEMBERS_WEEK_SORT = "GetGroupMembersWeekSort.do";
    public static final String GET_GROUP_MEMBERS_DAILY_SORT = "GetGroupMembersDailySort.do";
    public static final String CREATE_GROUP = "CreateGroup.do";
    public static final String GET_ACTIVE_GROUPS = "GetActiveGroups.do";
    public static final String GET_RECOMMEND_GROUPS = "GetRecommendGroups.do";
    public static final String GET_CONTRIBUTION_SORT_GROUPS = "GetContributionSortGroups.do";
    public static final String GET_GROUP_SIMPLE_PROFILE = "GetGroupSimpleProfile.do";
    public static final String GET_GROUP_PROFILE = "GetGroupProfile.do";
    public static final String GET_GROUP_NOTICES = "GetGroupNotices.do";
    public static final String GET_STAR_LEADER_BANNERS = "GetStarLeaderBanners.do";
    public static final String UPDATE_USER_PROFILE = "UpdateUserProfile.do";
    public static final String GET_ALL_GROUP_LEVEL_CONFIG = "GetAllGroupLevelConfigs.do";
    public static final String GET_GROUP_MEMBERS = "GetGroupMembers.do";
    public static final String SEARCH_GROUPS = "SearchGroups.do";
    public static final String LEADER_DEMISE = "LeaderDemise.do";
    public static final String GET_SIGNIN_HISTORY = "GetSigninHistory.do";

    public static final String GET_ALL_BADAGES = "GetAllBadges.do";

    public static final String GET_USER_LEVEL_CONFIGS = "GetUserLevelConfigs.do";
    public static final String GET_GROUP_LEADER_DOALOG = "GetGroupLeaderDialog.do";

    public static final String BIND_INVITATION_CODE = "BindInvitationCode.do";
    public static final String CHANGE_GROUP_PROFILE = "ChangeGroupProfile.do";
    public static final String GET_NEWBIE_GROUP_PROFILE = "GetNewBieGroupProfile.do";
    public static final String JOIN_NEWBIE_GROUP = "JoinNewBieGroup.do";
    public static final String GET_SPLASH = "GetSplash.do";
    public static final String GET_SIGNINS = "GetSignins.do";
    public static final String ADD_TODAY_WEIGHT = "AddTodayWeight.do";
    public static final String UP_LOAD_USER_GUIDE = "UploadUserGuide.do";
    public static final String GET_OTHER_USER_PROFILE = "GetOtherUserProfile.do";
    public static final String CHECK_ANDROID_VERSION = "CheckAndroidVersion.do";

    public static final String GET_GROUP_TAG_SCONFIG_FOR_GROUP = "GetGroupTagsConfigForGroup.do";
    public static final String ADD_GROUP_TASK = "AddGroupTask.do";
    public static final String GET_GROUP_TASKS = "GetGroupTasks.do";
    public static final String DELETE_GROUP_TASK = "DeleteGroupTask.do";
    public static final String GET_GROUP_TASKS_FOR_USER = "GetGroupTasksForUser.do";

    public static final String GET_GROUP_TASK_DAILY_SIGNIN_DETAIL = "GetGroupTaskDailySignin.do";

    public static final String GET_GROUPS_BY_TAG = "GetGroupsByTag.do";
    public static final String FAST_JOIN_GROUP = "FastJoinGroup.do";
    public static final String GET_GROUP_TAGS_CONFIG = "GetGroupTagsConfig.do";
    public static final String SEND_SMS = "SendSms.do";
    public static final String SEND_VOICE_CODE = "SendVoiceCode.do";
    public static final String GET_SIGNIN_HISTORY_BY_GROUP = "GetSigninHistoryByGroup.do";
    public static final String GET_HOT_TASKS = "GetHotTasks.do";
    public static final String SET_GROUP_NEED_CHECK = "SetGroupNeedCheck.do";
    public static final String GET_SIGNIN_HISTORY_BY_IDS = "GetSigninHistoryByIds.do";
    public static final String GET_FOLLOWING_USERS = "GetFollowingUsers.do";
    public static final String GET_FOLLOWED_USERS = "GetFollowedUsers.do";
    public static final String GET_ALL_CHANNELS = "GetAllChannels.do";
    public static final String GET_RECOMMEND_CHANNELS = "GetRecommendChannels.do";
    public static final String GET_RECOMMEND_SIGNINS = "GetRecommendSignins.do";

    public static final String GET_HOME_PAGE = "GetHomePage.do";
    public static final String FOLLOW_USER = "FollowUser.do";
    public static final String FOLLOW_CHANNEL = "FollowChannel.do";
    public static final String GET_SIGNIN_AND_POST_HISTORY = "GetSigninAndPostHistory.do";
    public static final String GET_CHANNEL_SIGNINS = "GetChannelSignins.do";
    public static final String GET_FOLLOWING_USERS_SIGNIN_HISTORY = "GetFollowingUsersSigninHistory.do";
    public static final String GET_RECOMMEND_VIP_USERS = "GetRecommendVipUsers.do";
    public static final String GET_MENTOR_SIGNIN_HISTORY = "GetMentorSigninHistory.do";

    public static final String GET_SIGNIN_TRACK = "GetSigninTrack.do";
    public static final String GET_BANNERS = "GetBanners.do";

    public static final String GET_SIGNIN_COMMENTS = "GetSigninComments.do";
    public static final String GET_VIP_USER_TAGS = "GetVipUserTags.do";
    public static final String GET_USER_APPLICATION_VIP_STATE = "GetUserApplicationVipState.do";
    public static final String APPLICATION_VIP = "ApplicationVip.do";
    public static final String GET_VIP_USERS = "GetVipUsers.do";
    public static final String COMMENT_SIGNIN = "CommentSignin.do";
    public static final String PUBLISH_POST = "PublishPost.do";
    public static final String GET_MENTEES = "GetMentees.do";
    public static final String GET_LIKE_SIGNIN_USERS = "GetLikeSignInUsers.do";

    public static final String UPDATE_USER_TAGS = "UpdateUserTags.do";
    public static final String ADOPT_MENTOR = "AdoptMentor.do";

    public static final String UPDATE_SIGNIN = "UpdateSignin.do";

    public static final String GET_SIGNIN_RELATIVE_INFO = "GetSigninRelativeInfo.do";
    public static final String UPLOAD_VIDEO = "UploadVideo.do";

    public static void cancelRequest(String tag) {
        BaseHttp.cancelAll(tag);
    }


    public static void loginHttp(String login_type, String third_party_id, String nick_name, String icon, String sms_code, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("login_type", login_type);
        params.put("third_party_id", String.valueOf(third_party_id));
        params.put("nick_name", nick_name);
        params.put("icon", icon);
        params.put("sms_code", sms_code);
        CommonAPI.postString(HOST + LOGIN, params, onRequestEnd);
    }

    public static void getUserProfileHttp(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("device", "Android" + VersionModel.getInstance().version);
        CommonAPI.postString(HOST + GET_USER_PROFILE, params, onRequestEnd);
    }

    public static void getUserInfoHttp(String huanxin_id_list, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("huanxin_id_list", huanxin_id_list);

        CommonAPI.postString(HOST + GET_USER_INFO, params, onRequestEnd);
    }

    public static void signinHttp(int user_id, String signin_info, @Nullable String description, @Nullable String img, int signin_type, int is_share, int is_default_img, String task_name,
                                  OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("signin_info", String.valueOf(signin_info));
        if (!TextUtils.isEmpty(description))
            params.put("description", description);
        if (!TextUtils.isEmpty(img))
            params.put("img", img);
        if (!TextUtils.isEmpty(task_name)) {
            params.put("task_name", task_name);
        }
        params.put("signin_type", String.valueOf(signin_type));
        params.put("is_default_img", String.valueOf(is_default_img));
        params.put("is_share", String.valueOf(is_share));
        CommonAPI.postString(HOST + SIGNIN, params, onRequestEnd);
    }

    public static void getSigninHttp(int user_id, int signin_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("signin_id", String.valueOf(signin_id));
        CommonAPI.postString(HOST + GET_SIGNIN, params, onRequestEnd);
    }

    public static void LikeUserSigninHttp(int user_id, int signin_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("signin_id", String.valueOf(signin_id));
        CommonAPI.postString(HOST + LIKE_USER_SIGNIN, params, onRequestEnd);
    }


    public static void getCreateGroupHttp(int user_id, String group_name, String description, @Nullable String slogan, @Nullable String tags, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_name", group_name);
        params.put("description", description);
        if (!TextUtils.isEmpty(slogan))
            params.put("slogan", slogan);
        if (!TextUtils.isEmpty(tags))
            params.put("tags", tags);
        CommonAPI.postString(HOST + CREATE_GROUP, params, onRequestEnd);
    }

    public static void getGroupSquareHttp(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_GROUP_SQUARE, params, onRequestEnd);
    }

    public static void applicationGroupHttp(int user_id, int group_id, String description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("description", description);
        CommonAPI.postString(HOST + APPLICATION_GROUP, params, onRequestEnd);
    }


    public static void checkApplication(int user_id, int group_id, int application_id, int state, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("application_id", String.valueOf(application_id));
        params.put("state", String.valueOf(state));
        CommonAPI.postString(HOST + CHECK_APPLICATION, params, onRequestEnd);
    }

    public static void quitGroupHttp(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + QUIT_GROUP, params, onRequestEnd);
    }


    public static void kickoffGroupHttp(int leader_id, int group_id, int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("leader_id", String.valueOf(leader_id));
        CommonAPI.postString(HOST + KICKOFF_GROUP, params, onRequestEnd);
    }

    public static void publishGroupNoticeHttp(int group_id, String description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("description", description);
        CommonAPI.postString(HOST + PUBLISH_GROUP_NOTICE, params, onRequestEnd);
    }

    public static void getGroupNoticeHttp(int user_id, int group_id, int notice_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("user_id", String.valueOf(user_id));
        params.put("notice_id", String.valueOf(notice_id));
        CommonAPI.postString(HOST + GET_GROUP_NOTICE, params, onRequestEnd);
    }

    public static void getGroupMembersWeekSortHttp(int group_id, Date date, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("date", GsonHelper.gsonDateFormat.format(date));
        CommonAPI.postString(HOST + GET_GROUP_MEMBERS_WEEK_SORT, params, onRequestEnd);
    }

    public static void getGroupMembersDailySortHttp(int group_id, Date date, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("date", GsonHelper.gsonDateFormat.format(date));
        CommonAPI.postString(HOST + GET_GROUP_MEMBERS_DAILY_SORT, params, onRequestEnd);
    }

    public static void getGroupSquare(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_GROUP_SQUARE, params, onRequestEnd);
    }

    public static void getActiveGroups(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_ACTIVE_GROUPS, params, onRequestEnd);
    }

    public static void getRecommendGroups(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_RECOMMEND_GROUPS, params, onRequestEnd);
    }

    public static void getContributionSortGroups(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_CONTRIBUTION_SORT_GROUPS, params, onRequestEnd);
    }

    public static void getGroupSimpleProfile(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_GROUP_SIMPLE_PROFILE, params, onRequestEnd);
    }

    public static void getGroupProfile(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_GROUP_PROFILE, params, onRequestEnd);
    }

    public static void applicationGroup(int user_id, int group_id, String description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        if (!description.equals(""))
            params.put("description", description);
        CommonAPI.postString(HOST + APPLICATION_GROUP, params, onRequestEnd);
    }

    public static void getGroupNotices(int page, int group_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_GROUP_NOTICES, params, onRequestEnd);
    }

    public static void getStarLeaderBanners(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
//        params.put("limit", String.valueOf(limit));
//        params.put("page", String.valueOf(page));

        CommonAPI.postString(HOST + GET_STAR_LEADER_BANNERS, params, onRequestEnd);
    }

    public static void updateUserProfileHttp(int user_id, String icon, String nick_name, float goal_weight, String age_description, String job, String sport_frequency, String sport_way, String self_description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        if (!TextUtils.isEmpty(icon))
            params.put("icon", icon);
        if (!TextUtils.isEmpty(nick_name))
            params.put("nick_name", nick_name);
        if (goal_weight > 0)
            params.put("goal_weight", String.valueOf(goal_weight));
        if (!TextUtils.isEmpty(age_description)) {
            params.put("age_description", age_description);
        }
        if (!TextUtils.isEmpty(job)) {
            params.put("job", job);
        }
        if (!TextUtils.isEmpty(sport_frequency)) {
            params.put("sport_frequency", sport_frequency);
        }
        if (!TextUtils.isEmpty(sport_way)) {
            params.put("sport_way", sport_way);
        }
        if (self_description != null) {
            params.put("self_description", self_description);
        }
        CommonAPI.postString(HOST + UPDATE_USER_PROFILE, params, onRequestEnd);
    }

    public static void updateUserProfileHttp(int user_id, int height, int gender, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("height", String.valueOf(height));
        params.put("gender", String.valueOf(gender));
        CommonAPI.postString(HOST + UPDATE_USER_PROFILE, params, onRequestEnd);
    }

    public static void TestDailtJob(OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        CommonAPI.postString(HOST + "TestDailyJob.do", params, onRequestEnd);
    }

    public static void TestTransparent(String user_huanxin_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_huanxin_id", user_huanxin_id);
        CommonAPI.postString(HOST + "TestTransparent.do", params, onRequestEnd);
    }

    public static void getAllGroupLevelConfig(OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        CommonAPI.postString(HOST + GET_ALL_GROUP_LEVEL_CONFIG, params, onRequestEnd);
    }

    public static void getApplicationList(int page, int user_id, int group_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_APPLICATION_LIST, params, onRequestEnd);
    }

    public static void getGroupMembers(int page, int user_id, int group_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_GROUP_MEMBERS, params, onRequestEnd);
    }

    public static void searchGroups(String key, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", key);

        CommonAPI.postString(HOST + SEARCH_GROUPS, params, onRequestEnd);
    }

    public static void leaderDemise(int leader_id, int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("leader_id", String.valueOf(leader_id));
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + LEADER_DEMISE, params, onRequestEnd);
    }

    public static void getSigninHistory(int page, int current_user_id, int view_user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("current_user_id", String.valueOf(current_user_id));
        params.put("view_user_id", String.valueOf(view_user_id));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_SIGNIN_HISTORY, params, onRequestEnd);
    }

    public static void changeGroupProfile(int group_id, String group_name, String slogan, String tags, String description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        if (!TextUtils.isEmpty(group_name)) {
            params.put("group_name", String.valueOf(group_name));
        }
        if (!TextUtils.isEmpty(slogan)) {
            params.put("slogan", String.valueOf(slogan));
        }
        if (!TextUtils.isEmpty(tags)) {
            params.put("tags", String.valueOf(tags));
        }
        if (!TextUtils.isEmpty(description)) {
            params.put("description", String.valueOf(description));
        }
        CommonAPI.postString(HOST + CHANGE_GROUP_PROFILE, params, onRequestEnd);
    }

    public static void getSigninsHttp(int user_id, Date begin_date, Date end_date, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("begin_date", GsonHelper.gsonDateFormat.format(begin_date));
        params.put("end_date", GsonHelper.gsonDateFormat.format(end_date));
        CommonAPI.postString(HOST + GET_SIGNINS, params, onRequestEnd);
    }

    public static void getAllBadages(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_ALL_BADAGES, null, onRequestEnd);
    }

    public static void getUserLevelConfigs(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_USER_LEVEL_CONFIGS, null, onRequestEnd);
    }

    public static void getGroupLeaderDialog(int leader_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("leader_id", String.valueOf(leader_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_GROUP_LEADER_DOALOG, params, onRequestEnd);
    }

    public static void bindInvitationCode(int user_id, String invitation_code, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("invitation_code", invitation_code);
        CommonAPI.postString(HOST + BIND_INVITATION_CODE, params, onRequestEnd);
    }

    public static void getNewbieGroupProfile(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_NEWBIE_GROUP_PROFILE, new HashMap<String, String>(), onRequestEnd);
    }

    public static void joinNewbieGroup(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + JOIN_NEWBIE_GROUP, params, onRequestEnd);
    }

    public static void getSplash(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_SPLASH, new HashMap<String, String>(), onRequestEnd);
    }

    public static void addTodayWeight(int user_id, float weight, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("weight", String.valueOf(weight));
        CommonAPI.postString(HOST + ADD_TODAY_WEIGHT, params, onRequestEnd);
    }

    public static void uploadUserGuide(int user_id, int gender, int height, float weight, float goal_weight, String guide_info, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("gender", String.valueOf(gender));
        params.put("height", String.valueOf(height));
        params.put("weight", String.valueOf(weight));
        params.put("goal_weight", String.valueOf(goal_weight));
        params.put("guide_info", guide_info);
        CommonAPI.postString(HOST + UP_LOAD_USER_GUIDE, params, onRequestEnd);
    }

    public static void getOtherUserProfile(int other_user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(other_user_id));
        CommonAPI.postString(HOST + GET_OTHER_USER_PROFILE, params, onRequestEnd);
    }

    public static void getOtherUserProfile(int self_user_id, int other_user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(other_user_id));
        params.put("self_user_id", String.valueOf(self_user_id));
        CommonAPI.postString(HOST + GET_OTHER_USER_PROFILE, params, onRequestEnd);
    }

    public static void checkAndroidVersion(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + CHECK_ANDROID_VERSION, new HashMap<String, String>(), onRequestEnd);
    }

    public static void getGroupTagsConfigForGroup(int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        if (group_id != -1) {
            params.put("group_id", String.valueOf(group_id));
        }
        CommonAPI.postString(HOST + GET_GROUP_TAG_SCONFIG_FOR_GROUP, params, onRequestEnd);
    }

    public static void addGroupTask(int user_id, int group_id, String task_name, String task_info, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("task_name", task_name);
        params.put("task_info", task_info);
        CommonAPI.postString(HOST + ADD_GROUP_TASK, params, onRequestEnd);
    }

    public static void getGroupTasks(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_GROUP_TASKS, params, onRequestEnd);
    }

    public static void deleteGroupTask(int user_id, int group_id, int task_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("task_id", String.valueOf(task_id));
        CommonAPI.postString(HOST + DELETE_GROUP_TASK, params, onRequestEnd);
    }

    public static void getGroupTasksForUser(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_GROUP_TASKS_FOR_USER, params, onRequestEnd);
    }

    public static void GetGroupTaskDailySignin(int group_id, int task_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("group_id", String.valueOf(group_id));
        params.put("task_id", String.valueOf(task_id));
        CommonAPI.postString(HOST + GET_GROUP_TASK_DAILY_SIGNIN_DETAIL, params, onRequestEnd);
    }

    public static void getGroupsByTag(int page, int tag_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("tag_id", String.valueOf(tag_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_GROUPS_BY_TAG, params, onRequestEnd);

    }

    public static void fastJoinGroup(int user_id, int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + FAST_JOIN_GROUP, params, onRequestEnd);
    }

    public static void GetGroupTagsConfig(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_GROUP_TAGS_CONFIG, null, onRequestEnd);
    }

    public static void SendSms(String phone_num, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", phone_num);
        CommonAPI.postString(HOST + SEND_SMS, params, onRequestEnd);
    }

    public static void SendVoiceCode(String phone_num, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("phone_number", phone_num);
        CommonAPI.postString(HOST + SEND_VOICE_CODE, params, onRequestEnd);
    }

    public static void getSigninHistoryByGroup(int page, int user_id, int group_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_SIGNIN_HISTORY_BY_GROUP, params, onRequestEnd);
    }

    public static void getHotTasks(int group_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(group_id));
        CommonAPI.postString(HOST + GET_HOT_TASKS, params, onRequestEnd);
    }

    public static void setGroupNeedCheck(int user_id, int group_id, int need_check, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("group_id", String.valueOf(group_id));
        params.put("need_check", String.valueOf(need_check));
        CommonAPI.postString(HOST + SET_GROUP_NEED_CHECK, params, onRequestEnd);
    }

    public static void getSigninHistoryByIds(List<Integer> sign_ids, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sign_ids", String.valueOf(sign_ids));
        CommonAPI.postString(HOST + GET_SIGNIN_HISTORY_BY_IDS, params, onRequestEnd);
    }

    public static void getFollowingUsers(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_FOLLOWING_USERS, params, onRequestEnd);
    }

    public static void getFollowedUsers(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_FOLLOWED_USERS, params, onRequestEnd);
    }

    public static void getAllChannels(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_ALL_CHANNELS, params, onRequestEnd);
    }

    public static void getRecommendChannels(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_RECOMMEND_CHANNELS, params, onRequestEnd);
    }

    public static void getRecommendSignins(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_RECOMMEND_SIGNINS, params, onRequestEnd);
    }

    public static void getHomePage(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_HOME_PAGE, params, onRequestEnd);
    }

    public static void followUser(int user_id, int following_user_id, int flag, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("following_user_id", String.valueOf(following_user_id));
        params.put("flag", String.valueOf(flag));
        CommonAPI.postString(HOST + FOLLOW_USER, params, onRequestEnd);
    }

    public static void getSigninAndPostHistory(int page, int current_user_id, int view_user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("current_user_id", String.valueOf(current_user_id));
        params.put("view_user_id", String.valueOf(view_user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_SIGNIN_AND_POST_HISTORY, params, onRequestEnd);
    }

    public static void getChannelSignins(int page, int user_id, int channel_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("channel_id", String.valueOf(channel_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_CHANNEL_SIGNINS, params, onRequestEnd);
    }

    public static void followChannel(int user_id, int following_channel_id, int flag, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("following_channel_id", String.valueOf(following_channel_id));
        params.put("flag", String.valueOf(flag));
        CommonAPI.postString(HOST + FOLLOW_CHANNEL, params, onRequestEnd);
    }

    public static void getRecommendVipUsers(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_RECOMMEND_VIP_USERS, params, onRequestEnd);
    }


    public static void getSigninTrack(int user_id, String begin_time, int day, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        if (!TextUtils.isEmpty(begin_time)) {
            params.put("begin_time", begin_time);
        }
        params.put("day", String.valueOf(day));
        CommonAPI.postString(HOST + GET_SIGNIN_TRACK, params, onRequestEnd);
    }

    public static void getBanners(OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        CommonAPI.postString(HOST + GET_BANNERS, params, onRequestEnd);
    }

    public static void getSigninComments(int page, int user_id, int signin_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("signin_id", String.valueOf(signin_id));
        params.put("limit", String.valueOf(limit));
        params.put("page", String.valueOf(page));
        CommonAPI.postString(HOST + GET_SIGNIN_COMMENTS, params, onRequestEnd);
    }

    public static void getVipUserTags(OnRequestEnd onRequestEnd) {
        CommonAPI.postString(HOST + GET_VIP_USER_TAGS, null, onRequestEnd);
    }


    public static void getFollowingUsersSigninHistory(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_FOLLOWING_USERS_SIGNIN_HISTORY, params, onRequestEnd);
    }

    public static void getMentorSigninHistory(int user_id, String end_date, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        if (!TextUtils.isEmpty(end_date)) {
            params.put("end_date", end_date);
        }
        CommonAPI.postString(HOST + GET_MENTOR_SIGNIN_HISTORY, params, onRequestEnd);
    }

    public static void getUserApplicationVipState(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_USER_APPLICATION_VIP_STATE, params, onRequestEnd);
    }

    public static void applicationVip(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + APPLICATION_VIP, params, onRequestEnd);

    }

    public static void getVipUsers(int user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        CommonAPI.postString(HOST + GET_VIP_USERS, params, onRequestEnd);
    }

    public static void updateUserTags(int user_id, List<Integer> list, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("tags", String.valueOf(list));
        CommonAPI.postString(HOST + UPDATE_USER_TAGS, params, onRequestEnd);
    }

    public static void commentSignin(int user_id, int signin_id, String description, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("signin_id", String.valueOf(signin_id));
        params.put("description", description);
        CommonAPI.postString(HOST + COMMENT_SIGNIN, params, onRequestEnd);
    }

    public static void publishPost(int user_id, int tag_id, String description, String img, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("tag_id", String.valueOf(tag_id));
        if (!TextUtils.isEmpty(description)) {
            params.put("description", description);
        }
        if (!TextUtils.isEmpty(img)) {
            params.put("img", img);
        }
        CommonAPI.postString(HOST + PUBLISH_POST, params, onRequestEnd);
    }

    public static void adoptMentor(int user_id, int mentor_user_id, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("mentor_user_id", String.valueOf(mentor_user_id));
        CommonAPI.postString(HOST + ADOPT_MENTOR, params, onRequestEnd);
    }

    public static void updateSignin(int signin_id, String description, String img, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("signin_id", String.valueOf(signin_id));
        if (!TextUtils.isEmpty(description)) {
            params.put("description", description);
        }
        if (!TextUtils.isEmpty(img)) {
            params.put("img", img);
        }
        CommonAPI.postString(HOST + UPDATE_SIGNIN, params, onRequestEnd);
    }

    public static void getSigninRelativeInfo(int user_id, String task_name, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("task_name", task_name);
        CommonAPI.postString(HOST + GET_SIGNIN_RELATIVE_INFO, params, onRequestEnd);
    }

    public static void getMentees(int page, int user_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_MENTEES, params, onRequestEnd);
    }

    public static void getLikeSigninUsers(int page, int signin_id, int limit, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("signin_id", String.valueOf(signin_id));
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        CommonAPI.postString(HOST + GET_LIKE_SIGNIN_USERS, params, onRequestEnd);
    }

    public static void uploadVideo(int user_id, String user_nick_name, String video_link, int duration, String task_name, OnRequestEnd onRequestEnd) {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", String.valueOf(user_id));
        params.put("user_nick_name", user_nick_name);
        params.put("video_link", video_link);
        params.put("duration", String.valueOf(duration));
        params.put("task_name", task_name);
        CommonAPI.postString(HOST + UPLOAD_VIDEO, params, onRequestEnd);
    }

}
