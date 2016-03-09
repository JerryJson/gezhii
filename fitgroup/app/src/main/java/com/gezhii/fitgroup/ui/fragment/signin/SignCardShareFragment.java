package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.SigninInfoDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.QrHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.utils.BitmapUtil;

import java.util.HashMap;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/27.
 */
public class SignCardShareFragment extends BaseFragment {
    public static final String TAG_SIGN_ID = "tag_sign_id";
    public static final String TAG_HUANXIN_ID = "tag_huanxin_id";
    public static final String TAG_SIGNIN = "tag_signin";

    public static final int FLAG_SHARE_OTHER_SIGNIN = 1;
    public static final int FLAG_SHARE_SELF_SIGNIN = 0;
    public static final String TAG_FLAG = "flag";

    public static final String TAG = "tag";
    public static final String TAG_WECHAT = "tag_wechat";
    public static final String TAG_WECHAT_MOMENT = "tag_wechat_moment";
    public static final String TAG_SINA_WEIBO = "tag_sina_weibo";
    public static final String TAG_QQ = "tag_qq";

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
    @InjectView(R.id.task_layout)
    LinearLayout taskLayout;
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
    LinearLayout shareBottomLayout;
    @InjectView(R.id.share_to_third_layout)
    LinearLayout shareToThirdLayout;
    @InjectView(R.id.sign_card_share_layout)
    LinearLayout signCardShareLayout;


    int sign_id;
    String user_huanxin_id;
    Signin signin;
    private HashMap<String, Object> hashMap = new HashMap<String, Object>();
    boolean isInit;

    public static void start(Activity activity, int sign_id, String huanxin_id, String share_type) {
        Log.i("sign_id", sign_id);
        Log.i("huanxin_id", huanxin_id);
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_SIGN_ID, sign_id);
        params.put(TAG_HUANXIN_ID, huanxin_id);
        params.put(TAG, share_type);
        params.put(TAG_FLAG,FLAG_SHARE_SELF_SIGNIN);
        mainActivity.showNext(SignCardShareFragment.class, params);
    }

    public static void start(Activity activity,Signin signin,String share_type){
        Log.i("signin",signin);
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String,Object> params = new HashMap<>();
        params.put(TAG_SIGNIN,signin);
        params.put(TAG,share_type);
        params.put(TAG_FLAG,FLAG_SHARE_OTHER_SIGNIN);
        mainActivity.showNext(SignCardShareFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_card_share_fragment, null);
        ButterKnife.inject(this, rootView);
        initView();
        return rootView;
    }

    public void initView() {
        //qrcodeImg
        String text = "http://dl.qing.am?type=signin";
        String qrcodePath = QrHelper.createImage(getActivity(), text, UUID.randomUUID().toString());
        qrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(qrcodePath));

