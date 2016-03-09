package com.gezhii.fitgroup.ui.fragment.me;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.QrHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.utils.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/23.
 */
public class BadgeShareFragment extends BaseFragment {
    public static final String TAG = "tag";
    public static final String TAG_BADGE = "tag_badge";
    public static final String TAG_WECHAT = "tag_wechat";
    public static final String TAG_WECHAT_MOMENT = "tag_wechat_moment";
    public static final String TAG_SINA_WEIBO = "tag_sina_weibo";
    public static final String TAG_QQ = "tag_qq";


    @InjectView(R.id.badge_bg_img)
    ImageView badgeBgImg;
    @InjectView(R.id.badge_text)
    TextView badgeText;
    @InjectView(R.id.badge_img)
    ImageView badgeImg;
    @InjectView(R.id.lock_badge_img)
    ImageView lockBadgeImg;
    @InjectView(R.id.badge_information_text)
    TextView badgeInformationText;
    @InjectView(R.id.lock_img)
    ImageView lockImg;
    @InjectView(R.id.unlock_condition_text)
    TextView unlockConditionText;
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
    @InjectView(R.id.badge_share_bottom_layout)
    LinearLayout badgeShareBottomLayout;
    @InjectView(R.id.badge_share_layout)
    LinearLayout badgeShareLayout;
    @InjectView(R.id.qrcode_img)
    ImageView qrcodeImg;
    private HashMap<String, Object> hashMap = new HashMap<String, Object>();
    boolean isInit;

    public static void start(Context context, String tag, Badge badge) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG, tag);
        params.put(TAG_BADGE, badge);
        ((MainActivity) context).showNext(BadgeShareFragment.class, params);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.badge_share_fragment, null);
        ButterKnife.inject(this, rootView);
        initView();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        badgeShareLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isInit) {
//                    if (UserModel.getInstance().getMyGroup() != null && UserModel.getInstance().getMyGroup().getGroup_name() != null) {
//                        String groupName = UserModel.getInstance().getMyGroup().getGroup_name();
//                        myAssociationNumberText.setText("我的公会" + "\"" + groupName + "\"" + "累计减脂");
//                        loseWeightValueText.setText("" + UserModel.getInstance().getMyGroup().getTotal_weight_reduction());
//                    } else {
//                        myGroupLayout.setVisibility(View.GONE);
//                    }
                    if (TAG_WECHAT.equals(hashMap.get(TAG))) {
                        Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                        String filePath = saveBitmap(bitmap);
                        ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                        shareParams.setImagePath(filePath);
                        ShareHelper.shareWechat(getActivity(), shareParams);
                    } else if (TAG_WECHAT_MOMENT.equals(hashMap.get(TAG))) {
                        Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                        String filePath = saveBitmap(bitmap);
                        ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                        shareParams.setImagePath(filePath);
                        ShareHelper.shareWechatMoment(getActivity(), shareParams);
                    } else if (TAG_SINA_WEIBO.equals(hashMap.get(TAG))) {
                        Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                        String filePath = saveBitmap(bitmap);
                        ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                        shareParams.setImagePath(filePath);
                        shareParams.setText("轻元素 http://dl.qing.am");
                        ShareHelper.shareSina(getActivity(), shareParams);
                    } else if (TAG_QQ.equals(hashMap.get(TAG))) {
                        Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                        String filePath = saveBitmap(bitmap);
                        ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                        shareParams.setImagePath(filePath);
                        shareParams.setText("");
                        ShareHelper.shareQQ(getActivity(), shareParams);
                    }
                    finish();
                    isInit = true;
                }
            }
        });

    }

    public void initView() {
        hashMap = getNewInstanceParams();
        Badge badge = (Badge) hashMap.get(TAG_BADGE);
        QiniuHelper.bindImage(badge.getIcon(), badgeImg);
        QiniuHelper.bindImage(badge.getBackground(), badgeBgImg);
        lockBadgeImg.setVisibility(View.GONE);
        lockImg.setImageResource(R.mipmap.unlock_shape);
        badgeText.setText(badge.getName());
        badgeInformationText.setText(badge.getDescription());
        unlockConditionText.setText("解锁条件: " + badge.getUnlock_condition());
        String text="http://dl.qing.am?type=badge";
        //qrcodeImg
        String qrcodePath = QrHelper.createImage(getActivity(), text, UUID.randomUUID().toString());
        qrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(qrcodePath));
        Log.i(hashMap.get(TAG));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public static String saveBitmap(Bitmap bitmap) {
        String localPath = Environment.getExternalStorageDirectory()
                + "/fitGroup/Image/";
        String localFile = localPath + getNowTimeAccurate() + ".jpg";
        File f = new File(localPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File imageFile = new File(localFile);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localFile;
    }

    public static String getNowTimeAccurate() {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String time = sdFormat.format(new Date());

        return time;
    }
}
