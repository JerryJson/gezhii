package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.UserDataChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.KeyBoardHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.PickerView;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/18.
 */
public class MyPersonInfoFragment extends BaseFragment {
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
    @InjectView(R.id.user_profile_icon)
    RectImageView userProfileIcon;
    @InjectView(R.id.head_layout)
    LinearLayout headLayout;
    @InjectView(R.id.nickname_text)
    TextView nicknameText;
    @InjectView(R.id.nickname_layout_btn)
    LinearLayout nicknameLayoutBtn;
    @InjectView(R.id.age_text)
    TextView ageText;
    @InjectView(R.id.age_layout_btn)
    LinearLayout ageLayoutBtn;
    @InjectView(R.id.job_text)
    TextView jobText;
    @InjectView(R.id.job_layout_btn)
    LinearLayout jobLayoutBtn;
    @InjectView(R.id.sport_frequence_text)
    TextView sportFrequenceText;
    @InjectView(R.id.sport_frequence_layout_btn)
    LinearLayout sportFrequenceLayoutBtn;
    @InjectView(R.id.sport_way_text)
    TextView sportWayText;
    @InjectView(R.id.sport_way_layout_btn)
    LinearLayout sportWayLayoutBtn;
    @InjectView(R.id.sport_description_input)
    EditText sportDescriptionInput;
    @InjectView(R.id.write_num_of_words_text)
    TextView writeNumOfWordsText;
    @InjectView(R.id.person_info_scrollView)
    ScrollView personInfoScrollView;
    @InjectView(R.id.cancle)
    TextView cancle;
    @InjectView(R.id.sure)
    TextView sure;
    @InjectView(R.id.item_pv)
    PickerView itemPv;
    @InjectView(R.id.picker_layout)
    RelativeLayout pickerLayout;

    private String age_description;
    private String job;
    private String sport_frequency;
    private String sport_way;
    private String self_description;
    private String nick_name;
    ImagePickDialog imagePickDialog;

    private User user;

    private String selectText;
    private int itemType = 0;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.my_person_info_id = mainActivity.showNext(MyPersonInfoFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_person_info_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        EventBus.getDefault().register(this);
        setView();
        return rootView;
    }

    public void onEvent(UserDataChangeEvent userDataChangeEvent) {
        setView();
    }

