package com.gezhii.fitgroup.tools;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xianrui on 15/6/12.
 */
public class TimeHelper {


    Calendar mCalendar;

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd");

    public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_y_m_d_h_m_s = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_m_d_h_m = new SimpleDateFormat("MM月dd日 HH:mm");
    public static final SimpleDateFormat DATE_FORMAT_m_d = new SimpleDateFormat("MM月dd日");
    public static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat signCardDateFormat = new SimpleDateFormat("yyyy.MM.dd | HH:mm");
    public static final SimpleDateFormat RecordWeekStartFormat = new SimpleDateFormat("yyyy年MM月dd日");
    public static final SimpleDateFormat RecordWeekEndFormat = new SimpleDateFormat("MM月dd日");


    private static class TimeHelperHolder {
        public final static TimeHelper sington = new TimeHelper();
    }

    public TimeHelper() {
        mCalendar = Calendar.getInstance();
    }


    public static String getDay(Date created_time) {
        return TimeHelper.dateFormat.format(created_time);
    }


    public boolean isThisYear(Date date) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(new Date());
        c2.setTime(date);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    public String formatDateForTitle(Date created_time) {
        if (isThisYear(created_time)) {
            return TimeHelper.RecordWeekEndFormat.format(created_time);
        } else {
            return TimeHelper.RecordWeekStartFormat.format(created_time);
        }
    }

    public void refresh() {
        mCalendar = Calendar.getInstance();
    }


    public static TimeHelper getInstance() {
        return TimeHelperHolder.sington;
    }


    public String getTodayString() {
        refresh();
        return dateFormat.format(mCalendar.getTime());
    }

    public String getSevenDayLaterDayString() {
        refresh();
        Calendar calendar = (Calendar) this.mCalendar.clone();
        calendar.add(Calendar.DAY_OF_MONTH, 6);
        return dateFormat.format(calendar.getTime());
    }

    public static String getTimeDifferenceString(Date date) {
        Calendar mCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (mCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            return hourFormat.format(date);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (mCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                return "昨天 " + hourFormat.format(date);
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return simpleDateFormat.format(calendar.getTime()) + " " + hourFormat.format(date);
            }
        }
    }

    public static String getNewTimeDifferenceString(Date date) {//打卡日期新规则
        Calendar mCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        long l = mCalendar.getTimeInMillis() - date.getTime();
        if (mCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
            int hour = (int) (l / (60 * 60 * 1000));
            if (hour < 1) {
                return "刚刚";
            } else if (1 <= hour && hour <= 4) {
                return hour + "小时前";
            } else {
                return "今天";
            }
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            if (mCalendar.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                return "昨天 ";
            } else {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return simpleDateFormat.format(calendar.getTime());
            }
        }
    }

    public ArrayList<TimeTableDayItem> getTimeTableDayList(int weekOffset, Calendar mCalendar) {
        ArrayList<TimeTableDayItem> timeTableDayItems = new ArrayList<>();
        Calendar calendar = (Calendar) mCalendar.clone();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (weekOffset != 0) {
            calendar.add(Calendar.WEEK_OF_YEAR, weekOffset);
        }

        int nowWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        Log.i("xianrui", "nowWeek ", nowWeek);
        if (nowWeek == -1) {
            nowWeek = 6;
        }
        if (nowWeek == 0) {
            for (int i = 0; i < 7; i++, nowWeek++) {
                TimeTableDayItem timeTableDayItem = new TimeTableDayItem();
                timeTableDayItem.week = nowWeek;
                timeTableDayItem.day = calendar.get(Calendar.DAY_OF_MONTH);
                timeTableDayItem.weekString = getWeekString(nowWeek);
                timeTableDayItem.date = calendar.getTime();
                timeTableDayItems.add(timeTableDayItem);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            int indexWeek = 0;
            calendar.add(Calendar.DAY_OF_MONTH, -nowWeek);
            for (int i = 0; i < 7; i++, indexWeek++) {
                if (indexWeek > 7) {
                    indexWeek = 1;
                }
                TimeTableDayItem timeTableDayItem = new TimeTableDayItem();
                timeTableDayItem.week = indexWeek;
                timeTableDayItem.day = calendar.get(Calendar.DAY_OF_MONTH);
                timeTableDayItem.weekString = getWeekString(indexWeek);
                timeTableDayItem.date = calendar.getTime();
                timeTableDayItems.add(timeTableDayItem);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }


        return timeTableDayItems;
    }


    private String getWeekString(int week) {
        String weekString = MyApplication.getApplication().getString(R.string.week);
        switch (week) {
            case 0:
                return MyApplication.getApplication().getString(R.string.sun);
            case 1:
                return MyApplication.getApplication().getString(R.string.mon);
            case 2:
                return MyApplication.getApplication().getString(R.string.tues);
            case 3:
                return MyApplication.getApplication().getString(R.string.wednes);
            case 4:
                return MyApplication.getApplication().getString(R.string.thurs);
            case 5:
                return MyApplication.getApplication().getString(R.string.fri);
            case 6:
                return MyApplication.getApplication().getString(R.string.satur);

        }

        return "";
    }

    public static int compare(Date d1, Date d2) {
        return (int) (d1.getTime() - d2.getTime());
    }

    public static List<String> getBetweenDateList(Date d1, Date d2) {
        List<String> dateList = new ArrayList<>();
        long from = d1.getTime();
        long to = d2.getTime();
        int day = (int) ((to - from) / (1000 * 60 * 60 * 24));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        dateList.add(format.format(d1));
        for (int i = 0; i < day; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date date1 = new Date(calendar.getTimeInMillis());
            dateList.add(format.format(date1));
        }
        return dateList;
    }

    public static List<String> getBetweenDateList(Date d1, int days) {
        List<String> dateList = new ArrayList<>();
        long from = d1.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        dateList.add(format.format(d1));
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date date1 = new Date(calendar.getTimeInMillis());
            dateList.add(format.format(date1));
        }
        return dateList;
    }

    public static String getTheDayTomorrowDay(String d1) {
        String tomorrow = null;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = fmt.parse(d1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date date1 = new Date(calendar.getTimeInMillis());
            tomorrow = format.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return tomorrow;
    }

    public static List<String> getForwardBetweenDateList(Date d1, int days) {
        List<String> dateList = new ArrayList<>();
        long from = d1.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d1);
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        dateList.add(format.format(d1));
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Date date1 = new Date(calendar.getTimeInMillis());
            dateList.add(format.format(date1));
        }
        return dateList;
    }

    public static boolean isToToday(String date) {
        Date date1 = null;
        try {
            date1 = TimeHelper.dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.getTime() >= Calendar.getInstance().getTime().getTime();
    }

    public static int getBetweenDaysNumber(String dateTo, String dateFrom) {
        Date date_to = null;
        Date date_from = null;
        try {
            date_to = TimeHelper.dateFormat.parse(dateTo);
            date_from = TimeHelper.dateFormat.parse(dateFrom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int) ((date_to.getTime() - date_from.getTime()) / (1000 * 60 * 60 * 24)) + 1;
    }


    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public final static class TimeTableDayItem {
        private int week;
        private String weekString;
        private int day;
        private boolean canSelect;
        private Date date;
        private boolean enabled = true;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }

        public String getWeekString() {
            return weekString;
        }

        public void setWeekString(String weekString) {
            this.weekString = weekString;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public boolean isCanSelect() {
            return canSelect;
        }

        public void setCanSelect(boolean canSelect) {
            this.canSelect = canSelect;
        }
    }

}
