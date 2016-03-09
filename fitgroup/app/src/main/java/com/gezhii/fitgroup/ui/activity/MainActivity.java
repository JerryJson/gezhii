package com.gezhii.fitgroup.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.event.JumpPageEvent;
import com.gezhii.fitgroup.event.JumpToChooseTagsEvent;
import com.gezhii.fitgroup.event.JumpToGroupApplicationEvent;
import com.gezhii.fitgroup.event.LoginConflictEvent;
import com.gezhii.fitgroup.event.PushExperienceEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.VersionModel;
import com.gezhii.fitgroup.step.PedometerSettings;
import com.gezhii.fitgroup.step.StepService;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.PushBadgeDialog;
import com.gezhii.fitgroup.ui.dialog.PushExperienceDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.Discovery13Fragment;
import com.gezhii.fitgroup.ui.fragment.follow.AboutMeMessageFragment;
import com.gezhii.fitgroup.ui.fragment.follow.FollowFragment;
import com.gezhii.fitgroup.ui.fragment.group.ApplicationGroupFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.gezhii.fitgroup.ui.fragment.group.leader.GroupMemberApplicationListFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyProfileFragment;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.fragment.plan.ChooseSportInterestFragment;
import com.gezhii.fitgroup.ui.fragment.plan.FollowMentorPlanFragment;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xianrui on 15/8/18.
 */
public class MainActivity extends BaseActivity {

    private static final String STATE_SELECTED_POSITION = "state_selected_position";
    public static final int GROUP_SELECT = 1;
    public static final int FIND_SELECT = 2;
    public static final int ME_SELECT = 3;
    public static final int FOLLOW_SELECT = 4;
    public static final int PLAN_SELECT = 5;


    @InjectView(R.id.bottom_find_img)
    ImageView bottomFindImg;
    @InjectView(R.id.bottom_find_text)
    TextView bottomFindText;
    @InjectView(R.id.bottom_find_btn)
    RelativeLayout bottomFindBtn;
    @InjectView(R.id.bottom_me_img)
    ImageView bottomMeImg;
    @InjectView(R.id.bottom_me_text)
    TextView bottomMeText;
    @InjectView(R.id.bottom_me_btn)
    RelativeLayout bottomMeBtn;
    @InjectView(R.id.bottom_bar_layout)
    FrameLayout bottomBarLayout;
    @InjectView(R.id.main_content_layout)
    FrameLayout mainContentLayout;
    @InjectView(R.id.content_layout)
    FrameLayout contentLayout;

    @InjectView(R.id.bottom_follow_btn)
    RelativeLayout bottomFollowBtn;
    @InjectView(R.id.bottom_follow_img)
    ImageView bottomFollowImg;
    @InjectView(R.id.bottom_follow_text)
    TextView bottomFollowText;


    BaseFragment currentTabFragment;
    BaseFragment lastTabFragment;
    BaseFragment currentPageFragment;

    PushExperienceDialog mPushExperienceDialog;
    PushBadgeDialog mPushBadgeDialog;
    @InjectView(R.id.cache_img)
    ImageView cacheImg;
    @InjectView(R.id.bottom_plan_img)
    ImageView bottomPlanImg;
    @InjectView(R.id.bottom_plan_text)
    TextView bottomPlanText;
    @InjectView(R.id.bottom_plan_btn)
    RelativeLayout bottomPlanBtn;

    private int currentSelectedPosition = -1;


    private StepService mService;
    private int mStepValue;
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    /**
     * True, when service is running.
     */
    private boolean mIsRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.main_activity);
        ButterKnife.inject(this);

        Bundle bundle = this.getIntent().getExtras();


        if (bundle == null) {
            selectNextItem(FOLLOW_SELECT);
        } else {
            selectNextItem(bundle.getInt(STATE_SELECTED_POSITION));
        }

