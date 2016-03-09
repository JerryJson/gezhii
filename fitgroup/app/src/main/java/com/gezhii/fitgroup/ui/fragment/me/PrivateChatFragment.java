package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.event.NetWorkStateChangeEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.EmojiAndTextMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.EmojiMessageExt;
import com.gezhii.fitgroup.huanxin.messageExt.MessageExt;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.PrivateChatListAdapter;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMMSdk;
import com.melink.bqmmsdk.ui.keyboard.Bqmm_Keyboard;
import com.melink.bqmmsdk.widget.Bqmm_Editview;
import com.melink.bqmmsdk.widget.Bqmm_SendButton;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/10/21.
 */
public class PrivateChatFragment extends BaseFragment {

    public static final String TAG_HUANXIN_ID = "tag_huanxin_id";
    public static final String TAG_USER_NAME = "tag_user_name";
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.private_chat_list_view)
    RecyclerView privateChatListView;
    //    @InjectView(R.id.private_chat_input)
//    EditText privateChatInput;
//    @InjectView(R.id.send_text_btn)
//    TextView sendTextBtn;
    @InjectView(R.id.send_image_btn)
    ImageView sendImageBtn;
    @InjectView(R.id.root_layout)
    LinearLayout rootLayout;
    @InjectView(R.id.net_work_error_layout)
    LinearLayout netWorkErrorLayout;

    @InjectView(R.id.send_text_btn)
    Bqmm_SendButton sendTextBtn;
    @InjectView(R.id.private_chat_input)
    Bqmm_Editview privateChatInput;
    @InjectView(R.id.bqmm_keyboard)
    Bqmm_Keyboard bqmm_keyboard;
    @InjectView(R.id.bqmm_check_box)
    CheckBox bqmmCheckBox;

    public boolean isKeyBoardShow;
    List<EMMessage> mEMMesageList;
    PrivateChatListAdapter mPrivateChatListAdapter;
    ImagePickDialog mImagePickDialog;
    String huanxin_id;
    String user_name;
    LinearLayoutManager mLinearLayoutManager;
    boolean isHasMore = true;
    PrivateMessageModel.onMessageChangListener mOnMessageChangeListener;
    private BQMMSdk bqmmsdk;

    public static void start(Activity activity, String to_huanxin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_HUANXIN_ID, to_huanxin_id);
        params.put(TAG_USER_NAME, "");
        mainActivity.showNext(PrivateChatFragment.class, params);
    }

    public static void start(Activity activity, String to_huanxin_id, String nick_name) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_HUANXIN_ID, to_huanxin_id);
        params.put(TAG_USER_NAME, nick_name);
        mainActivity.showNext(PrivateChatFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("xianrui", this + " onCreateView");
        View rootView = inflater.inflate(R.layout.private_chat_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "private_chat", UmengEvents.getEventMap("click", "load"));
        huanxin_id = (String) getNewInstanceParams().get(TAG_HUANXIN_ID);
        user_name = (String) getNewInstanceParams().get(TAG_USER_NAME);
        rootView.setOnTouchListener(this);
        initTitle();
        setView();

        privateChatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput(privateChatInput);
                hideBqmmKeyboard();
                return false;
            }
        });
        mOnMessageChangeListener = new PrivateMessageModel.onMessageChangListener() {
            @Override
            public void onMessageChange() {
                refreshChatList();
            }
        };
        PrivateMessageModel.getInstance().addMessageChangeListener(mOnMessageChangeListener);
        checkNetWork();

        privateChatInput.requestFocus();

        bqmmsdk = BQMMSdk.getInstance();
        // 初始化表情MM键盘，需要传入关联的EditView,SendBtn
        bqmmsdk.setEditView(privateChatInput);
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
            Log.i("darren", height);
            params.height = height;
            bqmm_keyboard.setLayoutParams(params);
        }
        /**
         * 表情键盘切换监听
         */
        privateChatInput.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
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
                if (isBqmmKeyboardVisible()) { // PlaceHolder -> Keyboard
                    showSoftInput(privateChatInput);
                } else if (isKeyboardVisible()) { // Keyboard -> PlaceHolder
                    mPendingShowPlaceHolder = true;
                    hideSoftInput(privateChatInput);
                } else { // Just show PlaceHolder
                    //mRealListView.setSelection(mRealListView.getAdapter().getCount() -1);
                    showBqmmKeyboard();
                }
            }
        });

        //点输入框,系统键盘自动会弹出来.同时隐藏"添加图片"弹出来的layout, checkbox设置为unchecked状态
        privateChatInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bqmmCheckBox.setChecked(false);
                return false;
            }
        });
        //一个是"发送"按钮的监听:小表情符号 + 文字
        //一个是直接点击大的Gif动画发送:大图,纯Gif动画
        bqmmsdk.setBqmmSendMsgListener(new Bqmm_SendButton.IBqmmSendMessageListener() {

            @Override
            public void onSendEmoji(List<Object> list) {
                sendTextBtnClicked();
            }

            @Override
            public void onSendFace(Emoji emoji) {
                Log.i("ycl", "face clicked");
                faceClicked(emoji);
            }
        });

        return rootView;
    }

    private void faceClicked(Emoji emoji) {
        HuanXinHelper.sendPrivateTextMessage(new EmojiMessageExt(UserModel.getInstance().getUserNickName(),
                        UserModel.getInstance().getUserIcon(),
                        Integer.toString(UserModel.getInstance().getUserId()), emoji.getEmoCode()),
                huanxin_id,
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

    private void checkNetWork() {
        if (NetUtils.hasNetwork(getActivity())) {
            netWorkErrorLayout.setVisibility(View.GONE);
        } else {
            netWorkErrorLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onEvent(NetWorkStateChangeEvent netWorkStateChangeEvent) {
        checkNetWork();
    }

    @Override
    public void onResume() {
        super.onResume();
        PrivateMessageModel.getInstance().cleanRedPoint(huanxin_id);
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initTitle() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        privateChatInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (sendTextBtn != null && sendImageBtn != null) {
//                    if (hasFocus) {
//                        sendTextBtn.setVisibility(View.VISIBLE);
//                        sendImageBtn.setVisibility(View.GONE);
//                    } else {
//                        sendTextBtn.setVisibility(View.GONE);
//                        sendImageBtn.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//
//        });
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        privateChatListView.setLayoutManager(mLinearLayoutManager);
        controlKeyboardLayout(rootLayout);
        if (null == UserCacheModel.getInstance().getUserInfo(huanxin_id))
            titleText.setText(user_name);
        else
            titleText.setText(UserCacheModel.getInstance().getUserInfo(huanxin_id).nickName);
    }

    private void sendTextBtnClicked() {
        String content = privateChatInput.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("内容不能为空");
        } else {
            privateChatInput.setText("");
            //含表情的话,用特殊消息发送; 不含表情,纯文本,用普通消息发送
            if (containEmoji(content)) {
                String replacedContent = replaceEmoji(content);

                HuanXinHelper.sendPrivateTextMessage(new EmojiAndTextMessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), content),
                        huanxin_id, replacedContent,
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
                HuanXinHelper.sendPrivateTextMessage(new MessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())),
                        huanxin_id, content,
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

    private void setView() {
        privateChatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence var1, int var2, int var3, int var4) {
            }

            @Override
            public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
            }

            public void afterTextChanged(Editable s) {

                //根据输入框中是否有内容,决定右面是显示"发送",还是"加号"
                if (privateChatInput.getText().length() == 0) {
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

        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImagePickDialog == null) {
                    createPickImageDialog();
                }
                mImagePickDialog.show();
            }
        });
        refreshChatList();
    }


    private void createPickImageDialog() {
        mImagePickDialog = new ImagePickDialog(this);
        mImagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                HuanXinHelper.sendPrivateImageMessage(new MessageExt(UserModel.getInstance().getUserNickName(), UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())),
                        huanxin_id, resInformationDto.getFilePath(), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        groupChatInput.setText("");
                                        refreshChatList();
                                    }
                                });
                            }

                            @Override
                            public void onError(int i, String s) {

                            }

                            @Override
                            public void onProgress(int i, String s) {

                            }
                        });


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImagePickDialog == null) {
            createPickImageDialog();
        }
        mImagePickDialog.onActivityResult(requestCode, resultCode, data);
    }


    private void refreshChatList() {
        refreshChatList(-1);
    }


    private void refreshChatList(int scrollTo) {
        PrivateMessageModel.getInstance().cleanRedPoint(huanxin_id);
        final EMConversation conversation = EMChatManager.getInstance().getConversation(huanxin_id);
        mEMMesageList = conversation.getAllMessages();
        if (mPrivateChatListAdapter == null) {
            mPrivateChatListAdapter = new PrivateChatListAdapter(getActivity(), mEMMesageList, UserModel.getInstance().getUserHuanXinName());
            privateChatListView.setAdapter(mPrivateChatListAdapter);
            privateChatListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            List<EMMessage> messageList = conversation.loadMoreMsgFromDB(mEMMesageList.get(0).getMsgId(), Config.loadMessageCount);
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
            mPrivateChatListAdapter.setEmMessageList(mEMMesageList);
            mPrivateChatListAdapter.notifyDataSetChanged();
        }
        if (scrollTo == -1) {
            privateChatListView.scrollToPosition(mPrivateChatListAdapter.getItemCount() - 1);
        } else {
            privateChatListView.scrollToPosition(scrollTo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        KeyBoardHelper.hideKeyBoard(getActivity(), privateChatInput);
        PrivateMessageModel.getInstance().removeMessageChangeLister(mOnMessageChangeListener);
        ButterKnife.reset(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bqmmsdk.destory();
    }

    private void controlKeyboardLayout(final View root) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                //获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;

                //若不可视区域高度大于100，则键盘显示
                int scrollHeight;
                if (rootInvisibleHeight - Screen.getStatusBarHeight() > 100) {
                    isKeyBoardShow = true;
                    if (privateChatInput != null && !privateChatInput.hasFocus())
                        privateChatInput.requestFocus();
                } else {
                    isKeyBoardShow = false;
                    if (privateChatInput != null && privateChatInput.hasFocus())
                        privateChatInput.clearFocus();
                }
                Log.i("xianrui", rootInvisibleHeight + " controlKeyboardLayout");
                Log.i("xianrui", "isKeyBoardShow " + isKeyBoardShow);
            }
        });
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
            hideSoftInput(privateChatInput);
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
        privateChatInput.getGlobalVisibleRect(tmp);
        return tmp.bottom;
    }

    /**************************表情键盘软键盘切换相关 end **************************************/
}
