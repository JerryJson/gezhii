package com.gezhii.fitgroup.ui.fragment;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.discovery.GroupSimpleProfileFragment;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/6/10.
 */
public class WebFragment extends BaseFragment {

    public static final String TAG_NEW_PAGE = "p-";
    public static final String TAG_THIS_PAGE = "r-";
    public static final String TAG_SINGER_TOP_PAGE = "q-";


    public static final String TAG_CLIENT_URL = "tag_client_url";
    public static final String TAG_FLAG = "tag_flag";
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
    @InjectView(R.id.web_view)
    WebView webView;
//    @InjectView(R.id.share_btn)
//    TextView shareBtn;

    String clientUrl;
    @InjectView(R.id.add_to_group_btn)
    TextView addToGroupBtn;
    @InjectView(R.id.empty_view)
    View emptyView;
    ClipboardManager myClipboardManager;
    ClipData myClipData;
    int flag = 0;

    public static void client(Activity activity, String url) {
        client(activity, url, 0);
    }

    public static void client(Activity activity, String url, int flag) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_CLIENT_URL, url);
        params.put(TAG_FLAG, flag);
        mainActivity.showNext(WebFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_view_fragment, null);
        ButterKnife.inject(this, rootView);
        myClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (getNewInstanceParams().get(TAG_FLAG) != null) {
            flag = (int) getNewInstanceParams().get(TAG_FLAG);
            if (flag == 1) {
                titleText.setText("精选内容");
            }
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(this.toString(), "leftBtn onClick");
                finish();
            }
        });
        rightText.setText("复制链接");
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClipData = ClipData.newPlainText("text", clientUrl);
                myClipboardManager.setPrimaryClip(myClipData);
                showToast("复制成功！");
            }
        });
        Log.i(this.toString(), "onCreate");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        clientUrl = (String) getNewInstanceParams().get(TAG_CLIENT_URL);
        if (!TextUtils.isEmpty(clientUrl)) {
            if (clientUrl.contains("wy_group_id")) {
                final int groupId = Integer.valueOf(Uri.parse(clientUrl).getQueryParameter("wy_group_id"));
                emptyView.setVisibility(View.VISIBLE);
                addToGroupBtn.setVisibility(View.VISIBLE);
                addToGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getActivity(), "star_leader_page", UmengEvents.getEventMap("click", "加入ta的公会"));
                        GroupSimpleProfileFragment.start(getActivity(), groupId, "");
                    }
                });
            } else {
                emptyView.setVisibility(View.GONE);
                addToGroupBtn.setVisibility(View.GONE);
            }
            initWebView();
        }
    }

    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(getWebViewClient(getActivity()));
        webView.setWebChromeClient(getWebChromeClient());
        //webView.addJavascriptInterface(new ShareInterface(getActivity(), shareBtn), "share_interface");
        webView.loadUrl(clientUrl);
    }


    public WebChromeClient getWebChromeClient() {
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    if ("减肥百科".equals(title.trim())) {
                        titleText.setText("减肥百科");
                    }
                }


            }
        };

        return webChromeClient;
    }

    public static WebViewClient getWebViewClient(Activity context) {
        return new FitAnyWebViewClient(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView.loadData("", "text/html; charset=UTF-8", null);
        ButterKnife.reset(this);
    }


    public static class FitAnyWebViewClient extends WebViewClient {
        Activity mContext;

        public FitAnyWebViewClient(Activity context) {
            super();
            mContext = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.substring(0, 2).equals(TAG_NEW_PAGE)) {
                WebFragment.client(mContext, url.substring(2, url.length()));
            } else if (url.substring(0, 2).equals(TAG_THIS_PAGE)) {
                view.loadUrl(url.substring(2, url.length()));
            } else if (url.substring(0, 2).equals(TAG_SINGER_TOP_PAGE)) {
                WebFragment.client(mContext, url.substring(2, url.length()), Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                view.loadUrl(url);
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            view.loadUrl("javascript:share_interface.setShareBtn(document.getElementById('share').innerText,document.getElementById('title').innerText);");
            super.onPageFinished(view, url);
        }
    }


    public final class ShareInterface {
        Context mContext;
        View shareBtn;

        public ShareInterface(Context context, View shareBtn) {
            this.mContext = context;
            this.shareBtn = shareBtn;
        }

        /**
         * javascript接口  混淆时要keep
         *
         * @param title
         * @param url
         */
        @JavascriptInterface
        public void setShareBtn(final String url, final String title) {
            Log.i("xianrui", "setShareBtn   title : " + title + " url : " + url);
        }


    }


}
