package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.GroupNoticeDto;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.event.ATailEvent;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.event.GroupNoticeEvent;
import com.gezhii.fitgroup.event.NetWorkStateChangeEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.ATailMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.EmojiAndTextMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.EmojiMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.MessageExt;
import com.gezhii.fitgroup.model.GroupNoticeCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupChatListAdapter;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignFragment;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMMSdk;
import com.melink.bqmmsdk.ui.keyboard.Bqmm_Keyboard;
import com.melink.bqmmsdk.widget.Bqmm_Editview;
import com.melink.bqmmsdk.widget.Bqmm_SendButton;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/21.
 */
public class GroupChatFragment extends BaseFragment {

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.group_chat_list_view)
    RecyclerView groupChatListView;

    @InjectView(R.id.send_image_btn)
    ImageView sendImageBtn;

    @InjectView(R.id.root_layout)
    LinearLayout rootLayout;
    @InjectView(R.id.system_notice_text)
    TextView systemNoticeText;
    @InjectView(R.id.system_notice_close_btn)
    ImageView systemNoticeCloseBtn;
    @InjectView(R.id.notice_layout)
    LinearLayout noticeLayout;
    @InjectView(R.id.sign_btn)
    ImageView signBtn;
    @InjectView(R.id.net_work_error_layout)
    LinearLayout netWorkErrorLayout;

    @InjectView(R.id.group_signin_hint_layout)
    LinearLayout groupSigninHintLayout;
    @InjectView(R.id.group_signin_hint_text1)
    TextView groupSigninHintText1;
    @InjectView(R.id.group_signin_hint_text2)
    TextView groupSigninHintText2;

    @InjectView(R.id.group_add_task_hint_layout)
    LinearLayout groupAddTaskHintLayout;


    @InjectView(R.id.send_text_btn)
    Bqmm_SendButton sendTextBtn;
    @InjectView(R.id.group_chat_input)
    Bqmm_Editview groupChatInput;
    @InjectView(R.id.bqmm_keyboard)
    Bqmm_Keyboard bqmm_keyboard;
    @InjectView(R.id.bqmm_check_box)
    CheckBox bqmmCheckBox;

    public boolean isKeyBoardShow;
    List<EMMessage> mEMMesageList;
    GroupChatListAdapter mGroupChatListAdapter;
    LinearLayoutManager mLinearLayoutManager;
    boolean isHasMore = true;
    @InjectView(R.id.signin_layout)
    LinearLayout signinLayout;
    @InjectView(R.id.photo_layout)
    LinearLayout photoLayout;
    @InjectView(R.id.camera_layout)
    LinearLayout cameraLayout;
    @InjectView(R.id.show_type_layout)
    LinearLayout showTypeLayout;
    GroupChatFragment groupChatFragment;
    @InjectView(R.id.group_introduce_myself_layout)
    LinearLayout groupIntroduceMyselfLayout;
    public static final int RETURN_CAMERA = 1;
    public static final int RETURN_PHOTO = 2;
    public static final String TAG_CACHE_CAMERA_FILE_NAME = "tag_cache_camera_file_name";
    String mFileName;
    ImagePickDialog imagePickDialog;

    public HashMap<String, String> mATailMap;//存@的人的昵称和对应的环形id


    public static void start(Activity activity) {
        Log.i("darren", "groupChat_start");
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(GroupChatFragment.class);
    }


    private BQMMSdk bqmmsdk;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("darren", "groupChat_onCreateView");
        final View rootView = inflater.inflate(R.layout.group_chat_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        EventBus.getDefault().register(this);
        groupChatFragment = this;
        MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "load"));
        initTitle();
        mATailMap = new HashMap<>();
        setView();
        setGroupNotice();
        groupChatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (showTypeLayout.isShown()) {
                    showTypeLayout.setVisibility(View.GONE);
                }
                hideBqmmKeyboard();
                hideSoftInput(groupChatInput);
                return false;
            }
        });

        checkNetWork();

        groupChatInput.requestFocus();

        bqmmsdk = BQMMSdk.getInstance();
        // 初始化表情MM键盘，需要传入关联的EditView,SendBtn
        bqmmsdk.setEditView(groupChatInput);
        bqmmsdk.setKeyboard(bqmm_keyboard);
        bqmmsdk.setSendButton(sendTextBtn);
        bqmmsdk.load();

        /**
         * 设置键盘的默认高度
         */
        //如果打开过一次系统键盘,把系统键盘的高度记录下来.然后下次再进入的时候,把表情键盘的高度设成系统键盘的高度
        int defaultHeight = DensityUtils.dip2px(getActivity(), 250);
        int height = getActivity().getPreferences(getActivity().MODE_PRIVATE).getInt(LAST_KEYBOARD_HEIGHT, defaultHeight);
        ViewGroup.LayoutParams params = bqmm_keyboard.getLayoutParams();
        if (params != null) {
            params.height = height;
            bqmm_keyboard.setLayoutParams(params);
        }
        /**
         * 表情键盘切换监听
         */
        groupChatInput.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                Log.i("ycl", "onPreDraw called");

                if (bqmm_keyboard == null) {
                    return true;
                }
                // Keyboard -> BQMM
                if (mPendingShowPlaceHolder) {
                    // 在设置mPendingShowPlaceHolder时已经调用了隐藏Keyboard的方法，直到Keyboard隐藏前都取消重绘
                    if (isKeyboardVisible()) {
                        ViewGroup.LayoutParams params = bqmm_keyboard.getLayoutParams();
                        int distance = getDistanceFromInputToBottom();
                        // 调整PlaceHolder高度
                        if (distance > DISTANCE_SLOP && distance != params.height) {
                            params.height = distance;
                            bqmm_keyboard.setLayoutParams(params);
                            getActivity().getPreferences(getActivity().MODE_PRIVATE).edit().putInt(LAST_KEYBOARD_HEIGHT, distance).apply();
                        }
                        return false;
                    } else {
                        // mRealListView.setSelection(mRealListView.getAdapter().getCount() -1);
                        showBqmmKeyboard();
                        mPendingShowPlaceHolder = false;
                        return false;
                    }
                } else {//BQMM -> Keyboard
                    if (isBqmmKeyboardVisible() && isKeyboardVisible()) {
                        //  mRealListView.setSelection(mRealListView.getAdapter().getCount() -1);
                        hideBqmmKeyboard();
                        return false;
                    }
                }
                return true;
            }
        });

        //点表情按钮,来回在系统键盘/表情键盘切换
        bqmmCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeLayout.setVisibility(View.GONE);

                if (isBqmmKeyboardVisible()) { // PlaceHolder -> Keyboard
                    showSoftInput(groupChatInput);
                } else if (isKeyboardVisible()) { // Keyboard -> PlaceHolder
                    mPendingShowPlaceHolder = true;
                    hideSoftInput(groupChatInput);
                } else { // Just show PlaceHolder
                    //mRealListView.setSelection(mRealListView.getAdapter().getCount() -1);
                    showBqmmKeyboard();
                }
            }
        });

        //点输入框,系统键盘自动会弹出来.同时隐藏"添加图片"弹出来的layout, checkbox设置为unchecked状态
        groupChatInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showTypeLayout.setVisibility(View.GONE);
                bqmmCheckBox.setChecked(false);
                return false;
            }
        });

        //点添加图片按钮
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showTypeLayout.isShown()) {
                    showTypeLayout.setVisibility(View.GONE);
                } else {
                    showTypeLayout.setVisibility(View.VISIBLE);

                    //点击添加图片按钮,(1)表情键盘,系统键盘都得隐藏 (2)输入框失去焦点 (3)表情按钮的状态,切换到uncheck状态
                    groupChatInput.clearFocus();
                    if (isBqmmKeyboardVisible()) {
                        hideBqmmKeyboard();
                        bqmmCheckBox.setChecked(false);
                    } else if (isKeyboardVisible()) {
                        hideSoftInput(groupChatInput);
                    }
                }
            }
        });

        //一个是"发送"按钮的监听:小表情符号 + 文字
        //一个是直接点击大的Gif动画发送:大图,纯Gif动画
        bqmmsdk.setBqmmSendMsgListener(new Bqmm_SendButton.IBqmmSendMessageListener() {

            @Override
            public void onSendEmoji(List<Object> list) {
                MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "text and emoji"));
                sendTextBtnClicked();
            }

            @Override
            public void onSendFace(Emoji emoji) {
                MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "gif emoji"));
                Log.i("ycl", "face clicked");
                faceClicked(emoji);
            }
        });


        return rootView;
    }

    public void onEvent(ATailEvent aTailEvent) {
        mATailMap.putAll(aTailEvent.getParams());
        Log.i("darren", mATailMap.toString());
    }

    private void setView() {
//        //监控输入框中字符变化
        groupChatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {
            }

            @Override
            public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
            }

            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    int index = groupChatInput.getSelectionStart();
                    if (index > 0) {
                        char x = s.charAt(index - 1);
                        Log.i("x", x);
                        Log.i("x", "@".equals(String.valueOf(x)));
                        if ("@".equals(String.valueOf(x))) {
                            GroupChatATailMemberListFragment.start(getActivity(), groupChatInput);
                        }
                    }

                }

                //根据输入框中是否有内容,决定右面是显示"发送",还是"加号"
                if (groupChatInput.getText().length() == 0) {
                    sendTextBtn.setVisibility(View.GONE);
                    sendImageBtn.setVisibility(View.VISIBLE);
                    rootLayout.requestLayout();
                } else {
                    sendTextBtn.setVisibility(View.VISIBLE);
                    sendImageBtn.setVisibility(View.GONE);
                    rootLayout.requestLayout();
                }
            }
        });
