package com.gezhii.fitgroup.ui.fragment.group;

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
import android.widget.Toast;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.GroupProfileDTO;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.tools.qrcode.QrHelper;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.common.utils.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ycl on 15/10/23.
 */
public class ShareGroupProfileFragment extends BaseFragment {

    @InjectView(R.id.share_close_img)
    ImageView shareCloseImg;
    @InjectView(R.id.group_profile_mask_img)
    ImageView groupProfileMaskImg;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.group_leader_info_text)
    TextView groupLeaderInfoText;
    @InjectView(R.id.group_leader_icon_img)
    RectImageView groupLeaderIconImg;
    @InjectView(R.id.group_level_text)
    TextView groupLevelText;
    @InjectView(R.id.group_activeness_text)
    TextView groupActivenessText;
    @InjectView(R.id.group_description_text)
    TextView groupDescriptionText;
    @InjectView(R.id.group_number_text)
    TextView groupNumberText;
    @InjectView(R.id.qrcode_img)
    ImageView qrcodeImg;
//    @InjectView(R.id.lose_weight_value_text)
//    TextView loseWeightValueText;
    @InjectView(R.id.img_save_text)
    TextView imgSaveText;

    @InjectView(R.id.share_wechat_img)
    ImageView shareWeChatImg;
    @InjectView(R.id.share_wechat_zone_img)
    ImageView shareWeChatZoneImg;
    @InjectView(R.id.share_weibo_img)
    ImageView shareWeiboImg;
    @InjectView(R.id.share_qq_img)
    ImageView shareQQImg;
    @InjectView(R.id.share_to_third_layout)
    LinearLayout shareToThirdLayout;
    @InjectView(R.id.group_qrcode_img)
    ImageView groupQrcodeImg;
    @InjectView(R.id.save_picture_text)
    TextView savePictureText;
//    @InjectView(R.id.my_association_number_text)
//    TextView myAssociationNumberText;
//    @InjectView(R.id.kg_text)
//    TextView kgText;
//    @InjectView(R.id.my_group_layout)
//    RelativeLayout myGroupLayout;
//    @InjectView(R.id.app_icon_img)
//    ImageView appIconImg;
    @InjectView(R.id.qrcode_layout)
    LinearLayout qrcodeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.share_group_profile_fragment, null);
        ButterKnife.inject(this, view);
        MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "load"));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }


    public void setView() {
        final GroupProfileDTO profileDTO = (GroupProfileDTO) this.getNewInstanceParams().get("groupProfileDTO");
        shareCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "back"));
                finish();
            }
        });
        if (profileDTO != null) {
            //待布局结束之后,再执行这个方法
            groupProfileMaskImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (groupProfileMaskImg != null) {
                        QiniuHelper.bindBlurImage(profileDTO.group.getLeader().getIcon(), groupProfileMaskImg);
                    }

                }
            });

            //title + 公会号
            titleText.setText(profileDTO.group.getGroup_name());

            groupLeaderInfoText.setText("会长: " + profileDTO.group.getLeader().getNick_name()
                    + "  公会号: " + profileDTO.group.getGroup_number());

            //会长卡片部分
            QiniuHelper.bindAvatarImage(profileDTO.group.getLeader().getIcon(), groupLeaderIconImg);

            groupLevelText.setText("" + profileDTO.group.getLevel());

            if (profileDTO.group.yesterdayGroupDailyStatistics != null) {
                groupActivenessText.setText("" +
                        (int) Math.floor(profileDTO.group.yesterdayGroupDailyStatistics.getActiveness() * 100) + "%");
            } else {
                groupActivenessText.setText("0.0%");
            }

            //公会描述
            groupDescriptionText.setText(profileDTO.group.getDescription());

//            //公会名字 + 累计减脂
//            myAssociationNumberText.setText("我的公会" + "\"" + profileDTO.group.getGroup_name() + "\"" + "累计减脂");
//
//            loseWeightValueText.setText("" + profileDTO.group.getTotal_weight_reduction());

            //公会号 + 二微码
            groupNumberText.setText("公会号: " + profileDTO.group.getGroup_number());
            String text = "http://dl.qing.am?type=group_profile&group_number=" + profileDTO.group.getGroup_number();
            //qrcodeImg
            String groupQrcodePath = QrHelper.createImage(getActivity(), text, UUID.randomUUID().toString());
            groupQrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(groupQrcodePath));

//            String shareText = "http://dl.qing.am?type=group_profile";
//            String qrcodePath = QrHelper.createImage(getActivity(), shareText, UUID.randomUUID().toString());
//            qrcodeImg.setImageBitmap(BitmapUtil.filePathToBitmap(qrcodePath));
            qrcodeLayout.setVisibility(View.GONE);

            imgSaveText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareCloseImg.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                    String filePath = saveBitmap(bitmap);
                    Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_SHORT).show();
                    shareCloseImg.setVisibility(View.VISIBLE);
                }
            });
            shareWeChatImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "share wechat_session"));
                    shareCloseImg.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                    String filePath = saveBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareWechat(getActivity(), shareParams);
                    shareCloseImg.setVisibility(View.VISIBLE);
                }
            });

            shareWeChatZoneImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "share wechat_timeline"));
                    shareCloseImg.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                    String filePath = saveBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareWechatMoment(getActivity(), shareParams);
                    shareCloseImg.setVisibility(View.VISIBLE);
                }
            });

            shareWeiboImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "share sinaweibo"));
                    shareCloseImg.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                    String filePath = saveBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    shareParams.setText("轻元素 http://dl.qing.am");
                    ShareHelper.shareSina(getActivity(), shareParams);
                    shareCloseImg.setVisibility(View.VISIBLE);
                }
            });

            shareQQImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_card", UmengEvents.getEventMap("click", "share qq"));
                    shareCloseImg.setVisibility(View.INVISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(shareToThirdLayout);
                    String filePath = saveBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareQQ(getActivity(), shareParams);
                    shareCloseImg.setVisibility(View.VISIBLE);
                }
            });
        }


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
