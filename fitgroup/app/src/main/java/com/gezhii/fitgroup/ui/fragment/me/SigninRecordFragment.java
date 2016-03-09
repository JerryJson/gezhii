package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.SignRecordDataChangeEvent;
import com.gezhii.fitgroup.model.SignRecordModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.SignHistoryListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardDetailFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/19.
 */
public class SigninRecordFragment extends BaseFragment {
    public static final String TAG = SigninRecordFragment.class.getName();
    public static final int MAX_WEEK_COUNT = 1000;

    @InjectView(R.id.select_week_left)
    ImageView selectWeekLeft;
    @InjectView(R.id.record_week_text)
    TextView recordWeekText;
    @InjectView(R.id.select_week_right)
    ImageView selectWeekRight;
    @InjectView(R.id.week_view_pager)
    ViewPager weekViewPager;
    @InjectView(R.id.record_continuous_sign_text)
    TextView recordContinuousSignText;
    @InjectView(R.id.record_cumulative_sign_text)
    TextView recordCumulativeSignText;

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

    SignHistoryListAdapter mSignHistoryListAdapter;
    @InjectView(R.id.no_card_records_layout)
    LinearLayout noCardRecordsLayout;
    @InjectView(R.id.sign_history_list_view)
    LoadMoreListView signHistoryListView;

    RecordWeekAdapter mRecordWeekAdapter;

    SignCardDetailFragment mSignCardDetailFragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.record_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "my_signin_history", UmengEvents.getEventMap("click", "load"));
        initTitle();
        initViewPage();
        signHistoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getSigninHistory(1, UserModel.getInstance().getUserId(), UserModel.getInstance().getUserId(), Config.loadPageCount,
                new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                        mSignHistoryListAdapter = new SignHistoryListAdapter(getActivity(), UserModel.getInstance().getUserDto().getUser(), 1,mSignCardDetailFragment);
                        mSignHistoryListAdapter.init_data_list(signHistoryDto.data_list);
                        signHistoryListView.setLoadMoreListViewAdapter(mSignHistoryListAdapter);
                        if (signHistoryDto.data_list.size() == 0) {
                            noCardRecordsLayout.setVisibility(View.VISIBLE);
                            signHistoryListView.setVisibility(View.GONE);
                        } else {
                            noCardRecordsLayout.setVisibility(View.GONE);
                            signHistoryListView.setVisibility(View.VISIBLE);
                        }
                        if (signHistoryDto.data_list.size() == Config.loadPageCount) {
                            Object[] params = new Object[4];
                            params[0] = 1;
                            params[1] = UserModel.getInstance().getUserId();
                            params[2] = UserModel.getInstance().getUserId();
                            params[3] = Config.loadPageCount;
                            mSignHistoryListAdapter.setIsHasMore(true);
                            signHistoryListView.setApiAutoInvoker("getSigninHistory", params, SignHistoryDto.class);
                        }
                    }
                });
        setView();
    }

    public void onEvent(SignRecordDataChangeEvent signRecordDataChangeEvent) {
        initViewPage();
    }


    public void initTitle() {
        titleText.setText("打卡记录");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setView() {
        if (UserModel.getInstance().isLogin()) {
            recordContinuousSignText.setText(String.valueOf(UserModel.getInstance().getUserDto().getUser().getContinue_signin_days()));
            recordCumulativeSignText.setText(String.valueOf(UserModel.getInstance().getUserDto().getUser().getSignin_count()));
        }
        if (mSignHistoryListAdapter == null) {
            mSignHistoryListAdapter = new SignHistoryListAdapter(getActivity(), UserModel.getInstance().getUserDto().getUser(), 1,mSignCardDetailFragment);
        }
    }

    private void initViewPage() {
        selectWeekLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (weekViewPager.getCurrentItem() > 0) {
                    weekViewPager.setCurrentItem(weekViewPager.getCurrentItem() - 1, false);
                }
            }
        });

        selectWeekRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (weekViewPager.getCurrentItem() < MAX_WEEK_COUNT) {
                    weekViewPager.setCurrentItem(weekViewPager.getCurrentItem() + 1, false);
                }
            }
        });


        mRecordWeekAdapter = new RecordWeekAdapter(getActivity());
        weekViewPager.setAdapter(mRecordWeekAdapter);
        weekViewPager.setCurrentItem(MAX_WEEK_COUNT - 1);
        setRecordWeekText(MAX_WEEK_COUNT - 1);

        weekViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setRecordWeekText(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setRecordWeekText(int position) {
        final ArrayList<TimeHelper.TimeTableDayItem> timeTableDayItems =
                TimeHelper.getInstance().getTimeTableDayList(position - MAX_WEEK_COUNT + 1, mRecordWeekAdapter.getCalendar());
        String timeText = TimeHelper.RecordWeekStartFormat.format(timeTableDayItems.get(0).getDate())
                + "-" + TimeHelper.RecordWeekEndFormat.format(timeTableDayItems.get(timeTableDayItems.size() - 1).getDate());
        recordWeekText.setText(timeText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }


    public static class RecordWeekAdapter extends PagerAdapter {

        HashMap<Integer, View> viewCacheMap;
        Calendar mCalendar;

        Activity mContext;

        public RecordWeekAdapter(Activity mContext) {
            this.mContext = mContext;
            mCalendar = Calendar.getInstance();
            viewCacheMap = new HashMap<>();
        }


        @Override
        public int getCount() {
            return MAX_WEEK_COUNT;
        }

        public Calendar getCalendar() {
            return mCalendar;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final LinearLayout itemLayout;
            final int selectPosition = position - MAX_WEEK_COUNT + 1;
            final ArrayList<TimeHelper.TimeTableDayItem> timeTableDayItems = TimeHelper.getInstance().getTimeTableDayList(selectPosition, mCalendar);


            ViewHolder vh;

            if (viewCacheMap.get(position) == null) {
                itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.record_fragment_week_item_layout, null);
                vh = new ViewHolder(itemLayout);
                itemLayout.setTag(vh);
                viewCacheMap.put(position, itemLayout);
            } else {
                itemLayout = (LinearLayout) viewCacheMap.get(position);
                vh = (ViewHolder) itemLayout.getTag();
            }

            final ViewHolder viewHolder = vh;


            for (int i = 0; i < viewHolder.dayTextLayout.getChildCount(); i++) {
                FrameLayout frameLayout = (FrameLayout) viewHolder.dayTextLayout.getChildAt(i);
                TextView dayText = (TextView) frameLayout.getChildAt(0);
                dayText.setText(String.valueOf(timeTableDayItems.get(i).getDay()));
                if (SignRecordModel.getInstance().getIsSign(timeTableDayItems.get(i).getDate())) {
                    dayText.setSelected(true);
                } else {
                    dayText.setSelected(false);
                }
            }
            container.addView(itemLayout);

            return itemLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(viewCacheMap.get(position));

        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'record_fragment_week_item_layout.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        static class ViewHolder {
            @InjectView(R.id.root_layout)
            LinearLayout rootLayout;
            @InjectView(R.id.day_text_layout)
            LinearLayout dayTextLayout;


            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }

}