//        if (savedInstanceState == null) {
//            selectNextItem(FOLLOW_SELECT);
//        } else {
//            selectNextItem(savedInstanceState.getInt(STATE_SELECTED_POSITION));
//        }

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    Log.i("xianrui", "backStackEntryCount " + backStackEntryCount);
                    if (currentTabFragment != null && backStackEntryCount == 0) {
                        currentTabFragment.onResume();
                        currentPageFragment = null;
                    } else if (backStackEntryCount != 0) {
                        BaseFragment baseFragment = (BaseFragment) manager.findFragmentById(R.id.content_layout);
                        if (baseFragment != null) {
                            baseFragment.onFragmentResume();
                            currentPageFragment = baseFragment;
                        }
                    }
                    if (currentPageFragment != null && currentPageFragment instanceof GroupChatFragment) {
                        MyApplication.getApplication().getHuanXinHelper().isGroupChatPageShowing = true;
                    } else {
                        MyApplication.getApplication().getHuanXinHelper().isGroupChatPageShowing = false;
                    }
                    if (currentPageFragment != null && currentPageFragment instanceof PrivateChatFragment) {
                        MyApplication.getApplication().getHuanXinHelper().isPrivateChatPageShowing = true;
                    } else {
                        MyApplication.getApplication().getHuanXinHelper().isPrivateChatPageShowing = false;
                    }
                }
            }
        });
        Log.i("xianrui", "MainActivity onCreate");
        cacheImage();
        VersionModel.getInstance().getVersion(this);


        //为Bugly crashreport加上user id
        if (UserModel.getInstance().getUserDto() != null) {
            CrashReport.setUserId(Integer.toString(UserModel.getInstance().getUserId()));
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder) service).getService();

            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private void resetValues(boolean updateDisplay) {
        if (mService != null && mIsRunning) {
            mService.resetValues();
        } else {
            SharedPreferences state = this.getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }


    private void startStepService() {
        if (!mIsRunning) {
            android.util.Log.i("darren-------", "startStepService");
            android.util.Log.i("darren", "[SERVICE] Start");
            mIsRunning = true;
            this.startService(new Intent(this,
                    StepService.class));
        }
    }

    private void bindStepService() {
        android.util.Log.i("darren-------", "bindStepService");
        android.util.Log.i("darren", "[SERVICE] Bind");
        this.bindService(new Intent(this,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        android.util.Log.i("darren-------", "unbindStepService");
        android.util.Log.i("darren", "[SERVICE] Unbind");
        this.unbindService(mConnection);
    }

    private void stopStepService() {
        android.util.Log.i("darren", "[SERVICE] Stop");
        if (mService != null) {
            android.util.Log.i("darren-------", "stopStepService");
            android.util.Log.i("darren", "[SERVICE] stopService");
            this.stopService(new Intent(this,
                    StepService.class));
        }
        mIsRunning = false;
    }

    private void cacheImage() {
        for (String url : QiniuHelper.imgs) {
            QiniuHelper.bindImage(url, cacheImg);
        }
    }

    public void onResume() {
        super.onResume();
        Log.i("xianrui", "MainActivity onResume");
        MyApplication.getApplication().getHuanXinHelper().isActivityPause = false;

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mIsRunning = mPedometerSettings.isServiceRunning();
//        this.startService(new Intent(this,
//                StepService.class));
        startStepService();
        mIsRunning = true;
        //bindStepService();

        mPedometerSettings.clearServiceRunning();
    }

    public void onPause() {
        super.onPause();
        Log.i("xianrui", "MainActivity onPause");
        MyApplication.getApplication().getHuanXinHelper().isActivityPause = true;
    }

    public void onEventMainThread(JumpPageEvent jumpPageEvent) {
        Class<?> c = jumpPageEvent.getJumpClass();
        finishAll();
        if (c.getName().equals(FollowMentorPlanFragment.class.getName())) {
            selectNextItem(PLAN_SELECT);
        } else if (c.getName().equals(Discovery13Fragment.class.getName())) {
            selectNextItem(FIND_SELECT);
        } else if (c.getName().equals(MyProfileFragment.class.getName())) {
            selectNextItem(ME_SELECT);
        } else if (c.getName().equals(FollowFragment.class.getName())) {
            selectNextItem(FOLLOW_SELECT);
        } else {
            if (jumpPageEvent.getParams() != null) {
                showNext((Class<? extends BaseFragment>) c, jumpPageEvent.getParams());
            } else {
                showNext((Class<? extends BaseFragment>) c);
            }
        }
    }

    public void onEventMainThread(PushExperienceEvent pushExperienceEvent) {
        if (mPushExperienceDialog == null) {
            mPushExperienceDialog = new PushExperienceDialog(this);
        }
        if (mPushBadgeDialog == null) {
            mPushBadgeDialog = new PushBadgeDialog(this);
        }
        UserModel.getInstance().tryLoadRemote(true);
        if (pushExperienceEvent.getPushExperienceDto().badge != null && pushExperienceEvent.getPushExperienceDto().badge.size() > 0) {
            mPushBadgeDialog.setPushExperienceDto(pushExperienceEvent.getPushExperienceDto());
            mPushBadgeDialog.show();
        } else {
            mPushExperienceDialog.setPushExperienceDto(pushExperienceEvent.getPushExperienceDto());
            mPushExperienceDialog.show();
        }

    }

    public void onEventMainThread(JumpToGroupApplicationEvent jumpToGroupApplicationEvent) {
        Log.i("group_id", jumpToGroupApplicationEvent.getParams().get("group_id").toString());
        Log.i("leader_huanxin_id", jumpToGroupApplicationEvent.getParams().get("leader_huanxin_id").toString());
        ApplicationGroupFragment.start(this, Integer.valueOf(jumpToGroupApplicationEvent.getParams().get("group_id").toString()), jumpToGroupApplicationEvent.getParams().get("leader_huanxin_id").toString());
    }

    public void onEventMainThread(JumpToChooseTagsEvent jumpToChooseTagsEvent) {
        ChooseSportInterestFragment.start(this);
    }
//    public void onEventMainThread(JoinGroupSuccessEvent joinGroupSuccessEvent) {
//        Log.i("darren", "joinGroupSuccessEvent");
//        Log.i("darren",group_tag_list_id);
//        if(group_tag_list_id!=-1){
//            popToFragment(group_tag_list_id);
//        }
//        //finishAll();
//        // GroupChatFragment.start(this);
//    }

    public void finishAll() {
        Log.i("darren", "finishAll" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            Log.i("darren", getSupportFragmentManager().getBackStackEntryAt(0).getName());
            getSupportFragmentManager().popBackStackImmediate(getSupportFragmentManager().getBackStackEntryAt(0).getName()
                    , FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int type = intent.getIntExtra("type", -1);
        finishAll();
        switch (type) {
            case 42:
                GroupMemberApplicationListFragment.start(this);
                break;
            case 100:
                String huanxin_id = intent.getStringExtra("huanxin_id");
                PrivateChatFragment.start(this, huanxin_id);
                break;
            case 101:
                GroupChatFragment.start(this);
                break;
            case 310:
                AboutMeMessageFragment.start(this);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("xianrui", "HomeActivity onSaveInstanceState  currentSelectedPosition + " + currentSelectedPosition);
        outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
        super.onSaveInstanceState(outState);
    }


    public boolean isNeedShowFragment(Fragment fragment) {
        if (currentTabFragment != null) {
            if (currentTabFragment == fragment) {
                return true;
            } else {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction ft = manager.beginTransaction();
                ft.hide(fragment);
                ft.commit();
                return false;
            }
        }
        return false;
    }

    @OnClick({R.id.bottom_plan_btn, R.id.bottom_find_btn, R.id.bottom_me_btn, R.id.bottom_follow_btn})
    public void onMenuItemClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_plan_btn:
                selectNextItem(PLAN_SELECT);
                break;
            case R.id.bottom_find_btn:
                selectNextItem(FIND_SELECT);
                break;
            case R.id.bottom_me_btn:
                selectNextItem(ME_SELECT);
                break;
            case R.id.bottom_follow_btn:
                selectNextItem(FOLLOW_SELECT);
                break;
        }
    }


    private void checkoutTab(Class<? extends BaseFragment> fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        currentTabFragment = (BaseFragment) manager.findFragmentByTag(tag);

        if (currentTabFragment == null) {
            currentTabFragment = (BaseFragment) Fragment.instantiate(this, fragment.getName());
            ft.add(R.id.main_content_layout, currentTabFragment, tag);
        }


        if (lastTabFragment != null) {
            lastTabFragment.onPause();
            ft.hide(lastTabFragment);
        }

        if (currentTabFragment.isAdded() && currentTabFragment.isResumed()) {
            currentTabFragment.onResume();
        }
        ft.show(currentTabFragment);
        ft.commit();

        lastTabFragment = currentTabFragment;
    }

    private void selectNextItem(int select) {
        if (currentSelectedPosition == select)
            return;
        switch (select) {
            case FOLLOW_SELECT:
                if (!UserModel.getInstance().isLogin()) {
                    Intent goLogin = new Intent(this, RegisterAndLoginActivity.class);
                    startActivity(goLogin);
                } else {
                    checkoutTab(FollowFragment.class, FollowFragment.TAG);
                    currentSelectedPosition = FOLLOW_SELECT;
                }
                break;
            case PLAN_SELECT:
                checkoutTab(FollowMentorPlanFragment.class, FollowMentorPlanFragment.TAG);
                currentSelectedPosition = PLAN_SELECT;
                break;
            case FIND_SELECT:
                checkoutTab(Discovery13Fragment.class, Discovery13Fragment.TAG);
                currentSelectedPosition = FIND_SELECT;
                break;
            case ME_SELECT:
                if (!UserModel.getInstance().isLogin()) {
                    Intent goLogin = new Intent(this, RegisterAndLoginActivity.class);
                    startActivity(goLogin);
                } else {
                    checkoutTab(MyProfileFragment.class, MyProfileFragment.TAG);
                    currentSelectedPosition = ME_SELECT;
                }
                break;
            default:
                break;
        }
        setBottomBarStatus(currentSelectedPosition);
    }

    private void setBottomBarStatus(int currentSelectedPosition) {
        Log.i("xianrui", "currentSelectedPosition  " + currentSelectedPosition);
        if (currentSelectedPosition != FOLLOW_SELECT) {
            bottomFollowImg.setSelected(false);
            bottomFollowText.setTextColor(getResources().getColor(R.color.gray_9b));
        } else {
            bottomFollowImg.setSelected(true);
            bottomFollowText.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (currentSelectedPosition != PLAN_SELECT) {
            bottomPlanImg.setSelected(false);
            bottomPlanText.setTextColor(getResources().getColor(R.color.gray_9b));
        } else {
            bottomPlanImg.setSelected(true);
            bottomPlanText.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (currentSelectedPosition != FIND_SELECT) {
            bottomFindImg.setSelected(false);
            bottomFindText.setTextColor(getResources().getColor(R.color.gray_9b));
        } else {
            bottomFindImg.setSelected(true);
            bottomFindText.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        if (currentSelectedPosition != ME_SELECT) {
            bottomMeImg.setSelected(false);
            bottomMeText.setTextColor(getResources().getColor(R.color.gray_9b));
        } else {
            bottomMeImg.setSelected(true);
            bottomMeText.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            FragmentManager manager = getSupportFragmentManager();
            if (manager != null) {
                int backStackEntryCount = manager.getBackStackEntryCount();
                if (backStackEntryCount > 0) {
                    manager.popBackStack();
                    return true;
                } else {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        exitTime = System.currentTimeMillis();
                    } else {
                        MobclickAgent.onKillProcess(this);
                        finish();
                        //System.exit(0);
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public int showNext(Class<? extends BaseFragment> fragmentName) {
        return showNext(fragmentName, null);
    }


    public int showNext(Class<? extends BaseFragment> fragmentName, HashMap<String, Object> newInstanceParams) {
        BaseFragment f = null;
        try {
            f = fragmentName.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (f != null) {
            if (newInstanceParams != null) {
                f.setNewInstanceParams(newInstanceParams);
            } else {
                f.clearNewInstanceParams();
            }
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(R.id.content);
//        if (fragment == null) {
//            return;
//        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(R.id.content_layout, f);
        if (f != null) {
            fragmentTransaction.addToBackStack(f.getClass().getName());
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //return fragmentTransaction.commit();
        return fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetValues(false);
//        unbindStepService();
//        stopStepService();
    }

    public void onEvent(LoginConflictEvent loginConflictEvent) {
        UserModel.getInstance().logout();
        Intent goLoginAct = new Intent(this, RegisterAndLoginActivity.class);
        startActivity(goLoginAct);
    }

    public void popToFragment(int id) {
        getSupportFragmentManager().popBackStackImmediate(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public int add_task_fragment_id = -1;
    public int group_profile_sub_id = -1;
    public int group_tag_list_id = -1;
    public int follow_mentor_plan_id = -2;
    public int my_person_info_id = -1;


    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            android.util.Log.i("darren", "stepChanged");
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }

        public void paceChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
        }

        public void distanceChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int) (value * 1000), 0));
        }

        public void speedChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG, (int) (value * 1000), 0));
        }

        public void caloriesChanged(float value) {
            mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG, (int) (value), 0));
        }
    };
    private static final int STEPS_MSG = 1;
    private static final int PACE_MSG = 2;
    private static final int DISTANCE_MSG = 3;
    private static final int SPEED_MSG = 4;
    private static final int CALORIES_MSG = 5;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
                    mStepValue = (int) msg.arg1;
                    String text = String.valueOf(mStepValue);
                    android.util.Log.i("darren", "mStepValue" + text);
                    // 设置参数
                    if (mStepValue % 10 == 0) {

                    }

                    break;
                case PACE_MSG:
//                    mPaceValue = msg.arg1;
//                    if (mPaceValue <= 0) {
//                        mPaceValueView.setText("0");
//                    } else {
//                        mPaceValueView.setText("" + (int) mPaceValue);
//                    }
                    break;
                case DISTANCE_MSG:
//                    mDistanceValue = ((int) msg.arg1) / 1000f;
//                    if (mDistanceValue <= 0) {
//                        tvDistance.setText("0");
//                    } else {
//                        tvDistance.setText(
//                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
//                        );
//                    }
                    break;
                case SPEED_MSG:
//                    mSpeedValue = ((int) msg.arg1) / 1000f;
//                    if (mSpeedValue <= 0) {
//                        mSpeedValueView.setText("0");
//                    } else {
//                        mSpeedValueView.setText(
//                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
//                        );
//                    }
                    break;
                case CALORIES_MSG:
//                    mCaloriesValue = msg.arg1;
//                    if (mCaloriesValue <= 0) {
//                        mCaloriesValueView.setText("0");
//                    } else {
//                        mCaloriesValueView.setText("" + (int) mCaloriesValue);
//                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };
}
