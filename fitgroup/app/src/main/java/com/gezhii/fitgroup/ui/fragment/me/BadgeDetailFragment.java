package com.gezhii.fitgroup.ui.fragment.me;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.QrHelper;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;
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
 * Created by fantasy on 15/11/7.
 */
public class BadgeDetailFragment extends BaseFragment {
    @InjectView(R.id.close_img)
    ImageView closeImg;
    @InjectView(R.id.badge_bg_img)
    ImageView badgeBgImg;
    @InjectView(R.id.badge_text)
    TextView badgeText;
    @InjectView(R.id.badge_img)
    ImageView badgeImg;
    @InjectView(R.id.badge_information_text)
    TextView badgeInformationText;
    @InjectView(R.id.unlock_condition_text)
    TextView unlockConditionText;
    @InjectView(R.id.lock_badge_img)
    ImageView lockBadgeImg;
    @InjectView(R.id.share_text)
    TextView shareText;
    @InjectView(R.id.wechat_img)
    ImageView wechatImg;
    @InjectView(R.id.wechat_moment_img)
    ImageView wechatMomentImg;
    @InjectView(R.id.sina_weibo_img)
    ImageView sinaWeiboImg;
    @InjectView(R.id.qq_img)
    ImageView qqImg;
    @InjectView(R.id.badge_detail_layout)
    RelativeLayout badgeDetailLayout;
    @InjectView(R.id.save_picture_text)
    TextView savePictureText;
//    @InjectView(R.id.my_association_number_text)
//    TextView myAssociationNumberText;
//    @InjectView(R.id.kg_text)
//    TextView kgText;
//    @InjectView(R.id.lose_weight_value_text)
//    TextView loseWeightValueText;
//    @InjectView(R.id.app_icon_img)
//    ImageView appIconImg;
    @InjectView(R.id.badge_share_bottom_layout)
    LinearLayout badgeShareBottomLayout;
    @InjectView(R.id.badge_share_layout)
    LinearLayout badgeShareLayout;
    @InjectView(R.id.share_to_layout)
    LinearLayout shareToLayout;
    @InjectView(R.id.lock_img)
    ImageView lockImg;
//    @InjectView(R.id.my_group_layout)
//    RelativeLayout myGroupLayout;
    @InjectView(R.id.qrcode_img)
    ImageView qrcodeImg;

    private HashMap<String, Object> hashMap = new HashMap<String, Object>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.badge_detail_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "badge_profile", UmengEvents.getEventMap("click", "load"));
        hashMap = getNewInstanceParams();
        QiniuHelper.bindImage(hashMap.get("icon").toString(), badgeImg);
        QiniuHelper.bindImage(hashMap.get("bg").toString(), badgeBgImg);
        if (hashMap.get("isLocked").equals(true)) {
            lockBadgeImg.setVisibility(View.VISIBLE);
            shareToLayout.setVisibility(View.INVISIBLE);
        } else {
            lockBadgeImg.setVisibility(View.GONE);
            lockImg.setImageResource(R.mipmap.unlock_shape);
        }
        badgeText.setText(hashMap.get("name").toString());
        badgeInformationText.setText(hashMap.get("description").toString());
        unlockConditionText.setText("解锁条件: " + hashMap.get("unlock_condition").toString());
        //qrcodeImg
        String text = "http://dl.qing.am?type=badge";
        String qrcodePath = QrHelper.createImage(getActivity(), text, UUID.randomUUID().toString());
        qrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(qrcodePath));
        setTitle();
        shareBadge();
        return rootView;
    }

    public void setTitle() {
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    public static int getItemHeight(View rootView) {
        rootView.measure(UNBOUNDED, UNBOUNDED);
        return rootView.getMeasuredHeight();
    }

    public void shareBadge() {
//        if (UserModel.getInstance().getMyGroup() != null && UserModel.getInstance().getMyGroup().getGroup_name() != null) {
//            String groupName = UserModel.getInstance().getMyGroup().getGroup_name();
//            myAssociationNumberText.setText("我的公会" + "\"" + groupName + "\"" + "累计减脂");
//            loseWeightValueText.setText("" + UserModel.getInstance().getMyGroup().getTotal_weight_reduction());
//        } else {
//            myGroupLayout.setVisibility(View.GONE);
//        }
        wechatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "badge_profile", UmengEvents.getEventMap("click", "wechat_session"));
                badgeShareBottomLayout.setVisibility(View.VISIBLE);
                shareToLayout.setVisibility(View.INVISIBLE);
                Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                String filePath = saveBitmap(bitmap);
                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                shareParams.setImagePath(filePath);
                ShareHelper.shareWechat(getActivity(), shareParams);
                badgeShareBottomLayout.setVisibility(View.INVISIBLE);
                shareToLayout.setVisibility(View.VISIBLE);
            }
        });
        wechatMomentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "badge_profile", UmengEvents.getEventMap("click", "wechat_timeline"));
                badgeShareBottomLayout.setVisibility(View.VISIBLE);
                shareToLayout.setVisibility(View.INVISIBLE);
                Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                String filePath = saveBitmap(bitmap);
                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                shareParams.setImagePath(filePath);
                ShareHelper.shareWechatMoment(getActivity(), shareParams);
                badgeShareBottomLayout.setVisibility(View.INVISIBLE);
                shareToLayout.setVisibility(View.VISIBLE);
            }
        });
        sinaWeiboImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "badge_profile", UmengEvents.getEventMap("click", "sinaweibo"));
                badgeShareBottomLayout.setVisibility(View.VISIBLE);
                shareToLayout.setVisibility(View.INVISIBLE);
                Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                String filePath = saveBitmap(bitmap);
                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                shareParams.setImagePath(filePath);
                shareParams.setText("轻元素 http://dl.qing.am");
                ShareHelper.shareSina(getActivity(), shareParams);
                badgeShareBottomLayout.setVisibility(View.INVISIBLE);
                shareToLayout.setVisibility(View.VISIBLE);
            }
        });
        qqImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "badge_profile", UmengEvents.getEventMap("click", "qq"));
                badgeShareBottomLayout.setVisibility(View.VISIBLE);
                shareToLayout.setVisibility(View.INVISIBLE);
                Bitmap bitmap = ImageFormat.getViewCache(badgeShareLayout);
                String filePath = saveBitmap(bitmap);
                ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                shareParams.setImagePath(filePath);
                ShareHelper.shareQQ(getActivity(), shareParams);
                badgeShareBottomLayout.setVisibility(View.INVISIBLE);
                shareToLayout.setVisibility(View.VISIBLE);
            }
        });

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
