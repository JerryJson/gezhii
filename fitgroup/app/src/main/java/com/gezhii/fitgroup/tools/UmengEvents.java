package com.gezhii.fitgroup.tools;


import java.util.HashMap;

/**
 * Created by xianrui on 15/5/18.
 */
public class UmengEvents {


    public static HashMap<String, String> getEventMap(String... params) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i + 1 < params.length; i += 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }

    // MobclickAgent.onEvent(mContext, "login", UmengEvents.getEventMap("click", "微信登入"));

//    以下为1.3
//    private let mobKey = "UserSigninTrack"
//    tag页面 MobClick.event(self.mobKey, attributes: ["click": "load"]) UserTags
//    选达人（区分tag和任务进入）
//            "VIPUsers"
//            MobClick.event(self.mobKey, attributes: ["click": "load"])
//    from "userTasks" / "tag"
//    运动轨迹
//    "UserSigninTrack"
//            MobClick.event(self.mobKey, attributes: ["click": "load"])
//    打卡界面
//            SigninAndStatistic
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    关注
//            AttentionHome
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    消息列表
//            Notification
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    关注的人
//            FollowingUsersSignin
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    channel详情
//            ChannelProfile
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    推荐达人
//            RecommendVIP
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    推荐频道/ 所有频道
//            RecommenChannel
//    from "all"/"recommend"
//            MobClick.event(self.mobKey, attributes: ["click": "load"])
//    任务页
//            MyTasks
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    粉丝列表
//            FansList
//    from others / my
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    跟随列表
//            MenteeList
//    from others / my
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    关注的人列表
//            FollowingList
//    from others / my
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    申请成为达人
//            ApplyDaRen
//    MobClick.event(self.mobKey, attributes: ["click": "load"])
//    发现页面
//            DiscoverPage
//    MobClick.event(self.mobKey, attributes: ["click": "load"])


}
