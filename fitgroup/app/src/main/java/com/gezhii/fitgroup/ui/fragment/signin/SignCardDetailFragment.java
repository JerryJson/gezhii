package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.LikeSigninMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.QrHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.BigPhotoDialog;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.xianrui.lite_common.litesuits.common.utils.BitmapUtil;

import java.util.HashMap;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/3.
 */
public class SignCardDetailFragment extends BaseFragment {

    public static final String TAG_SIGN_ID = "tag_sign_id";
    public static final String TAG_HUANXIN_ID = "tag_huanxin_id";


    int sign_id;
    String user_huanxin_id;
    ShareToThirdDialog mShareToThirdDialog;

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
    @InjectView(R.id.user_icon_img)
    RectImageView userIconImg;
    @InjectView(R.id.user_name_text)
    TextView userNameText;
    @InjectView(R.id.sign_date_text)
    TextView signDateText;
    @InjectView(R.id.sign_description_text)
    TextView signDescriptionText;
    @InjectView(R.id.sign_img)
    ImageView signImg;
    @InjectView(R.id.sign_card_small_icon_img)
    ImageView signCardSmallIconImg;
    @InjectView(R.id.sign_task_name_text)
    TextView signTaskNameText;
    @InjectView(R.id.sign_like_img)
    ImageView signLikeImg;
    @InjectView(R.id.sign_count_text)
    TextView signCountText;
    @InjectView(R.id.share_to_third_img)
    ImageView shareToThirdImg;
    @InjectView(R.id.save_picture_text)
    TextView savePictureText;
    //    @InjectView(R.id.my_association_number_text)
//    TextView myAssociationNumberText;
//    @InjectView(R.id.kg_text)
//    TextView kgText;
//    @InjectView(R.id.lose_weight_value_text)
//    TextView loseWeightValueText;
//    @InjectView(R.id.my_group_layout)
//    RelativeLayout myGroupLayout;
//    @InjectView(R.id.app_icon_img)
//    ImageView appIconImg;
    @InjectView(R.id.qrcode_img)
    ImageView qrcodeImg;

    @InjectView(R.id.share_bottom_layout)
    public LinearLayout shareBottomLayout;
    @InjectView(R.id.share_to_third_layout)
    public LinearLayout shareToThirdLayout;
    @InjectView(R.id.share_btn_layout)
    public LinearLayout shareBtnLayout;

    @InjectView(R.id.task_layout)
    LinearLayout taskLayout;
    @InjectView(R.id.private_chat_btn)
    LinearLayout privateChatBtn;
    @InjectView(R.id.share_to_third_btn)
    LinearLayout shareToThirdBtn;
    @InjectView(R.id.qrcode_layout)
    LinearLayout qrcodeLayout;

    public static void start(Activity activity, int sign_id, String huanxin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_SIGN_ID, sign_id);
        params.put(TAG_HUANXIN_ID, huanxin_id);
        mainActivity.showNext(SignCardDetailFragment.class, params);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_card_detail_fragment, null);
        ButterKnife.inject(this, rootView);

        sign_id = (int) getNewInstanceParams().get(TAG_SIGN_ID);
        user_huanxin_id = (String) getNewInstanceParams().get(TAG_HUANXIN_ID);

        setView();
        shareToThirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (UserModel.getInstance().getMyGroup() != null && UserModel.getInstance().getMyGroup().getGroup_name() != null) {
//                    String groupName = UserModel.getInstance().getMyGroup().getGroup_name();
//                    myAssociationNumberText.setText("我的公会" + "\"" + groupName + "\"" + "累计减脂");
//                    loseWeightValueText.setText("" + UserModel.getInstance().getMyGroup().getTotal_weight_reduction());
//                } else {
//                    myGroupLayout.setVisibility(View.GONE);
//                }
                if (mShareToThirdDialog == null) {
                    mShareToThirdDialog = new ShareToThirdDialog(getActivity(), SignCardDetailFragment.this);
                }
                mShareToThirdDialog.show();
            }
        });
        privateChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivateChatFragment.start(getActivity(), user_huanxin_id);
            }
        });
        return rootView;
    }

    private void setView() {
        shareBottomLayout.setVisibility(View.INVISIBLE);

        String text = "http://dl.qing.am?type=signin";
        String qrcodePath = QrHelper.createImage(getActivity(), text, UUID.randomUUID().toString());
        qrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(qrcodePath));
        final SigninDto signinDto = SignCacheModel.getInstance().getSign(sign_id);

        rightText.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (signinDto != null) {
            //设置导航栏标题
            if (signinDto.getSignin().getSignin_type() == 1) {
                titleText.setText("请假条");
            } else {
                titleText.setText("打卡详情");
            }

            //头像,昵称,打卡时间
            String icon = UserCacheModel.getInstance().getUserInfo(user_huanxin_id).icon;
            final String nick_name = UserCacheModel.getInstance().getUserInfo(user_huanxin_id).nickName;
            if (!TextUtils.isEmpty(icon)) QiniuHelper.bindImage(icon, userIconImg);
            userNameText.setText(nick_name);
            signDateText.setText(TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));

            //描述
            if (!TextUtils.isEmpty(signinDto.getSignin().getDescription())) {
                signDescriptionText.setText(signinDto.getSignin().getDescription());
            } else {
                signDescriptionText.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(signinDto.getSignin().getImg())) {
                signImg.setVisibility(View.VISIBLE);
                QiniuHelper.bingImageType(signinDto.getSignin().getImg(), signImg);
                signImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(getActivity());
                        bigPhotoDialog.setQiniuUrl(signinDto.getSignin().getImg());
                        bigPhotoDialog.show();
                    }
                });
            } else {
                signImg.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(signinDto.getSignin().getTask_name())) {
                signTaskNameText.setText(signinDto.getSignin().getTask_name() + " "
                        + "累计打卡" + signinDto.getSignin().getTask_continue_days() + "天");
            } else {
                taskLayout.setVisibility(View.GONE);
            }


            signCountText.setText(String.valueOf(signinDto.getSignin().getLike_count()));
            if (signinDto.isLiked()) {
                signCountText.setTextColor(getActivity().getResources().getColor(R.color.pink_ff));
                signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                signLikeImg.setEnabled(false);
            } else {
                signCountText.setTextColor(getActivity().getResources().getColor(R.color.gray_97));
                signLikeImg.setImageResource(R.mipmap.chat_sign_like_normal);
                signLikeImg.setEnabled(true);
                signLikeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signinDto.getSignin().getId(), new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) {
                                HuanXinHelper.sendGroupTextMessage(new LikeSigninMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), String.valueOf(signinDto.getSignin().getId())), UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                                        "[赞]@" + nick_name, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        signCountText.setText(signinDto.getSignin().getLike_count() + "");
                                                        signCountText.setTextColor(getActivity().getResources().getColor(R.color.pink_ff));
                                                        signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                                                        signLikeImg.setEnabled(false);
                                                    }
                                                });
                                                signinDto.getSignin().setLike_count(signinDto.getSignin().getLike_count() + 1);
                                                signinDto.setIsLiked(true);
                                                SignCacheModel.getInstance().putSign(signinDto);
                                                EventBus.getDefault().post(new GroupMessageEvent());
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }

                                            @Override
                                            public void onProgress(int i, String s) {

                                            }
                                        });
                            }
                        });
                    }
                });
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
