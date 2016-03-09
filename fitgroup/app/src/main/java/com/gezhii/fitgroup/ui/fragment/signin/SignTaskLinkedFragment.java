package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.util.NetUtils;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.dto.SigninRelativeInfoDTO;
import com.gezhii.fitgroup.dto.SpecialInfo;
import com.gezhii.fitgroup.dto.basic.GeneralInfo;
import com.gezhii.fitgroup.dto.basic.SameSigninUserInfoDTO;
import com.gezhii.fitgroup.dto.basic.Video;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.JustSigninEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.TaskDBModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.model.UserStepModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.VideoPickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.follow.ChannelProfileFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.fragment.plan.AllTaskCourseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/21.
 */
public class SignTaskLinkedFragment extends BaseFragment implements OnClickListener {
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
    @InjectView(R.id.week_finish_text)
    TextView weekFinishText;
    @InjectView(R.id.all_finish_text)
    TextView allFinishText;
    @InjectView(R.id.sign_step_flag_layout)
    LinearLayout signStepFlagLayout;
    @InjectView(R.id.sign_img_flag_layout)
    LinearLayout signImgFlagLayout;
    @InjectView(R.id.sign_once_flag_layout)
    LinearLayout signOnceFlagLayout;
    @InjectView(R.id.sign_weight_flag_layout)
    LinearLayout signWeightFlagLayout;
    @InjectView(R.id.see_my_moment_layout)
    LinearLayout seeMyMomentLayout;
    @InjectView(R.id.take_note_layout)
    LinearLayout takeNoteLayout;
    @InjectView(R.id.go_take_note_btn)
    ImageView goTakeNoteBtn;
    @InjectView(R.id.today_finish_person)
    TextView todayFinishPerson;
    @InjectView(R.id.task_name_text)
    TextView taskNameText;
    @InjectView(R.id.they_also_do_layout)
    LinearLayout theyAlsoDoLayout;
    @InjectView(R.id.they_also_do_text)
    TextView theyAlsoDoText;
    @InjectView(R.id.judge_you_like)
    LinearLayout judgeYouLike;
    @InjectView(R.id.they_also_do_out_layout)
    LinearLayout theyAlsoDoOutLayout;
    @InjectView(R.id.step_text)
    TextView stepText;

    //1.4版本视频
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
    @InjectView(R.id.upload_task_course)
    RelativeLayout uploadTaskCourse;
    @InjectView(R.id.not_has_task_course)
    LinearLayout notHasTaskCourse;
    @InjectView(R.id.has_task_course)
    LinearLayout hasTaskCourse;

    String taskName;
    String signinType = "";
    boolean is_signin = false;
    boolean is_take_note = false;
    int signinId = 0;
    int channelId = 0;
    private VideoPickDialog videoPickDialog;

    public static void start(Activity activity, String task_name, boolean is_signin, boolean is_take_note) {
        start(activity, task_name, is_signin, is_take_note, 0);
    }

    public static void start(Activity activity, String task_name, boolean is_signin, boolean is_take_note, int signin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("task_name", task_name);
        hashMap.put("is_signin", is_signin);
        hashMap.put("is_take_note", is_take_note);
        hashMap.put("signin_id", signin_id);
        mainActivity.showNext(SignTaskLinkedFragment.class, hashMap);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_task_linked_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "SigninAndStatistic", UmengEvents.getEventMap("click", "load"));
        EventBus.getDefault().register(this);
        taskName = (String) getNewInstanceParams().get("task_name");
        is_signin = (boolean) getNewInstanceParams().get("is_signin");
        is_take_note = (boolean) getNewInstanceParams().get("is_take_note");
        signinId = (int) getNewInstanceParams().get("signin_id");
        initTitleView();
        signinType = TaskDBModel.getInstance().getSigninTypeByName(taskName);

        uploadMyTaskCourse.setOnClickListener(this);
        uploadTaskCourse.setOnClickListener(this);

