package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.dto.SportScheduleDTO;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.event.MentorChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.SportScheduleAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/17.
 */
public class SportScheduleFragment extends BaseFragment {
    @InjectView(R.id.close_img)
    ImageView closeImg;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.icon_img)
    RectImageView iconImg;
    @InjectView(R.id.sport_record_listview)
    LoadMoreListView sportRecordListview;
    SportScheduleAdapter sportScheduleAdapter;
    @InjectView(R.id.join_with_vip_btn)
    RelativeLayout joinWithVipBtn;
    @InjectView(R.id.adopt_text)
    TextView adoptText;

    private ArrayList<Object> sportScheduleDTOList;
    private String lastDate;
    private int dayNumber = 0;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading = false;
    private int user_id;

    private boolean firstRequest = true;

    public static void start(Activity activity) {
        start(activity, UserModel.getInstance().getUserId(), UserModel.getInstance().getUserIcon());
    }

    public static void start(Activity activity, int user_id, String user_icon) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("user_icon", user_icon);
        mainActivity.showNext(SportScheduleFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sport_schedule_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "UserSigninTrack", UmengEvents.getEventMap("click", "load"));
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        QiniuHelper.bindAvatarImage((String) getNewInstanceParams().get("user_icon"), iconImg);

        user_id = (int) getNewInstanceParams().get("user_id");
        getDataAndSet(null);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        sportRecordListview.setLayoutManager(linearLayoutManager);
        sportScheduleAdapter = new SportScheduleAdapter(getActivity());

        sportScheduleDTOList = new ArrayList<Object>();
        sportScheduleAdapter.init_data_list(sportScheduleDTOList);
        sportRecordListview.setLoadMoreListViewAdapter(sportScheduleAdapter);

        sportRecordListview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                if (lastVisibleItem >= totalItemCount - 1 && dy >= 0) {
                    Log.i("darren", "===lastDate:" + lastDate);
                    if (!TimeHelper.isToToday(lastDate)) {
                        getDataAndSet(lastDate);
                    }
                }
            }
        });
        if(UserModel.getInstance().getUserId() != user_id){
            if (UserModel.getInstance().getUserDto().getUser().getMentor_id() != user_id) {
                joinWithVipBtn.setSelected(false);
                joinWithVipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        API.adoptMentor(UserModel.getInstance().getUserId(), user_id, new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                EventBus.getDefault().post(new MentorChangeEvent());
                                joinWithVipBtn.setSelected(true);
                                adoptText.setText("已跟随");
                                UserModel.getInstance().getUserDto().getUser().setMentor_id(user_id);
                            }
                        });
                    }
                });
            } else {
                joinWithVipBtn.setSelected(true);
                adoptText.setText("已跟随");
            }
        }else{
            joinWithVipBtn.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void getDataAndSet(String begin_time) {
        if (!loading) {
            loading = true;
            API.getSigninTrack(user_id, begin_time, 7, new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                    List<SportScheduleDTO> list = new ArrayList<SportScheduleDTO>();
                    List<String> dateList = new ArrayList<>();
                    Date d1 = null;

                    try {
                        if (signHistoryDto.data_list.size() > 0 && firstRequest) {
                            d1 = signHistoryDto.data_list.get(0).getCreated_time();
                        } else {
                            if (lastDate == null) {
                                d1 = TimeHelper.dateFormat.parse(TimeHelper.getInstance().getTodayString());
                            } else {
                                d1 = TimeHelper.dateFormat.parse(lastDate);
                            }

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dateList = TimeHelper.getBetweenDateList(d1, 6);
                    lastDate = TimeHelper.getTheDayTomorrowDay(dateList.get(dateList.size() - 1));
                    for (int i = 0; i < dateList.size(); i++) {
                        if (!TimeHelper.isToToday(dateList.get(i))) {
                            SportScheduleDTO sportScheduleDTO = new SportScheduleDTO();
                            List<Signin> signinList = new ArrayList<Signin>();
                            for (int j = 0; j < signHistoryDto.data_list.size(); j++) {
                                if (TimeHelper.getDay(signHistoryDto.data_list.get(j).getCreated_time()).equals(dateList.get(i))) {
                                    Signin signin = new Signin();
                                    signin.setTask_name(signHistoryDto.data_list.get(j).getTask_name());
                                    signinList.add(signin);
                                }
                            }
                            sportScheduleDTO.setList(signinList);
                            sportScheduleDTO.setThe_day_number(dayNumber + 1);
                            list.add(sportScheduleDTO);
                            dayNumber++;
                        }
                    }

                    sportScheduleDTOList.addAll(list);
                    sportScheduleAdapter.setTotal_data_list(sportScheduleDTOList);
                    sportScheduleAdapter.notifyDataSetChanged();

                    if (sportScheduleDTOList.size() > 0)
                        dayNumber = ((SportScheduleDTO) sportScheduleDTOList.get(sportScheduleDTOList.size() - 1)).getThe_day_number();
                    loading = false;
                    firstRequest = false;
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
