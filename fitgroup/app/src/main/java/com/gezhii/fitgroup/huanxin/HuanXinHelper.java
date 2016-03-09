package com.gezhii.fitgroup.huanxin;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.dto.PushExperienceDto;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.AboutMeMessage;
import com.gezhii.fitgroup.event.AboutMeMessageEvent;
import com.gezhii.fitgroup.event.ChatMessageEvent;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.event.LoginConflictEvent;
import com.gezhii.fitgroup.event.NetWorkStateChangeEvent;
import com.gezhii.fitgroup.event.PushExperienceEvent;
import com.gezhii.fitgroup.huanxin.messageExt.MessageExt;
import com.gezhii.fitgroup.model.GroupNoticeCacheModel;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserMessageModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.NotificationHelper;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.group.GroupProfileFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/19.
 */
public class HuanXinHelper implements EMConnectionListener, EMEventListener {

    public boolean isGroupChatPageShowing = false;
    public boolean isPrivateChatPageShowing = false;
    public boolean isActivityPause;

    public static final String TAG = HuanXinHelper.class.getSimpleName();

    private Context mContext;
    private LinkedBlockingQueue<EMMessage> mMessageQueue;


    public HuanXinHelper(Context mContext) {
        this.mContext = mContext;
        initChatSdk();
        mMessageQueue = new LinkedBlockingQueue<>();
    }

    public LinkedBlockingQueue<EMMessage> getMessageQueue() {
        return mMessageQueue;
    }