    public void setView() {
        user = UserModel.getInstance().getUserDto().getUser();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardHelper.hideKeyBoard(getActivity(), sportDescriptionInput);
                finish();
            }
        });
        titleText.setText("个人信息");
        rightText.setText("提交");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age_description = ageText.getText().toString();
                job = jobText.getText().toString();
                sport_frequency = sportFrequenceText.getText().toString();
                sport_way = sportWayText.getText().toString();
                self_description = sportDescriptionInput.getText().toString();
                nick_name = nicknameText.getText().toString();
                EventBus.getDefault().post(new ShowLoadingEvent());
                API.updateUserProfileHttp(UserModel.getInstance().getUserId(), null, nick_name, 0, age_description, job, sport_frequency, sport_way, self_description,
                        new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                EventBus.getDefault().post(new CloseLoadingEvent());
                                UserModel.getInstance().updateUserDto(UserDto.parserJson(response));
                                PreviewMyPersonInfoFragment.start(getActivity());
                            }
                        });

            }
        });
        QiniuHelper.bindAvatarImage(UserModel.getInstance().getUserIcon(), userProfileIcon);
        nicknameText.setText(user.getNick_name());
        ageText.setText(user.getAge_description());
        jobText.setText(user.getJob());
        sportFrequenceText.setText(user.getSport_frequency());
        sportWayText.setText(user.getSport_way());
        if (!TextUtils.isEmpty(user.getSelf_description())) {
            sportDescriptionInput.setText(user.getSelf_description());
        }

        headLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePickDialog == null) {
                    createImagePickDialog();
                }
                imagePickDialog.show();
            }
        });
        nicknameLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickNameDialog();
            }
        });
        sportDescriptionInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(144)});//限制输入字数数目

        sportDescriptionInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                personInfoScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                writeNumOfWordsText.setText(length + "/144");
            }
        });

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerLayout.setVisibility(View.GONE);
                if (selectText.equals("自定义")) {
                    if (itemType == 1) {
                        showCustomerDialog(ageText);
                    } else if (itemType == 2) {
                        showCustomerDialog(jobText);
                    } else if (itemType == 3) {
                        showCustomerDialog(sportFrequenceText);
                    } else if (itemType == 4) {
                        showCustomerDialog(sportWayText);
                    }
                } else {
                    if (itemType == 1) {
                        ageText.setText(selectText);
                    } else if (itemType == 2) {
                        jobText.setText(selectText);
                    } else if (itemType == 3) {
                        sportFrequenceText.setText(selectText);
                    } else if (itemType == 4) {
                        sportWayText.setText(selectText);
                    }
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerLayout.setVisibility(View.GONE);
            }
        });
        ageLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ages = {"自定义", "00后", "90后", "80后", "70后"};
                // showMyDialog("年龄", ages, ageText);
                showMyPickerView(ageText.getText().toString(), ages, 1);
            }
        });
        jobLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] jobs = {"自定义", "学生", "创业公司成员", "外企从业者", "国企从业者", "私企从业者", "公务员", "IT从业者"};
                //showMyDialog("职业", jobs, jobText);
                showMyPickerView(jobText.getText().toString(), jobs, 2);
            }
        });
        sportFrequenceLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] frequences = {"自定义", "每天一次", "隔天一次", "每周2，3次", "每周一次"};
                //showMyDialog("运动频率", frequences, sportFrequenceText);
                showMyPickerView(sportFrequenceText.getText().toString(), frequences, 3);
            }
        });
        sportWayLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] ways = {"自定义", "户外跑步", "健身房", "室内健身", "有氧运动为主",};
                // showMyDialog("运动方式", ways, sportWayText);
                showMyPickerView(sportWayText.getText().toString(), ways, 4);
            }
        });
    }

    //imagePickDialog就是那个有3个按钮的ActionSheet, "相册", "相机", "取消"
    private void createImagePickDialog() {
        imagePickDialog = new ImagePickDialog(MyPersonInfoFragment.this);
        imagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    String icon = QiniuHelper.QINIU_SPACE + "_" + key;
                                    Log.i("xianrui", "push_icon_name " + icon);
                                    API.updateUserProfileHttp(UserModel.getInstance().getUserId(),
                                            icon, null, 0, null, null, null, null, null, new OnRequestEnd() {
                                                @Override
                                                public void onRequestSuccess(String response) {
                                                    UserModel.getInstance().tryLoadRemote(true);
                                                    setView();
                                                }

                                                @Override
                                                public void onRequestFail(VolleyError error) {

                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从系统相机回来, dialog可能被销毁. 为了解决这个bug, 重建dialog
        //同时在manifest里面,设置了main activity的属性
        //android:configChanges="orientation|keyboardHidden|screenSize"
        if (imagePickDialog == null) {
            createImagePickDialog();
        }
        imagePickDialog.onActivityResult(requestCode, resultCode, data);
    }

    public void editNickNameDialog() {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setCancelString("取消");
        alertParams.setConfirmString("确定");
        alertParams.setTitle("修改昵称");
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setText(UserModel.getInstance().getUserDto().getUser().getNick_name());
        editText.setSelection(editText.getText().length());
        alertParams.setView(editText);
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if ("".equals(editText.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    nicknameText.setText(editText.getText().toString());
                    dialog.dismiss();
//                    EventBus.getDefault().post(new ShowLoadingEvent());
//                    API.updateUserProfileHttp(UserModel.getInstance().getUserId(), null, editText.getText().toString(), 0, null, null, null, null, null, new APICallBack() {
//                        @Override
//                        public void subRequestSuccess(String response) throws NoSuchFieldException {
//                            Log.i("darren", response);
//                            EventBus.getDefault().post(new CloseLoadingEvent());
//                            dialog.dismiss();
//
//                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
//                            UserModel.getInstance().updateUserDto(UserDto.parserJson(response));
//                        }
//                    });
                }

            }
        });
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 300);

    }

    public void showMyDialog(final String title, final String[] strings, final TextView textView) {

        final String chooseName;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.my_person_info_dialog_view, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);
        final EditText editText = (EditText) view.findViewById(R.id.custom_input);
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(strings[which]);
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String customInput = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(customInput)) {
                    textView.setText(customInput);
                } else {
                    showToast("请选择或自定义" + title);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void showCustomerDialog(final TextView textView) {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setCancelString("取消");
        alertParams.setConfirmString("确定");
        alertParams.setTitle("输入自定义内容");
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setText(textView.getText().toString());
        editText.setSelection(editText.getText().length());
        alertParams.setView(editText);
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if ("".equals(editText.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "自定义内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    textView.setText(editText.getText().toString());
                    dialog.dismiss();
                }

            }
        });
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 300);
    }

    public void showMyPickerView(String text, final String[] strings, int which) {
        selectText = text;
        itemType = which;
        pickerLayout.setVisibility(View.VISIBLE);
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < strings.length; i++) {
            data.add(strings[i]);
        }
        itemPv.setData(data);
        selectText = data.get(data.size() / 2);
        itemPv.setOnSelectListener(new PickerView.onSelectListener() {

            @Override
            public void onSelect(String text) {
                selectText = text;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
