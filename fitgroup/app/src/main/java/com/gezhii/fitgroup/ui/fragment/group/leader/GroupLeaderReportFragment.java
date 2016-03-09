package com.gezhii.fitgroup.ui.fragment.group.leader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupLeaderDialogDTO;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/10.
 */
public class GroupLeaderReportFragment extends BaseFragment {
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
    @InjectView(R.id.say_list_layout)
    LinearLayout sayListLayout;
    @InjectView(R.id.say_list_scroll)
    ScrollView sayListScroll;
    @InjectView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    private ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
    private int onClickNum = 0;
    private Handler pHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.president_briefing_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("fitGroup", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("briefingDate", TimeHelper.getInstance().getTodayString());
        editor.commit();
        initTitle();
        pHandler = new Handler();
        return rootView;
    }

    public void initTitle() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText("会长简报");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        User userLeader = UserModel.getInstance().getUserDto().getGroup().getLeader();
        API.getGroupLeaderDialog(userLeader.getId(), UserModel.getInstance().getGroupId(), new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                GroupLeaderDialogDTO groupLeaderDialogDTO = new GroupLeaderDialogDTO();
                groupLeaderDialogDTO = GroupLeaderDialogDTO.parserJson(response);
                List<GroupLeaderDialogDTO.DialogContent> list = groupLeaderDialogDTO.getDialog();
                initChatDialogView(list);
            }
        });

    }


    public void initChatDialogView(final List<GroupLeaderDialogDTO.DialogContent> list) {
        View leftFirstView = LayoutInflater.from(getActivity()).inflate(R.layout.president_briefing_left_item, null);

        View bottomFirstView = LayoutInflater.from(getActivity()).inflate(R.layout.president_briefing_bottom_item, null);
        LeftViewHolder leftViewHolder = new LeftViewHolder(leftFirstView);

        final BottomViewHolder bottomViewHolder = new BottomViewHolder(bottomFirstView);
        HashMap<String, Object> qHashMap = new HashMap<String, Object>();
        qHashMap = list.get(0).q;
        leftViewHolder.sayLeftText.setText(qHashMap.get("content").toString());
        sayListLayout.addView(leftFirstView);
        LinearLayout.LayoutParams emptylayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        emptylayoutParams.weight = 1;
        LinearLayout.LayoutParams bottomlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomViewHolder.sayBottomText.setText(list.get(0).a);
        View emptyLeftView = new View(getActivity());
        View emptyRightView = new View(getActivity());
        bottomLayout.addView(emptyLeftView, emptylayoutParams);
        bottomLayout.addView(bottomFirstView, bottomlayoutParams);
        bottomLayout.addView(emptyRightView, emptylayoutParams);
        bottomFirstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rightView = LayoutInflater.from(getActivity()).inflate(R.layout.president_briefing_right_item, null);
                RightViewHolder rightViewHolder = new RightViewHolder(rightView);
                if (null != list.get(onClickNum).a) {
                    QiniuHelper.bindImage(UserModel.getInstance().getUserIcon(), rightViewHolder.headImg);
                    rightViewHolder.sayRightText.setText(list.get(onClickNum).a);
                    sayListLayout.addView(rightView);
                    bottomViewHolder.sayBottomText.setVisibility(View.GONE);
                }
                onClickNum++;
                while (list.get(onClickNum).a == null) {
                    final View leftNewView = LayoutInflater.from(getActivity()).inflate(R.layout.president_briefing_left_item, null);
                    LeftViewHolder leftNewViewHolder = new LeftViewHolder(leftNewView);
                    if ("1.0".equals(list.get(onClickNum).q.get("type").toString().trim())) {
                        LinkedTreeMap<String, Object> contentMap = (LinkedTreeMap) list.get(onClickNum).q.get("content");
                        String content = contentMap.get("title").toString() + ":";
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList = (ArrayList) contentMap.get("list");
                        StringBuilder names = null;
                        if (arrayList.size() != 0) {
                            for (int i = 0; i < arrayList.size(); i++) {
                                names.append(arrayList.get(i));
                                names.append(" ");
                            }
                            leftNewViewHolder.sayLeftText.setText(content + names);
                        } else {
                            String name = "无未打卡人员";
                            leftNewViewHolder.sayLeftText.setText(content + name);
                        }
                        pHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sayListLayout.addView(leftNewView);
                            }
                        }, 500);

                    } else {
                        leftNewViewHolder.sayLeftText.setText(list.get(onClickNum).q.get("content").toString());
                        pHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sayListLayout.addView(leftNewView);
                            }
                        }, 500);

                    }
                    if (onClickNum != list.size() - 1) {
                        onClickNum++;
                    } else {
                        break;
                    }
                }
                if (list.get(onClickNum).a != null) {
                    final View leftView = LayoutInflater.from(getActivity()).inflate(R.layout.president_briefing_left_item, null);
                    LeftViewHolder leftViewHolder = new LeftViewHolder(leftView);
                    if (null != list.get(onClickNum).q && null != list.get(onClickNum).q.get("content")) {
                        if ("1.0".equals(list.get(onClickNum).q.get("type").toString().trim())) {
                            LinkedTreeMap<String, Object> contentMap = (LinkedTreeMap) list.get(onClickNum).q.get("content");
                            String content = contentMap.get("title").toString() + ":";
                            ArrayList<String> arrayList = new ArrayList<String>();
                            arrayList = (ArrayList) contentMap.get("list");
                            StringBuilder names = null;
                            if (arrayList.size() != 0) {
                                for (int i = 0; i < arrayList.size(); i++) {
                                    names.append(arrayList.get(i));
                                    names.append(" ");
                                }
                                leftViewHolder.sayLeftText.setText(content + names);
                            } else {
                                String name = "无未打卡人员";
                                leftViewHolder.sayLeftText.setText(content + name);
                            }
                            pHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sayListLayout.addView(leftView);
                                }
                            }, 500);

                        } else {
                            leftViewHolder.sayLeftText.setText(list.get(onClickNum).q.get("content").toString());
                            pHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sayListLayout.addView(leftView);
                                }
                            }, 500);

                        }
                    } else {
                        leftViewHolder.sayLeftText.setVisibility(View.GONE);
                    }
                }
                if (list.get(onClickNum).a != null) {
                    pHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bottomViewHolder.sayBottomText.setVisibility(View.VISIBLE);
                            bottomViewHolder.sayBottomText.setText(list.get(onClickNum).a);
                        }
                    }, 800);

                } else {
                    bottomViewHolder.sayBottomText.setVisibility(View.GONE);
                }
                pHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sayListScroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 700);
            }
        });
    }


    static class LeftViewHolder {
        @InjectView(R.id.say_left_text)
        TextView sayLeftText;

        LeftViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    static class RightViewHolder {
        @InjectView(R.id.head_img)
        ImageView headImg;
        @InjectView(R.id.say_right_text)
        TextView sayRightText;

        RightViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    static class BottomViewHolder {
        @InjectView(R.id.say_bottom_text)
        TextView sayBottomText;

        BottomViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