    private void initChatSdk() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase("com.gezhii.fitgroup")) {
//            Log.e(TAG, "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

//        EMChat.getInstance().setAutoLogin(false);
        EMChat.getInstance().init(mContext);
        EMChat.getInstance().setDebugMode(true);

        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
//        // 默认添加好友时，是不需要验证的，改成需要验证
//        options.setAcceptInvitationAlways(true);
//        // 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
//        options.setUseRoster(true);
        // 设置是否需要已读回执
//        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);
        // 设置从db初始化加载时, 每个conversation需要加载msg的个数
        options.setNumberOfMessagesLoaded(Config.loadMessageCount);

        EMChatManager.getInstance().addConnectionListener(this);
    }


    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Application.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    static EMMessage lastMessage;

    private static void setMessageTime(EMMessage message) {
        if (lastMessage == null) {
            message.setAttribute("isShowTimeView", true);
        } else {
            if (message.getMsgTime() - lastMessage.getMsgTime() > 10 * 60 * 1000) {
                message.setAttribute("isShowTimeView", true);
            } else {
                message.setAttribute("isShowTimeView", false);
            }

        }
        lastMessage = message;
    }

    //在线消息:一次收到1条
    //离线消息:上线之后,一次收到多条
    //所有消息,不管什么类型,再调用这个函数之前,消息已经被加入到db里面去了
    //只是在显示的时候,做了过滤,根据消息类型,判断某些消息是否显示
    //要控制某个消息不在conversion中,只能在之后的流程中从db删除(目前没有做这个逻辑)
    @Override
    public void onEvent(EMNotifierEvent emNotifierEvent) {
        Log.i("xianrui", getClass().getSimpleName() + " EMNotifierEvent");
        if (emNotifierEvent.getEvent() == EMNotifierEvent.Event.EventNewMessage || emNotifierEvent.getEvent() == EMNotifierEvent.Event.EventNewCMDMessage) {
            EMMessage message = (EMMessage) emNotifierEvent.getData();
            doMessageEvent(message, true);
        } else if (emNotifierEvent.getEvent() == EMNotifierEvent.Event.EventOfflineMessage) {
            List<EMMessage> messages = (List<EMMessage>) emNotifierEvent.getData();
            for (int i = 0; i < messages.size(); i++) {
                //离线情况下,连续收到1堆消息.只最后1条显示push.
                if (i == messages.size() - 1)
                    doMessageEvent(messages.get(i), true);
                else
                    doMessageEvent(messages.get(i), false);
            }
        }
    }


    private void doMessageEvent(EMMessage message, boolean needNotification) {
        //bug:给message加了一个扩展字段,"判断该消息是否显示时间戳"
        setMessageTime(message);

        //调用环信的接口,更新消息到db里面
        EMChatManager.getInstance().updateMessageBody(message);

        updateUserInfoCache(message);
        String nickName = message.getStringAttribute("sender_nick_name", "");
        int type = message.getIntAttribute("type", -1);

        //单聊(包括了客户端点对点聊天,服务器氧气君的推送)
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            if (type == 30) {
                //程序在后台,通知栏上挂push
                if (isActivityPause) {
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", type);
                    NotificationHelper.createNotification(mContext, "一条经验推送",
                            "一条经验推送", "一条经验推送", intent);
                }
                onPushExperience(message);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 40) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        "同意请求", nickName + "同意了你的入会请求", intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 41) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        "拒绝请求", nickName + "拒绝了你的入会请求", intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 42) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        nickName + "请求加入工会", nickName + " : " + nickName + "请求加入工会", intent);
                PrivateMessageModel.getInstance().setApplicationUnReadCount(PrivateMessageModel.getInstance().getMessageDto().getApplicationUnReadCount() + 1);
                saveMessageLocal(message);
            } else if (type == 43) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        "你已经被踢出公会", "你已经被踢出公会", intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 44) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        "会长将公会禅让给你了", "会长将公会禅让给你了", intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 45) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        "您已经升到3级，无法继续留在新手村了", "会长将公会禅让给你了", intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 46) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", 101);
                NotificationHelper.createNotification(mContext, "一条新的消息",
                        nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                        nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                        intent);
                UserModel.getInstance().tryLoadRemote(true);
            } else if (type == 310) {
                if(needNotification){
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 310);
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            intent);
                }
                saveMessageLocal(message);
            } else if (type == 311) {
                if(needNotification){
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 310);
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            intent);
                }
                saveMessageLocal(message);
            } else if (type == 312) {
                if(needNotification){
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 310);
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            intent);
                }
                saveMessageLocal(message);
            } else if (type == 313) {
                if(needNotification){
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 310);
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            "小Q : " + ((TextMessageBody) message.getBody()).getMessage(),
                            intent);
                }
                saveMessageLocal(message);
            } else {
                mMessageQueue.add(message);
                EventBus.getDefault().post(new ChatMessageEvent());

                //不在私聊页面(不管是前台,还是后台), 通知栏上挂通知
                if (!isPrivateChatPageShowing) {
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 100);
                    intent.putExtra("huanxin_id", message.getFrom());
                    if (message.getType() == EMMessage.Type.TXT) {
                        NotificationHelper.createNotification(mContext, "一条新的消息",
                                nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                                nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                                intent);
                    } else {
                        NotificationHelper.createNotification(mContext, "一条新的消息",
                                nickName + " : [图片]"
                                , nickName + " : [图片]"
                                , intent);
                    }
                }

            }

        }
        //群聊接收消息判断是否挂通知:
        //1.判断免打扰开关是否打开
        //2.所有特殊消息,不管程序是不是在"群聊页面",都会挂通知
        //3.普通群消息,程序不在"群聊页面",挂通知; 在群聊页面,不挂通知
        else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            //"免打扰开关"是否打开
            boolean isShowGroupMessage = DataKeeperHelper.getInstance().getDataKeeper().get(GroupProfileFragment.TAG_IS_SHOW_GROUP_MESSAGE, true);

            //打卡
            if (type == 10) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                if (isShowGroupMessage && needNotification) {
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "打卡消息", nickName + " : " + "打卡", intent);
                }
            }
            //赞打卡
            else if (type == 11) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                String signin_id = message.getStringAttribute("signin_id", "");
                if (!TextUtils.isEmpty(signin_id)) {
                    SigninDto signinDto = SignCacheModel.getInstance().getSign(Integer.parseInt(signin_id));
                    signinDto.getSignin().setLike_count(signinDto.getSignin().getLike_count() + 1);
                    SignCacheModel.getInstance().putSign(signinDto);
                }
            }
            //公告
            else if (type == 12) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                String notice_id = message.getStringAttribute("notice_id", "");
                if (!TextUtils.isEmpty(notice_id)) {
                    GroupNoticeCacheModel.getInstance().setGroupNotice(Integer.parseInt(notice_id));
                }
                if (isShowGroupMessage) {
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            "公告消息", nickName + " : " + "发了一条公告", intent);
                }
            }
            //有新人入会
            else if (type == 13) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                if (isShowGroupMessage) {
                    String newMemberNickName = message.getStringAttribute("new_member_nick_name", "");
                    NotificationHelper.createNotification(mContext, "一条新的消息",
                            newMemberNickName + "加入工会", nickName + " : " + newMemberNickName + "加入工会", intent);
                }
            }

            // TODO: 15/12/30 氧气君推送文章和打卡
            //小Q推送文章
            else if (type == 23) {
                Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                intent.putExtra("type", type);
                if (isShowGroupMessage && needNotification) {
                    NotificationHelper.createNotification(mContext, "一条新消息", "[今日精选]", nickName + " : " +
                            ((TextMessageBody) message.getBody()).getMessage(), intent);
                }
            }
            //普通消息(文字,图片,没有声音)
            else {
                //当前页面是不是"群聊天页面"
                if (!isGroupChatPageShowing) {
                    Intent intent = new Intent(MyApplication.getApplication(), MainActivity.class);
                    intent.putExtra("type", 101);
                    if (message.getType() == EMMessage.Type.TXT) {
                        if (isShowGroupMessage && needNotification) {
                            NotificationHelper.createNotification(mContext, "一条新的消息",
                                    nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                                    nickName + " : " + ((TextMessageBody) message.getBody()).getMessage(),
                                    intent);
                        }

                    } else {
                        if (isShowGroupMessage && needNotification) {
                            NotificationHelper.createNotification(mContext, "一条新的消息",
                                    nickName + " : [图片]"
                                    , nickName + " : [图片]"
                                    , intent);
                        }
                    }

                    //不在群聊界面，本地公会未读消息数加1；在群聊界面不做处理
                    UserMessageModel.getInstance().setUnreadGroupMessageCount(UserMessageModel.getInstance().getUnreadGroupMessageCount() + 1);
                }
            }
            EventBus.getDefault().post(new GroupMessageEvent());
        }
    }

    public void saveMessageLocal(EMMessage message) {
        UserMessageModel.getInstance().setHasNewMessage(true);
        AboutMeMessage aboutMeMessage = new AboutMeMessage();
        aboutMeMessage.setSender_nick_name(message.getStringAttribute("sender_nick_name", ""));
        aboutMeMessage.setSend_user_id(message.getStringAttribute("send_user_id", ""));
        aboutMeMessage.setSender_icon(message.getStringAttribute("sender_icon", ""));
        aboutMeMessage.setSignin_id(message.getIntAttribute("signin_id", -1));
        aboutMeMessage.setTime(TimeHelper.RecordWeekStartFormat.format(new Date(message.getMsgTime())));
        aboutMeMessage.setType(message.getIntAttribute("em_apns_ext", -1));
        aboutMeMessage.setSender_user_vip(message.getIntAttribute("sender_user_vip", 0));
        if (message.getIntAttribute("type", -1) == 42) {
            aboutMeMessage.setContent(message.getStringAttribute("sender_nick_name", "") + "申请入会");
        } else {
            aboutMeMessage.setContent(((TextMessageBody) message.getBody()).getMessage());
        }
        UserMessageModel.getInstance().addMessage(aboutMeMessage);
        EventBus.getDefault().post(new AboutMeMessageEvent());
    }

    private void onPushExperience(EMMessage message) {
        PushExperienceDto pushExperienceDto = new PushExperienceDto();
        String badgeString = message.getStringAttribute("badge", "");
        if (!TextUtils.isEmpty(badgeString)) {
            pushExperienceDto.badge = PushExperienceDto.parserBadgeListJson(badgeString);
        }
        pushExperienceDto.level = message.getIntAttribute("level", 0);
        pushExperienceDto.experience_inr = message.getIntAttribute("experience_inr", 0);
        pushExperienceDto.progress = Double.parseDouble(message.getStringAttribute("progress", "0"));
        pushExperienceDto.level_upgrade = message.getIntAttribute("level_upgrade", 0);
        EventBus.getDefault().postSticky(new PushExperienceEvent(pushExperienceDto));
    }


    private static void updateUserInfoCache(EMMessage message) {
        Log.i("xianrui", HuanXinHelper.class.getSimpleName() + " updateUserInfoCache");
        UserCacheModel.UserCacheInfo userCacheInfo = UserCacheModel.getInstance().getUserInfo(message.getFrom());
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheModel.UserCacheInfo();
        }

        String icon = message.getStringAttribute("sender_icon", "");
        if (!TextUtils.isEmpty(icon)) {
            userCacheInfo.icon = icon;
        }
        String nickName = message.getStringAttribute("sender_nick_name", "");
        if (!TextUtils.isEmpty(nickName)) {
            userCacheInfo.nickName = nickName;
        }
        UserCacheModel.getInstance().setUserInfo(message.getFrom(), userCacheInfo);
    }


    //sdk 群发,单发的接口,都是这个
    private static EMMessage sendTextMessage(MessageExt messageExt, String receipt, EMMessage.ChatType chatType, String textBody, EMCallBack emCallBack) {
        EMConversation conversation = EMChatManager.getInstance().getConversation(receipt);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(chatType);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(textBody);
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(receipt);
        message.setAttribute("type", 0);

        setMessageTime(message);

        messageExt.createExt(message);

        updateUserInfoCache(message);
        //conversation.addMessage(message);

        //发送消息, sdk 内部会自动调用conversation.addMessage,并且会判重
        EMChatManager.getInstance().sendMessage(message, emCallBack);

        return message;
    }

    private static EMMessage sendImageMessage(MessageExt messageExt, String receipt, EMMessage.ChatType chatType, String filePath, EMCallBack emCallBack) {
        EMConversation conversation = EMChatManager.getInstance().getConversation(receipt);
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
        message.setChatType(chatType);

        ImageMessageBody body = new ImageMessageBody(new File(filePath));
        message.addBody(body);
        message.setReceipt(receipt);
        message.setAttribute("type", 1);

        setMessageTime(message);

        messageExt.createExt(message);
        updateUserInfoCache(message);
        //conversation.addMessage(message);

        //发送消息, sdk 内部会自动调用conversation.addMessage,并且会判重
        EMChatManager.getInstance().sendMessage(message, emCallBack);

        return message;
    }


    public static void sendPrivateTextMessage(MessageExt messageExt, String receipt_user_huanxin_id, String textBody, EMCallBack emCallBack) {
        EMMessage emMessage = sendTextMessage(messageExt, receipt_user_huanxin_id, EMMessage.ChatType.Chat, textBody, emCallBack);
        if (messageExt.getType() < 3) {
            PrivateMessageModel.getInstance().addSendMessage(emMessage);
        }
    }

    public static void sendGroupTextMessage(MessageExt messageExt, String group_id, String textBody, EMCallBack emCallBack) {
        sendTextMessage(messageExt, group_id, EMMessage.ChatType.GroupChat, textBody, emCallBack);
    }

    public static void sendPrivateImageMessage(MessageExt messageExt, String receipt_user_huanxin_id, String filePath, EMCallBack emCallBack) {
        EMMessage emMessage = sendImageMessage(messageExt, receipt_user_huanxin_id, EMMessage.ChatType.Chat, filePath, emCallBack);
        if (messageExt.getType() < 3) {
            PrivateMessageModel.getInstance().addSendMessage(emMessage);
        }
    }

    public static void sendGroupImageMessage(MessageExt messageExt, String group_id, String filePath, EMCallBack emCallBack) {
        sendImageMessage(messageExt, group_id, EMMessage.ChatType.GroupChat, filePath, emCallBack);
    }


    @Override
    public void onConnected() {
        Log.i(TAG, "onConnected : connect success");
        EMChat.getInstance().setAppInited();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{
                EMNotifierEvent.Event.EventNewCMDMessage,
                EMNotifierEvent.Event.EventNewMessage,
                EMNotifierEvent.Event.EventOfflineMessage});
        EventBus.getDefault().post(new NetWorkStateChangeEvent());
        if (UserModel.getInstance().isLogin()) {
            EMChatManager.getInstance().updateCurrentUserNick(UserModel.getInstance().getUserNickName());
        }

    }

    @Override
    public void onDisconnected(int i) {
        if (i == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
        } else if (i == EMError.CONNECTION_CONFLICT) {
            EventBus.getDefault().postSticky(new LoginConflictEvent());
        } else {
            EventBus.getDefault().post(new NetWorkStateChangeEvent());
        }
    }

}
