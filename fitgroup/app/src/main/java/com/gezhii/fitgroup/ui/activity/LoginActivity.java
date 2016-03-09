package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.model.UserModel;
import com.tencent.bugly.crashreport.CrashReport;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by xianrui on 15/10/20.
 */
public class LoginActivity extends Activity {
    @InjectView(R.id.login)
    Button login;
    @InjectView(R.id.input_third_id)
    EditText inputThirdId;
    @InjectView(R.id.input_third_name)
    EditText inputThirdName;
    @InjectView(R.id.input_third_login_type)
    EditText inputThirdLoginType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.inject(this);
    }


    @OnClick(R.id.login)
    public void onLoginClick(View v) {
        String type = "qq";
        String name = "xianrui";
        String id = "10086";
        if (inputThirdId.getText().toString().trim().length() > 0) {
            id = inputThirdId.getText().toString().trim();
        }
        if (inputThirdName.getText().toString().trim().length() > 0) {
            name = inputThirdName.getText().toString().trim();
        }

        if (inputThirdLoginType.getText().toString().trim().length() > 0) {
            type = inputThirdLoginType.getText().toString().trim();
        }

        API.loginHttp(type, id, name, "000","", new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                UserDto userDto = UserDto.parserJson(response);
                UserModel.getInstance().updateUserDto(userDto);
                registerChatServer();
            }
        });
    }

    public void registerChatServer() {
        EMChatManager.getInstance().login(UserModel.getInstance().getUserHuanXinName()
                , UserModel.getInstance().getUserDto().getUser().getHuanxin_password()
                , new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        Log.d(getClass().getSimpleName(), "登陆聊天服务器成功！");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Log.d(getClass().getSimpleName(), "registerChatServer onError " + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }


}