//        if (UserModel.getInstance().getMyGroup() != null && UserModel.getInstance().getMyGroup().getGroup_name() != null) {
//            String groupName = UserModel.getInstance().getMyGroup().getGroup_name();
//            myAssociationNumberText.setText("我的公会" + "\"" + groupName + "\"" + "累计减脂");
//            Log.i("weight-------->", UserModel.getInstance().getMyGroup().getTotal_weight_reduction());
//            loseWeightValueText.setText("" + UserModel.getInstance().getMyGroup().getTotal_weight_reduction());
//        } else {
//            myGroupLayout.setVisibility(View.GONE);
//        }

        hashMap = getNewInstanceParams();
        SigninDto signinDto = null;
       if((int)hashMap.get(TAG_FLAG) == FLAG_SHARE_SELF_SIGNIN){
           sign_id = (int) hashMap.get(TAG_SIGN_ID);
           user_huanxin_id = (String) hashMap.get(TAG_HUANXIN_ID);
           signinDto = SignCacheModel.getInstance().getSign(sign_id);

       }else if((int)hashMap.get(TAG_FLAG) == FLAG_SHARE_OTHER_SIGNIN){
           signinDto = new SigninDto();
           signin = (Signin)hashMap.get(TAG_SIGNIN);
           signinDto.setSignin(signin);
       }

        if (signinDto != null) {
            String icon = null;
            if((int)hashMap.get(TAG_FLAG) == FLAG_SHARE_OTHER_SIGNIN){
                userNameText.setText(signin.getUser().getNick_name());
                icon = signin.getUser().getIcon();
            }else if((int)hashMap.get(TAG_FLAG) == FLAG_SHARE_SELF_SIGNIN){
                userNameText.setText(UserCacheModel.getInstance().getUserInfo(user_huanxin_id).nickName);
                icon = UserCacheModel.getInstance().getUserInfo(user_huanxin_id).icon;
                Log.i("icon", icon);
            }
            userIconImg.setImageBitmap(BitmapUtil.filePathToBitmap(ImageLoader.getInstance().getDiskCache().get(QiniuHelper.getPicCacheUrl(icon)).getAbsolutePath()));

            // QiniuHelper.bindAvatarImage(icon, userIconImg);
            signDateText.setText(TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));
            if (!TextUtils.isEmpty(signinDto.getSignin().getImg())) {
                QiniuHelper.bindImage(signinDto.getSignin().getImg(), signImg);
                String downUrl = QiniuHelper.getPicCacheUrl(signinDto.getSignin().getImg());
                Bitmap bitmap = ImageLoader.getInstance().getMemoryCache().get(QiniuHelper.getPicCacheUrl(signinDto.getSignin().getImg()));

                signImg.setImageBitmap(BitmapUtil.filePathToBitmap(ImageLoader.getInstance().getDiskCache().get(QiniuHelper.getPicCacheUrl(signinDto.getSignin().getImg())).getAbsolutePath()));

            } else {
                signImg.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(signinDto.getSignin().getTask_name())) {
                signTaskNameText.setText(signinDto.getSignin().getTask_name() + " "
                        + "累计打卡" + signinDto.getSignin().getTask_continue_days() + "天");
            } else {
                taskLayout.setVisibility(View.GONE);
            }


            //描述
            if (!TextUtils.isEmpty(signinDto.getSignin().getDescription())) {
                signDescriptionText.setText(signinDto.getSignin().getDescription());
            } else {
                signDescriptionText.setVisibility(View.GONE);
            }
            SigninInfoDto signinInfoDto = SigninInfoDto.parserJson(signinDto.getSignin().getSignin_info());
        }


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signCardShareLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (!isInit) {
                            if (TAG_WECHAT.equals(hashMap.get(TAG))) {
                                Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                                String filePath = QiniuHelper.saveShareBitmap(bitmap);
                                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                                shareParams.setImagePath(filePath);
                                ShareHelper.shareWechat(getActivity(), shareParams);
                            } else if (TAG_WECHAT_MOMENT.equals(hashMap.get(TAG))) {
                                Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                                String filePath = QiniuHelper.saveShareBitmap(bitmap);
                                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                                shareParams.setImagePath(filePath);
                                ShareHelper.shareWechatMoment(getActivity(), shareParams);
                            } else if (TAG_SINA_WEIBO.equals(hashMap.get(TAG))) {
                                Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                                String filePath = QiniuHelper.saveShareBitmap(bitmap);
                                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                                shareParams.setImagePath(filePath);
                                shareParams.setText("运动咖 http://dl.qing.am");
                                ShareHelper.shareSina(getActivity(), shareParams);
                            } else if (TAG_QQ.equals(hashMap.get(TAG))) {
                                Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                                String filePath = QiniuHelper.saveShareBitmap(bitmap);
                                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                                shareParams.setImagePath(filePath);
                                ShareHelper.shareQQ(getActivity(), shareParams);
                            }
                            finish();
                        }
                        isInit = true;
                    }
                });

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