//
        //监控键盘输入字符,比如删除键
        groupChatInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Editable editable = groupChatInput.getText();
                    String s = editable.toString().substring(0, groupChatInput.getSelectionStart());
                    if (s != null && s.length() > 0) {
                        int index = groupChatInput.getSelectionStart();
                        Log.i("index", index);
                        char x = s.charAt(index - 1);
                        if (s.contains("@")) {
                            int lastAtIndex = s.lastIndexOf("@");
                            Log.i("last" + String.valueOf(x));
                            Log.i("空islast" + " ".equals(String.valueOf(x)));
                            if (" ".equals(String.valueOf(x))) {
                                editable.delete(lastAtIndex, index);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });


        signinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignFragment.start(getActivity());
            }
        });
        photoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePickDialog == null) {
                    createPickImageDialog();
                }
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                groupChatFragment.startActivityForResult(intent, RETURN_PHOTO);
            }
        });
        cameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePickDialog == null) {
                    createPickImageDialog();
                }
                mFileName = ImagePickDialog.getFileName();
                DataKeeperHelper.getInstance().getDataKeeper().put(TAG_CACHE_CAMERA_FILE_NAME, mFileName);
                File mImage = new File(FileUtils.getImagePath(getActivity(), mFileName));
                if (!mImage.exists()) {
                    File vDirPath = mImage.getParentFile();
                    if (vDirPath == null) {
                        return;
                    } else {
                        vDirPath.mkdirs();
                    }
                }
                Uri uri = Uri.fromFile(mImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                groupChatFragment.startActivityForResult(intent, RETURN_CAMERA);
            }
        });
        refreshChatList();
    }

    private void faceClicked(Emoji emoji) {
        HuanXinHelper.sendGroupTextMessage(new EmojiMessageExt(UserModel.getInstance().getUserNickName(),
                        UserModel.getInstance().getUserIcon(),
                        Integer.toString(UserModel.getInstance().getUserId()), emoji.getEmoCode()),
                UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                "[表情]", new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("ycl", "sendFace onSuccess = ");
                                refreshChatList();
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.i("ycl", "sendFace onError = ");
                                refreshChatList();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });

        //消息被成功发送之前,刷一遍列表; 回调回来之后,再刷一次列表
        refreshChatList();
    }

    private void sendTextBtnClicked() {
        UserModel.getInstance().setHasIntroduceMyself();
        if (groupIntroduceMyselfLayout.getVisibility() == View.VISIBLE) {
            groupIntroduceMyselfLayout.setVisibility(View.GONE);
        }
        String content = groupChatInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("内容不能为空");
        } else {
            //获得所有@的人,挨个发私信
            List<String> huanXinIdList = (ArrayList) getATailMemberHuanXinId();
            for (int i = 0; i < huanXinIdList.size(); i++) {
                HuanXinHelper.sendPrivateTextMessage(new ATailMessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())),
                        huanXinIdList.get(i), UserModel.getInstance().getUserNickName() + "在公会里@了你",
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
            }
            groupChatInput.setText("");


            //含表情的话,用特殊消息发送; 不含表情,纯文本,用普通消息发送
            if (containEmoji(content)) {
                String replacedContent = replaceEmoji(content);

                HuanXinHelper.sendGroupTextMessage(new EmojiAndTextMessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), content),
                        UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), replacedContent,
                        new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("ycl", "sendMessage onSuccess = ");
                                        refreshChatList();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.i("ycl", "sendMessage onError = ");
                                        refreshChatList();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {
                                Log.i("ycl", "sendMessage onProgress = " + i + "," + s);

                            }
                        });
            } else {
                HuanXinHelper.sendGroupTextMessage(new MessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())),
                        UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), content,
                        new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.i("ycl", "sendMessage onSuccess = ");
                                        refreshChatList();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Log.i("ycl", "sendMessage onError = ");
                                        refreshChatList();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {
                                Log.i("ycl", "sendMessage onProgress = " + i + "," + s);

                            }
                        });
            }
            //消息被成功发送之前,刷一遍列表; 回调回来之后,再刷一次列表
            refreshChatList();

        }
    }

    private boolean containEmoji(String content) {
        Pattern pattern = Pattern.compile("\\[[0-9a-zA-Z]+\\]");
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    private String replaceEmoji(String content) {
        return content.replaceAll("\\[[0-9a-zA-Z]+\\]", "[表情]");
    }

    public List<String> getATailMemberHuanXinId() {
        ArrayList<String> arrayNameList = new ArrayList<>();
        ArrayList<String> arrayHuanXinIdList = new ArrayList<>();
        String contants = groupChatInput.getText().toString();
        for (int i = 0; i < contants.length(); i++) {
            if (String.valueOf(contants.charAt(i)).equals("@")) {
                StringBuffer name = new StringBuffer();
                for (int j = i; j < contants.length(); j++) {
                    if ((!String.valueOf(contants.charAt(j)).equals("@") && !String.valueOf(contants.charAt(j)).equals(" "))) {
                        name.append(contants.charAt(j));
                    }
                    if (String.valueOf(contants.charAt(j)).equals(" ")) {
                        if (!arrayNameList.contains(String.valueOf(name))) {
                            arrayNameList.add(String.valueOf(name));
                        }
                        break;
                    }
                }
            }
        }
        Set set = mATailMap.entrySet();
        for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            for (int j = 0; j < arrayNameList.size(); j++) {
                if (arrayNameList.get(j).equals(key)) {
                    if (!arrayHuanXinIdList.contains(String.valueOf(value))) {
                        arrayHuanXinIdList.add(value);
                    }
                }
            }
        }

        Log.i("darren_list", arrayNameList.toString());
        Log.i("darren_id_list", arrayHuanXinIdList.toString());
        return arrayHuanXinIdList;
    }

    private void checkNetWork() {
        if (NetUtils.hasNetwork(getActivity())) {
            netWorkErrorLayout.setVisibility(View.GONE);
        } else {
            netWorkErrorLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(NetWorkStateChangeEvent netWorkStateChangeEvent) {
        checkNetWork();
    }

    @OnClick(R.id.sign_btn)
    public void onSignBtnClick(View v) {
        UserModel.getInstance().setFirstSigninFlag();

        MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "打卡"));
        SignFragment.start(getActivity());
        KeyBoardHelper.hideKeyBoard(getActivity(), groupChatInput);
    }

    @Override
    public void onResume() {
        super.onResume();
        groupChatInput.clearFocus();
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        //判断是否显示自我介绍向导
        if (UserModel.getInstance().isNeedIntroduceMyself()) {
            groupIntroduceMyselfLayout.setVisibility(View.VISIBLE);
            groupIntroduceMyselfLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSoftInput(groupChatInput);
                }
            });
        } else {
            groupIntroduceMyselfLayout.setVisibility(View.GONE);
        }

        //判断是否显示打卡向导
        if (UserModel.getInstance().isFirstSigninFlag()) {
            groupSigninHintText1.setText("新人任务二：点击\"");
            groupSigninHintText2.setText("\"打个卡");
        } else {
            groupSigninHintLayout.setVisibility(View.GONE);
        }

        //判断是否显示添加任务向导(只有会长有这功能)
        if (UserModel.getInstance().isGroupLeader() && (UserModel.getInstance().getMyGroup().group_tasks == null || UserModel.getInstance().getMyGroup().group_tasks.size() == 0)) {
            groupAddTaskHintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddTaskFragment.start(getActivity());
                }
            });
        } else {
            groupAddTaskHintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        Log.i("darren", "groupChat  onPause");
        super.onPause();

    }


    private void setGroupNotice() {
        GroupNoticeDto groupNoticeDto = GroupNoticeCacheModel.getInstance().getGroupNoticeDto();
        if (groupNoticeDto == null) {
            noticeLayout.setVisibility(View.GONE);
        } else {
            noticeLayout.setVisibility(View.VISIBLE);
            systemNoticeText.setText("公告－" + groupNoticeDto.getGroup_notice().description);
            systemNoticeCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "关闭公告"));
                    noticeLayout.setVisibility(View.GONE);
                    GroupNoticeCacheModel.getInstance().clear();
                }
            });
        }
    }


    private void initTitle() {
        titleText.setText(UserModel.getInstance().getMyGroup().getGroup_name());
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBqmmKeyboardVisible() || isKeyboardVisible()) {
                    closebroad();
                }

                finish();
            }
        });
        rightImg.setImageResource(R.mipmap.group_info);

        rightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_chat", UmengEvents.getEventMap("click", "公会介绍"));
                ((MainActivity) getActivity()).showNext(GroupProfileFragment.class, null);
                KeyBoardHelper.hideKeyBoard(getActivity(), groupChatInput);
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        groupChatListView.setLayoutManager(mLinearLayoutManager);
        //controlKeyboardLayout(rootLayout);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RETURN_CAMERA:
                Log.i("darren", "camera");
                imagePickDialog.onCameraRequest(resultCode);
                break;
            case RETURN_PHOTO:
                Log.i("darren", "photo");
                imagePickDialog.onPhotoRequest(resultCode, data);
                break;
        }
        if (imagePickDialog == null) {
            createPickImageDialog();
        }
    }


    private void createPickImageDialog() {
        imagePickDialog = new ImagePickDialog(GroupChatFragment.this);
        imagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                // EventBus.getDefault().post(new ShowLoadingEvent());
                HuanXinHelper.sendGroupImageMessage(new MessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())),
                        UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), resInformationDto.getFilePath(), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshChatList();
                                        //EventBus.getDefault().post(new CloseLoadingEvent());
                                        imagePickDialog = null;
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshChatList();
                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                        imagePickDialog = null;
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });
            }
        });

        refreshChatList();
    }


    public void onEventMainThread(GroupMessageEvent groupMessageEvent) {
        refreshChatList();
    }

    public void onEventMainThread(GroupNoticeEvent groupNoticeEvent) {
        setGroupNotice();
    }

    private void refreshChatList() {
        refreshChatList(-1);
    }


    private void refreshChatList(int scrollTo) {
        final EMConversation conversation = EMChatManager.getInstance().getConversation(UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id());
        mEMMesageList = conversation.getAllMessages();
        Log.i("xianrui", "message list " + mEMMesageList.size());
        if (mGroupChatListAdapter == null) {
            mGroupChatListAdapter = new GroupChatListAdapter(getActivity(), mEMMesageList, UserModel.getInstance().getUserHuanXinName(), groupChatInput);
            groupChatListView.setAdapter(mGroupChatListAdapter);
            groupChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (isHasMore) {
                        if (firstVisibleItemPosition < 4 && dy <= 0) {
                            List<EMMessage> messageList = conversation.loadMoreGroupMsgFromDB(mEMMesageList.get(0).getMsgId(), Config.loadMessageCount);
                            if (messageList.size() > 0) {
                                isHasMore = true;
                            } else {
                                isHasMore = false;
                            }
                            Log.i("xianrui", "message list size" + messageList.size());
                            refreshChatList(messageList.size() + 4);
                        }
                    }
                }
            });
        } else {
            mGroupChatListAdapter.notifyDataSetChanged();
            mGroupChatListAdapter.setEmMessageList(mEMMesageList);
        }
        if (scrollTo == -1) {
            groupChatListView.scrollToPosition(mGroupChatListAdapter.getItemCount() - 1);
        } else {
            groupChatListView.scrollToPosition(scrollTo);
        }
    }

    @Override
    public void onDestroyView() {
        Log.i("darren", "groupchat   onDestoryView");
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), groupChatInput);
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bqmmsdk.destory();
    }


    /*
         * 键盘切换相关
         */
    private Rect tmp = new Rect();
    // private int mScreenHeight;
    // private View mMainContainer;
    private final int DISTANCE_SLOP = 180;
    private final String LAST_KEYBOARD_HEIGHT = "last_keyboard_height";
    private boolean mPendingShowPlaceHolder;

    /**************************
     * 表情键盘软键盘切换相关 start
     **************************************/
    private void closebroad() {
        if (isBqmmKeyboardVisible()) {
            hideBqmmKeyboard();
        } else if (isKeyboardVisible()) {
            hideSoftInput(groupChatInput);
        }
    }

    //系统键盘或者表情键盘打开
    private boolean isKeyboardVisible() {
        return (getDistanceFromInputToBottom() > DISTANCE_SLOP && !isBqmmKeyboardVisible())
                || (getDistanceFromInputToBottom() > (bqmm_keyboard.getHeight() + DISTANCE_SLOP) && isBqmmKeyboardVisible());
    }

    private void showSoftInput(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus && mScreenHeight <= 0) {
//            mMainContainer.getGlobalVisibleRect(tmp);
//            mScreenHeight = tmp.bottom;
//        }
//    }

    private void showBqmmKeyboard() {
        if (bqmm_keyboard.getVisibility() != View.VISIBLE) {
            bqmm_keyboard.setVisibility(View.VISIBLE);
        }
    }

    private void hideBqmmKeyboard() {
        if (bqmm_keyboard.getVisibility() != View.GONE) {
            bqmm_keyboard.setVisibility(View.GONE);
        }
    }

    private boolean isBqmmKeyboardVisible() {
        return bqmm_keyboard.getVisibility() == View.VISIBLE;
    }

    /**
     * 输入框的下边距离屏幕的距离(也就是系统键盘的高度)
     */
    private int getDistanceFromInputToBottom() {
        return Screen.getScreenHeight() - Screen.getStatusBarHeight() - getInputBottom();
    }


    /**
     * 输入框下边的位置
     */
    private int getInputBottom() {
        groupChatInput.getGlobalVisibleRect(tmp);
        return tmp.bottom;
    }

    /**************************表情键盘软键盘切换相关 end **************************************/
}
