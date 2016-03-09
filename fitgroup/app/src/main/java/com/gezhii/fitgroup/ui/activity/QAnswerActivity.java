package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/9.
 */
public class QAnswerActivity extends Activity {
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

    private Map<String, String> map;
    private Map<String, Object> nextMap;
    private Handler qHandler;
    private int rNum = 0;
    private int lNum = 1;
    private int bNum = 1;
    private Boolean isRightButtonPress = false;
    private String[] next;
    private int clickNum = 0;
    private int questionNum ;
    public static final String TAG_HAVE_ANSWER = "tag_have_answer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.q_answer_activity);
        ButterKnife.inject(this);
        qHandler = new Handler();
        initTitle();
        initData();
        // initView();
        initQAnswerView();

    }

    public void initTitle() {
        backBtn.setVisibility(View.GONE);
        titleText.setText("小Q问答");
        rightText.setText("登录");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveAnswerd();
                Intent goLoginAct = new Intent(QAnswerActivity.this, RegisterAndLoginActivity.class);
                startActivity(goLoginAct);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserModel.getInstance().isLogin()) {
            Intent goMainAct = new Intent(this, MainActivity.class);
            startActivity(goMainAct);
            finish();
        }
    }

    private void haveAnswerd() {
        SharedPreferences sharedPreferences = getSharedPreferences("fitGroup", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TAG_HAVE_ANSWER, true);
        editor.commit();
    }

    public void initData() {
        map = new HashMap<String, String>();
        map.put("q1", "欢迎进入轻元素的世界，我是领航员小Q");
        map.put("q2", "那就没错，轻星人是你最好的减肥小伙伴，轻星人对待减肥这项伟大的事业都很认真，有着正确的三观，“不吃药，科学饮食，坚持运动健身”");
        map.put("q3", "真的没错，轻星上有好多健身减肥公会！加入公会，跟轻星人做朋友，就能收集轻元素，你也能成为轻星人。那可是以马甲线/人鱼线 出名的宇宙种族哦");
        map.put("q4", "不理解没关系，等你明白“和认真的人一起，才能真的减肥”，你自然会回来跟我说，“我也要变成轻星人！”");


        map.put("a1", "什么东东？我是来找人一起减肥的！");
        map.put("a2", "什么轻星人？莫名奇妙，我真的是来找人一起减肥的！");
        map.put("a3", "什么东西啊，救命啊，我要叫警察了，我只是来找人一起减肥的！");
        map.put("a4", " 不知所谓，快点消失！");


        nextMap = new HashMap<String, Object>();
        nextMap.put("a0", "q1");
        nextMap.put("q1", "a1");
        nextMap.put("a1", "q2");
        nextMap.put("q2", "a2");
        nextMap.put("a2", "q3");
        nextMap.put("q3", "a3");
        nextMap.put("a3", "q4");
        nextMap.put("q4", "a4");
    }

    public void initQAnswerView() {
        View leftLayoutFir = LayoutInflater.from(this).inflate(R.layout.q_answer_left_item, null);
        final View bottomViewFir = LayoutInflater.from(this).inflate(R.layout.q_answer_bottom_item, null);
        LeftViewHolder leftViewHolder = new LeftViewHolder(leftLayoutFir);
        final BottomViewHolder bottomViewHolder = new BottomViewHolder(bottomViewFir);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final LinearLayout.LayoutParams emptyViewlayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        emptyViewlayoutParams.weight = 1;
        leftViewHolder.sayLeftText.setText(map.get("q1"));
        sayListLayout.addView(leftLayoutFir);
        bottomViewHolder.sayBottomText.setText(map.get("a1"));
        bottomLayout.addView(bottomViewFir, layoutParams);
        next = new String[2];
        next[0] = "q1";
        MobclickAgent.onEvent(QAnswerActivity.this, "guide_dialog", UmengEvents.getEventMap("step", "question 1"));
        bottomViewFir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View emptyLeftView = new View(QAnswerActivity.this);
                View emptyRightView = new View(QAnswerActivity.this);
                if (next[0].equals("q4")) {
                    haveAnswerd();
                    MobclickAgent.onEvent(QAnswerActivity.this, "guide_dialog", UmengEvents.getEventMap("step", "answer 4"));
                    Intent goMainIntent = new Intent(QAnswerActivity.this, MainActivity.class);
                    startActivity(goMainIntent);
                    finish();
                } else {
                    clickNum++;
                    questionNum =clickNum+1;
                    MobclickAgent.onEvent(QAnswerActivity.this, "guide_dialog", UmengEvents.getEventMap("step", "answer " + clickNum));
                    View rightLayout = LayoutInflater.from(QAnswerActivity.this).inflate(R.layout.q_answer_right_item, null);
                    RightViewHolder rightViewHolder = new RightViewHolder(rightLayout);
                    rightViewHolder.sayRightText.setText(map.get(nextMap.get(next[0])));
                    bottomViewHolder.sayBottomText.setVisibility(View.INVISIBLE);
                    bottomViewFir.setVisibility(View.GONE);
                    sayListLayout.addView(rightLayout);

                    qHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next[0] = nextMap.get(next[0]).toString();
                            View leftLayout = LayoutInflater.from(QAnswerActivity.this).inflate(R.layout.q_answer_left_item, null);
                            LeftViewHolder leftViewHolder = new LeftViewHolder(leftLayout);
                            leftViewHolder.sayLeftText.setText(map.get(nextMap.get(next[0])));
                            sayListLayout.addView(leftLayout);
                            MobclickAgent.onEvent(QAnswerActivity.this, "guide_dialog", UmengEvents.getEventMap("step", "question " + questionNum));
                        }
                    }, 600);


                    qHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            next[0] = nextMap.get(next[0]).toString();
                            bottomViewFir.setVisibility(View.VISIBLE);
                            bottomViewHolder.sayBottomText.setVisibility(View.VISIBLE);
                            bottomViewHolder.sayBottomText.setText(map.get(nextMap.get(next[0])));
                        }
                    }, 1400);
                    qHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sayListScroll.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }, 800);
                }
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
