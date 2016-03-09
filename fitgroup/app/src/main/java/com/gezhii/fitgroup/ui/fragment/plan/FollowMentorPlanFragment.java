package com.gezhii.fitgroup.ui.fragment.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.dto.MentorAndMePlanDto;
import com.gezhii.fitgroup.dto.MentorSignHistoryDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CustomTaskChangeEvent;
import com.gezhii.fitgroup.event.MentorChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.SigninCompleteEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.MentorAndMePlanAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/19.
 */
public class FollowMentorPlanFragment extends BaseFragment {
    public static final String TAG = FollowMentorPlanFragment.class.getName();

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
    @InjectView(R.id.plan_list)
    LoadMoreListView planList;
    @InjectView(R.id.head_text)
    TextView headText;
    @InjectView(R.id.head_icon)
    RectImageView headIcon;
    @InjectView(R.id.plan_head_layout)
    LinearLayout planHeadLayout;
    @InjectView(R.id.head_change_icon)
    RectImageView headChangeIcon;
    @InjectView(R.id.head_change_name)
    TextView headChangeName;
    @InjectView(R.id.head_change_mentor_layout)
    LinearLayout headChangeMentorLayout;
    @InjectView(R.id.head_change_layout)
    LinearLayout headChangeLayout;

    private List<Object> mentorAndMeDateList;
    private MentorAndMePlanAdapter mAdapter;
    private boolean isHasMore = true;
    private boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private String lastDate;
    private int currentState;
    private boolean isFirstLoadData = true;
    private User currentMentor;
    private boolean isRecored;
    private int startY;
    private boolean isAwokeChangeMentor = false;
    private int pos[] = {-1, -1}; //记录recylerview第一个条目在屏幕的坐标信息

