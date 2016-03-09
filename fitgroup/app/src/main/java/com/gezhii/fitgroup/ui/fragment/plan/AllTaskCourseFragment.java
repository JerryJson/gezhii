package com.gezhii.fitgroup.ui.fragment.plan;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.NetUtils;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Video;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 16/3/3.
 */
public class AllTaskCourseFragment extends BaseFragment {
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
    @InjectView(R.id.all_task_course_layout)
    LinearLayout allTaskCourseLayout;//所有课程

    List<Video> videoList;

    public static void start(Activity activity, List<Video> videoList) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put("videoList", videoList);
        mainActivity.showNext(AllTaskCourseFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_task_course_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        initTitleView();
        videoList = (List<Video>) getNewInstanceParams().get("videoList");
        for (int i = 0; i < videoList.size(); i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = Screen.dip2px(1);
            final Video video = videoList.get(i);
            View itemView = View.inflate(getActivity(), R.layout.task_course_item, null);
            ViewHolder viewHolder = new ViewHolder(itemView);
            QiniuHelper.bindVideoThumbnaiImage(video.getVideo_link(), viewHolder.taskCourseImg);
            viewHolder.taskCourseNameText.setText(video.getUser_nick_name() + "的教程");
            viewHolder.taskAuthorNameText.setText("提供者：" + video.getUser_nick_name());
            viewHolder.taskCourseDurationText.setText(TimeHelper.secToTime(video.getDuration()));
            viewHolder.moreLayout.setVisibility(View.INVISIBLE);
            viewHolder.reportBtn.setVisibility(View.VISIBLE);
            viewHolder.reportBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                    alertParams.setMessage("确定要举报该用户吗?");
                    alertParams.setTitle("举报");
                    alertParams.setCancelString("取消");
                    alertParams.setConfirmString("确定");
                    alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            showToast("举报成功");
                        }
                    });
                    alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertHelper.showAlert(getActivity(), alertParams);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (NetUtils.isWifiConnection(getActivity())) {
                        Intent intents = new Intent(Intent.ACTION_VIEW);
                        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(video.getVideo_link())), "video/*");
                        startActivity(intents);
                    } else {
                        AlertHelper.AlertParams params = new AlertHelper.AlertParams();
                        params.setMessage("您当前为非WiFi网络环境,建议您在WiFi环境下观看,是否继续？");
                        params.setCancelString("取消");
                        params.setCancelListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        params.setConfirmString("确定");
                        params.setConfirmListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intents = new Intent(Intent.ACTION_VIEW);
                                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(video.getVideo_link())), "video/*");
                                startActivity(intents);
                            }
                        });
                        AlertHelper.showAlert(getActivity(), params);
                    }
                }
            });
            allTaskCourseLayout.addView(itemView, layoutParams);
        }
        return rootView;
    }

    public void initTitleView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("所有教程");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'task_course_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.task_course_img)
        ImageView taskCourseImg;
        @InjectView(R.id.task_course_name_text)
        TextView taskCourseNameText;
        @InjectView(R.id.task_author_name_text)
        TextView taskAuthorNameText;
        @InjectView(R.id.task_course_duration_text)
        TextView taskCourseDurationText;
        @InjectView(R.id.more_task_course_btn)
        LinearLayout moreTaskCourseBtn;
        @InjectView(R.id.upload_my_task_course)
        RelativeLayout uploadMyTaskCourse;
        @InjectView(R.id.more_layout)
        LinearLayout moreLayout;
        @InjectView(R.id.report_btn)
        ImageView reportBtn;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