        setView();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getSigninRelativeInfo(UserModel.getInstance().getUserId(), taskName, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                SigninRelativeInfoDTO signinRelativeInfoDTO = SigninRelativeInfoDTO.parserJson(response);
                weekFinishText.setText(signinRelativeInfoDTO.getWeek_signin_count() + "");
                allFinishText.setText(signinRelativeInfoDTO.getTotal_signin_count() + "");
                final SpecialInfo specialInfo = signinRelativeInfoDTO.getSpecial_info();
                todayFinishPerson.setText("今日统计：" + specialInfo.getSame_signin_user_count() + "" + "人 完成 ");
                taskNameText.setText("#" + taskName);
                channelId = signinRelativeInfoDTO.getChannel_id();
                if (channelId != 0) {
                    taskNameText.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ChannelProfileFragment.start(getActivity(), null, channelId);
                        }
                    });
                }


                //1.4-------------------------------------
                if (signinRelativeInfoDTO.getVideos() == null || signinRelativeInfoDTO.getVideos().size() == 0) {
                    hasTaskCourse.setVisibility(View.GONE);
                    notHasTaskCourse.setVisibility(View.VISIBLE);
                } else {
                    final List<Video> videos = signinRelativeInfoDTO.getVideos();

                    Video video = signinRelativeInfoDTO.getVideos().get(0);
                    for (int i = 0; i < videos.size(); i++) {//如果有mentor的视频，则取mentor的第一个视频，否则取第一个
                        if (videos.get(i).getUser_id() == UserModel.getInstance().getUserDto().getUser().getMentor_id()) {
                            video = signinRelativeInfoDTO.getVideos().get(i);
                            break;
                        }
                    }
                    hasTaskCourse.setVisibility(View.VISIBLE);
                    notHasTaskCourse.setVisibility(View.GONE);
                    QiniuHelper.bindVideoThumbnaiImage(video.getVideo_link(), taskCourseImg);
                    taskCourseImg.setTag(video);
                    taskCourseImg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            if (NetUtils.isWifiConnection(getActivity())) {
                                Intent intents = new Intent(Intent.ACTION_VIEW);
                                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(((Video) view.getTag()).getVideo_link())), "video/*");
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
                                        intents.setDataAndType(Uri.parse(QiniuHelper.getVideoPlayUrl(((Video) view.getTag()).getVideo_link())), "video/*");
                                        startActivity(intents);
                                    }
                                });
                                AlertHelper.showAlert(getActivity(), params);
                            }
                        }
                    });
                    taskCourseNameText.setText(video.getUser_nick_name() + "的教程");
                    taskAuthorNameText.setText("提供者：" + video.getUser_nick_name());
                    taskCourseDurationText.setText(TimeHelper.secToTime(video.getDuration()));
                    moreTaskCourseBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AllTaskCourseFragment.start(getActivity(), videos);
                        }
                    });
                }
                //----------------------------------------

                int maxWeight = 0;
                for (int i = 0; i < specialInfo.getSame_signin_user_info().size(); i++) {
                    maxWeight += specialInfo.getSame_signin_user_info().get(i).getUser_count();
                }
                for (int i = 0; i < specialInfo.getSame_signin_user_info().size(); i++) {
                    final SameSigninUserInfoDTO sameSigninUserInfoDTO = specialInfo.getSame_signin_user_info().get(i);
                    theyAlsoDoOutLayout.setVisibility(View.VISIBLE);
                    View view = View.inflate(getActivity(), R.layout.special_info_item, null);
                    LinearLayout sumLayout = (LinearLayout) view.findViewById(R.id.sum_layout);
                    sumLayout.setWeightSum(maxWeight);
                    sumLayout.setGravity(Gravity.CENTER_VERTICAL);
                    View view1 = view.findViewById(R.id.person_num_view);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            0,
                            Screen.dip2px(5), sameSigninUserInfoDTO.getUser_count());
                    view1.setLayoutParams(param);
                    TextView tv1 = (TextView) view.findViewById(R.id.action_text);
                    tv1.setText(sameSigninUserInfoDTO.getString1());
                    TextView tv2 = (TextView) view.findViewById(R.id.tag_text);
                    tv2.setText(sameSigninUserInfoDTO.getString2());
                    TextView tv3 = (TextView) view.findViewById(R.id.person_num_text);
                    tv3.setText(sameSigninUserInfoDTO.getUser_count() + "人");
                    ImageView imageView = (ImageView) view.findViewById(R.id.info_img);
                    if (!TextUtils.isEmpty(sameSigninUserInfoDTO.getImg())) {
                        QiniuHelper.bindImage(sameSigninUserInfoDTO.getImg(), imageView);
                    }
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (sameSigninUserInfoDTO.getLink().contains("channel")) {
                                ChannelProfileFragment.start(getActivity(), null, Integer.valueOf(sameSigninUserInfoDTO.getLink().split("=")[1]));
                            } else if (sameSigninUserInfoDTO.getLink().contains("signin")) {
                                SigninAndPostDetailFragment.start(getActivity(), Integer.valueOf(sameSigninUserInfoDTO.getLink().split("=")[1]));
                            } else if (sameSigninUserInfoDTO.getLink().contains("profile")) {
                                UserProfileFragment.start(getActivity(), Integer.valueOf(sameSigninUserInfoDTO.getLink().split("=")[1]));
                            }
                        }
                    });
                    theyAlsoDoLayout.addView(view);
                }
                for (int i = 0; i < signinRelativeInfoDTO.getGeneral_info().size(); i++) {
                    List<GeneralInfo> list = signinRelativeInfoDTO.getGeneral_info().get(i);
                    View generalView = View.inflate(getActivity(), R.layout.general_info_layout_item, null);
                    LinearLayout layout = (LinearLayout) generalView.findViewById(R.id.general_info_item);
                    TextView textView = (TextView) generalView.findViewById(R.id.general_info_title_text);
                    textView.setText(((GeneralInfo) signinRelativeInfoDTO.getGeneral_info().get(i).get(0)).getSection());
                    for (int j = 0; j < list.size(); j++) {
                        final GeneralInfo generalInfo = list.get(j);
                        View view = View.inflate(getActivity(), R.layout.signin_card_bottom_item, null);
                        TextView tv1 = (TextView) view.findViewById(R.id.card_bottom_text1);
                        tv1.setText(generalInfo.getString1());
                        TextView tv2 = (TextView) view.findViewById(R.id.card_bottom_text2);
                        tv2.setText(generalInfo.getString2());
                        ImageView imageView = (ImageView) view.findViewById(R.id.card_bottom_img);
                        if (generalInfo.getImg() != null) {
                            QiniuHelper.bindImage(generalInfo.getImg(), imageView);
                        }
                        view.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (generalInfo.getLink().contains("channel")) {
                                    ChannelProfileFragment.start(getActivity(), null, Integer.valueOf(generalInfo.getLink().split("=")[1]));
                                } else if (generalInfo.getLink().contains("signin")) {
                                    SigninAndPostDetailFragment.start(getActivity(), Integer.valueOf(generalInfo.getLink().split("=")[1]));
                                } else if (generalInfo.getLink().contains("profile")) {
                                    UserProfileFragment.start(getActivity(), Integer.valueOf(generalInfo.getLink().split("=")[1]));
                                }
                            }
                        });
                        layout.addView(view);
                    }
                    judgeYouLike.addView(generalView);
                }


            }
        });


        return rootView;
    }


    public void setView() {
        if (is_signin) {
            goTakeNoteBtn.setClickable(false);
        } else {
            goTakeNoteBtn.setClickable(true);
            goTakeNoteBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel.getInstance().isFromGroupSignin = false;
                    if ("body_weight".equals(signinType)) {
                        AddTaskWeightFragment.start(getActivity(), taskName, "");
                    } else if ("image".equals(signinType)) {
                        SignAddContentFragment.start(getActivity(), taskName, "", "", true);
                    } else if ("step".equals(signinType)) {
                        int stepLimit = TaskDBModel.getInstance().getStepsByName(taskName);
                        if (UserStepModel.getInstance().getUserTodayStep() >= stepLimit) {
                            SignAddContentFragment.start(getActivity(), taskName, "", "", false);
                        } else {
                            showToast("未完成目标步数");
                        }
                    } else {
                        SignAddContentFragment.start(getActivity(), taskName, "", "", false);
                    }
                }
            });
        }
        if (is_take_note) {
            seeMyMomentLayout.setVisibility(View.VISIBLE);
            seeMyMomentLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SigninAndPostDetailFragment.start(getActivity(), signinId);
                }
            });
            goTakeNoteBtn.setImageResource(R.mipmap.check_button_red);
        } else {
            if (is_signin) {
                goTakeNoteBtn.setImageResource(R.mipmap.check_button_red);
                takeNoteLayout.setVisibility(View.VISIBLE);
                takeNoteLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SignAddContentFragment.start(getActivity(), taskName, "", "", false, signinId);
                    }
                });
            } else {
                if ("body_weight".equals(signinType)) {
                    signWeightFlagLayout.setVisibility(View.VISIBLE);
                } else if ("image".equals(signinType)) {
                    signImgFlagLayout.setVisibility(View.VISIBLE);
                } else if ("step".equals(signinType)) {
                    signStepFlagLayout.setVisibility(View.VISIBLE);
                    int stepLimit = TaskDBModel.getInstance().getStepsByName(taskName);
                    stepText.setText("已完成：" + UserStepModel.getInstance().getUserTodayStep() + "步   目标：" + stepLimit + "步");
                } else {
                    signOnceFlagLayout.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public void onEventMainThread(JustSigninEvent justSigninEvent) {
        is_signin = true;
        signinId = justSigninEvent.getSignin_id();
        setView();
    }

    public void initTitleView() {
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText(taskName);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_my_task_course:
            case R.id.upload_task_course:
                if (videoPickDialog == null) {
                    createVideoPickDialog();
                }
                videoPickDialog.show();
                break;
        }
    }

    public void createVideoPickDialog() {
        videoPickDialog = new VideoPickDialog(getActivity(), SignTaskLinkedFragment.this);
        videoPickDialog.setCallback(new VideoPickDialog.Callback() {
            @Override
            public void onImageBack(final ResInformationDto resInformationDto) {
                if (resInformationDto.getDuration() >= 5) {
                    if (NetUtils.isWifiConnection(getActivity())) {
                        upLoadCourse(resInformationDto);
                    } else {
                        AlertHelper.AlertParams params = new AlertHelper.AlertParams();
                        params.setTitle("您当前处于非Wifi网络环境下,上传可能产生大量流量费用,是否继续上传");
                        params.setCancelString("取消");
                        params.setConfirmString("确定");
                        params.setCancelListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        params.setConfirmListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                upLoadCourse(resInformationDto);
                            }
                        });
                        AlertHelper.showAlert(getActivity(), params);
                    }
                } else {
                    showToast("您选择的视频时长小于5s,请重新选择");
                }
            }
        });
    }

    public void upLoadCourse(final ResInformationDto resInformationDto) {
        View view = View.inflate(getActivity(), R.layout.upload_process_window, null);

        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setContentView(view);
        final ProgressBar upLoadProcress = (ProgressBar) view.findViewById(R.id.upload_course_progress);


        QiniuHelper.uploadVideo(UserModel.getInstance().getUserId(), resInformationDto.getFilePath(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    API.uploadVideo(UserModel.getInstance().getUserId(), UserModel.getInstance().getUserNickName(), QiniuHelper.getVideoAndThumbnai(key), resInformationDto.getDuration(), taskName, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            showToast("上传成功");
                            Log.i("logger", " upload success");
                        }
                    });
                }
            }
        }, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                upLoadProcress.setProgress((int) (percent * 1000));
                if (percent == 1) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (videoPickDialog == null) {
            videoPickDialog = new VideoPickDialog(getActivity(), SignTaskLinkedFragment.this);
        }
        videoPickDialog.onActivityResult(requestCode, resultCode, data);
    }
}