    private long lastRefreshTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plan_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "MyTasks", UmengEvents.getEventMap("click", "load"));
        EventBus.getDefault().register(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        planList.setLayoutManager(linearLayoutManager);
        setView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lastRefreshTime != 0) {
            if ((System.currentTimeMillis() - lastRefreshTime) / 1000 / 60 / 60 >= 1) {
                setView();
            }
        }
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
    }

    private void setView() {
        isLoading = false;
        lastDate = null;
        isFirstLoadData = true;
        mentorAndMeDateList = new ArrayList<>();
        mAdapter = new MentorAndMePlanAdapter(getActivity());
        mAdapter.init_data_list(mentorAndMeDateList);
        planList.setLoadMoreListViewAdapter(mAdapter);

        backBtn.setVisibility(View.GONE);
        titleText.setText("运动任务");
        rightText.setText("换人");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserModel.getInstance().getUserDto().getUser().getTags() != null && UserModel.getInstance().getUserDto().getUser().getTags().size() > 0) {
                    GetVipUsersFragment.start(getActivity(), null, true);
                } else {
                    ChooseSportInterestFragment.start(getActivity());
                }
            }
        });
        planList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                currentState = newState;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 4 && dy >= 0 && isHasMore) {
                    refreshData(lastDate);
                } else if (mentorAndMeDateList.size() < 4 && isHasMore) {
                    refreshData(lastDate);
                }
            }
        });
        //实现弹性下拉窗口
        planList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!isAwokeChangeMentor) {
                    linearLayoutManager.getChildAt(0).getLocationOnScreen(pos);
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //当前recylerview的第一个可视条目是第一条，并且可视条目已经滑动到屏幕的顶端时（pos[1] > XX）y轴的坐标
                            if (linearLayoutManager.findFirstVisibleItemPosition() == 0 && pos[1] >= Screen.dip2px(45)) {
                                isRecored = true;
                                startY = (int) motionEvent.getY();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isRecored) {
                                break;
                            }
                            planHeadLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                            planHeadLayout.invalidate();
                            planHeadLayout.setVisibility(View.INVISIBLE);
                            isRecored = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (!isRecored && linearLayoutManager.findFirstVisibleItemPosition() == 0 && pos[1] >= Screen.dip2px(45)) {
                                isRecored = true;
                                startY = (int) motionEvent.getY();
                            }
                            if (!isRecored) {
                                break;
                            }
                            int tempY = (int) motionEvent.getY();
                            int moveY = tempY - startY;
                            if (moveY < 0) {
                                break;
                            }
                            planHeadLayout.setVisibility(View.VISIBLE);
                            planHeadLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (moveY * 0.5)));
                            planHeadLayout.invalidate();
                            break;
                    }
                }
                return false;
            }
        });

        refreshData(null);
    }

    private void refreshData(String end_date) {
        if (!isLoading) {
            isLoading = true;
            if (end_date == null) {
                end_date = TimeHelper.getInstance().getTodayString();
            }
            if (isFirstLoadData) {
                EventBus.getDefault().post(new ShowLoadingEvent());
                lastRefreshTime = System.currentTimeMillis();
            }
            API.getMentorSigninHistory(UserModel.getInstance().getUserId(), end_date, new OnRequestEnd() {
                @Override
                public void onRequestSuccess(String response) {
                    Log.i("logger", "   " + response);
                    final MentorSignHistoryDto mentorSignHistoryDto = MentorSignHistoryDto.parserJson(response);
                    if (isFirstLoadData) {
                        isFirstLoadData = false;
                        EventBus.getDefault().post(new CloseLoadingEvent());

                        currentState = mentorSignHistoryDto.getMentor_state();
                        currentMentor = mentorSignHistoryDto.getMentor();


                        //设置是否显示顶部提示换人的view及显示的样式
                        if (currentMentor == null) {
                            isAwokeChangeMentor = false;
                            rightText.setText("跟随达人");
                            headText.setText("今天自己还要继续努力运动哦..");
                            QiniuHelper.bindAvatarImage(UserModel.getInstance().getUserIcon(), headIcon);
                            headChangeLayout.setVisibility(View.GONE);
                        } else if (currentState == 1 && currentMentor != null) {
                            rightText.setText("换人");
                            isAwokeChangeMentor = false;
                            headText.setText(currentMentor.getNick_name() + " 今天还在努力运动中");
                            headChangeLayout.setVisibility(View.GONE);
                            QiniuHelper.bindAvatarImage(currentMentor.getIcon(), headIcon);
                        } else if (currentState == 0 && currentMentor != null) {
                            rightText.setText("换人");
                            isAwokeChangeMentor = true;
                            headChangeLayout.setVisibility(View.VISIBLE);
                            QiniuHelper.bindAvatarImage(currentMentor.getIcon(), headChangeIcon);
                            headChangeName.setText(currentMentor.getNick_name() + " 最近好像没运动了");
                            headChangeMentorLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    GetVipUsersFragment.start(getActivity(), null, true);
                                }
                            });
                        }
                    }

                    isHasMore = mentorSignHistoryDto.getHas_more() == 1 ? true : false;

                    List<String> dateList = new ArrayList<String>();
                    try {
                        //生成从获取的日期往前的日期列表，用于按时间聚合打卡，比获取的时间段多一天（mentor的打卡日期比user的早一天）
                        if (null == lastDate)
                            dateList = TimeHelper.getForwardBetweenDateList(TimeHelper.dateFormat.parse(TimeHelper.getInstance().getTodayString()), mentorSignHistoryDto.getDays());
                        else
                            dateList = TimeHelper.getForwardBetweenDateList(TimeHelper.dateFormat.parse(lastDate), mentorSignHistoryDto.getDays());
                        lastDate = dateList.get(dateList.size() - 1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (mentorSignHistoryDto.getUser_begin_signin_date().startsWith(TimeHelper.getInstance().getTodayString())
                            && mentorSignHistoryDto.getMentor() == null) {
                        MentorAndMePlanDto mentorAndMePlanDto = new MentorAndMePlanDto();
                        mentorAndMePlanDto.setIsToday(true);
                        mentorAndMePlanDto.setDay_number(1);
                        mentorAndMePlanDto.setIsChangeMentor(true);
                        mentorAndMePlanDto.setMentor(mentorSignHistoryDto.getMentor());
                        mentorAndMePlanDto.setMentor_signin(mentorSignHistoryDto.getMentor_signin_history());
                        mentorAndMePlanDto.setUser_signin(mentorSignHistoryDto.getUser_signin_history());

                        mentorAndMeDateList.add(mentorAndMePlanDto);
                        mAdapter.setTotal_data_list(mentorAndMeDateList);
                        mAdapter.notifyDataSetChanged();
                        isLoading = false;
                    } else {

                        mentorSignHistoryDto.setMentor_signin_history(mentorSignHistoryDto.getMentor_signin_history());
                        mentorSignHistoryDto.setUser_signin_history(mentorSignHistoryDto.getUser_signin_history());
                        processSigninData(mentorSignHistoryDto, dateList);

                    }
                }

                @Override
                public void onRequestFail(VolleyError error) {
                    Log.i("logger", "error:");
                    isLoading = false;
                }
            });
        }

    }

    //按日期聚合历史打卡，并对比出每天打卡情况，（只有mentor练了自己没练，自己加练的，跟着mentor练得）
    private void processSigninData(MentorSignHistoryDto mentorSignHistoryDto, List<String> dateList) {
        List<MentorAndMePlanDto> data = new ArrayList<>();

        for (int i = 0; i < dateList.size() - 1; i++) {
            MentorAndMePlanDto mentorAndMePlanDto = new MentorAndMePlanDto();
            mentorAndMePlanDto.setMentor(mentorSignHistoryDto.getMentor());
            mentorAndMePlanDto.setUser_date(dateList.get(i));
            mentorAndMePlanDto.setDay_number(TimeHelper.getBetweenDaysNumber(dateList.get(i), mentorSignHistoryDto.getUser_begin_signin_date()));
            mentorAndMePlanDto.setIsToday(isToday(dateList.get(i)));
            if(isToday(dateList.get(i))){
                HashMap<String,Integer> map = mentorSignHistoryDto.getMentor_yestoday_task_videos();
                List<Signin> user_signin = mentorSignHistoryDto.getUser_signin_history();
                if(map != null && map.keySet().size() != 0){
                    for(int k=0;k<user_signin.size();k++){
                        if(map.get(user_signin.get(k).getTask_name()) != null){
                            user_signin.get(k).setHasVideo(map.get(user_signin.get(k).getTask_name()));
                        }
                    }
                }
            }

            List<Signin> list_user = new ArrayList<Signin>();
            for (int j = 0; j < mentorSignHistoryDto.getUser_signin_history().size(); j++) {
                if (TimeHelper.getDay(mentorSignHistoryDto.getUser_signin_history().get(j).getCreated_time()).equals(dateList.get(i))) {
                    list_user.add(mentorSignHistoryDto.getUser_signin_history().get(j));
                }
            }
            mentorAndMePlanDto.setUser_signin(list_user);
            if (list_user.size() == 0) {
                mentorAndMePlanDto.setUser_is_rest(true);
            } else {
                mentorAndMePlanDto.setUser_is_rest(false);
            }
            data.add(mentorAndMePlanDto);
        }

        for (int i = 1; i < dateList.size(); i++) {
            MentorAndMePlanDto mentorAndMePlanDto = data.get(i - 1);
            mentorAndMePlanDto.setMentor_date(dateList.get(i));

            List<Signin> list = new ArrayList<Signin>();
            for (int j = 0; j < mentorSignHistoryDto.getMentor_signin_history().size(); j++) {
                if (TimeHelper.getDay(mentorSignHistoryDto.getMentor_signin_history().get(j).getCreated_time()).equals(dateList.get(i))) {
                    list.add(mentorSignHistoryDto.getMentor_signin_history().get(j));
                }
            }
            mentorAndMePlanDto.setMentor_signin(list);
            if (list.size() == 0) {
                mentorAndMePlanDto.setMentor_is_rest(true);
            } else {
                mentorAndMePlanDto.setMentor_is_rest(false);
            }
        }

        for (int i = 0; i < data.size(); i++) {
            MentorAndMePlanDto mentorAndMePlanDto = data.get(i);

            List<String> mentor_task_name = new ArrayList<>();
            for (int k = 0; k < mentorAndMePlanDto.getMentor_signin().size(); k++) {
                mentor_task_name.add(mentorAndMePlanDto.getMentor_signin().get(k).getTask_name());
            }
            List<String> user_task_name = new ArrayList<>();
            for (int k = 0; k < mentorAndMePlanDto.getUser_signin().size(); k++) {
                user_task_name.add(mentorAndMePlanDto.getUser_signin().get(k).getTask_name());
            }

            List<String> all_done = new ArrayList<>();
            all_done.addAll(mentor_task_name);
            all_done.retainAll(user_task_name);

            List<String> only_mentor = new ArrayList<>();
            only_mentor.addAll(mentor_task_name);
            only_mentor.removeAll(user_task_name);

            List<String> only_user = new ArrayList<>();
            only_user.addAll(user_task_name);
            only_user.removeAll(mentor_task_name);

            mentorAndMePlanDto.setAll_done(all_done);
            mentorAndMePlanDto.setOnly_mentor_done(only_mentor);
            mentorAndMePlanDto.setOnly_user_done(only_user);
        }

        mentorAndMeDateList.addAll(data);
        for (int i = 0; i < mentorAndMeDateList.size(); i++) {
            MentorAndMePlanDto mentorAndMePlanDto = (MentorAndMePlanDto) mentorAndMeDateList.get(i);
            if (i == 0)
                mentorAndMePlanDto.setIsChangeMentor(true);
            else
                mentorAndMePlanDto.setIsChangeMentor(isMentorChange((MentorAndMePlanDto) mentorAndMeDateList.get(i), (MentorAndMePlanDto) mentorAndMeDateList.get(i - 1)));

        }
        mAdapter.setTotal_data_list(mentorAndMeDateList);
        mAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    //当前日期的mentor和后一天的mentor相比较，判断是否改变了mentor或者跟随状态
    private boolean isMentorChange(MentorAndMePlanDto m1, MentorAndMePlanDto m2) {
        if (m1.getMentor() == null) {
            if (m2.getMentor() != null)
                return true;
            else
                return false;
        } else {
            if (m2.getMentor() == null)
                return true;
            else if (m2.getMentor().getMentor_id() != m1.getMentor().getMentor_id())
                return true;
            else
                return false;
        }
    }

    private boolean isToday(String date) {
        return TimeHelper.getDay(Calendar.getInstance().getTime()).equals(date);
    }

    public void onEventMainThread(MentorChangeEvent mentorChangeEvent) {
        setView();
    }

    public void onEventMainThread(CustomTaskChangeEvent customTaskChangeEvent) {
        if (null != mAdapter) {
            mAdapter.notifyItemChanged(0);
        }
    }

    public void onEventMainThread(SigninCompleteEvent signinCompleteEvent) {
        setView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
