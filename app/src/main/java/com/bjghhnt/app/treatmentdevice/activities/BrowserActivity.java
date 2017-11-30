package com.bjghhnt.app.treatmentdevice.activities;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.bjghhnt.app.treatmentdevice.BuildConfig;
import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.serials.ReceiveThread;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialFrame;
import com.bjghhnt.app.treatmentdevice.utils.serials.SerialHandler;
import com.bjghhnt.app.treatmentdevice.utils.serials.SimpleHandler;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserActivity extends MinorActivity {
    private final SimpleHandler mSimHandler = new SimpleHandler(this);
    private static final String TAG = "BrowserActivity";
    @BindView(R.id.web_view_news)
    WebView mWebView;
    @BindView(R.id.btn_back_from_browser)
    Button btnBackFromBrowser;
    @BindView(R.id.btn_browser_backward)
    Button btnBrowserBackward;
    @BindView(R.id.btn_browser_forward)
    Button btnBrowserForward;

    @Override
    protected void upDateTime() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);
        mWebView.setWebViewClient(new RestrictedWebViewClient());
        // 启用javascript
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);
        String ua = settings.getUserAgentString();
        System.out.println(ua);
        settings.setUserAgentString(ua.replace("Android", "HFWSH_USER Android"));
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.loadUrl("http://www.bjghhnt.com/page/zjll/index.php");
        mWebView.loadUrl("http://www.youku.com/");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Log.d(TAG, "onCreate: " + mWebView.isShown());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected SerialHandler setHandler() {
        return mSimHandler;
    }

    @OnClick(R.id.btn_back_from_browser)
    void goBack() {
        super.finish();
    }

    @OnClick(R.id.btn_browser_backward)
    void navigationBack() {
        mWebView.goBack();
    }

    @OnClick(R.id.btn_browser_forward)
    void navigationForward() {
        mWebView.goForward();
    }

    private class RestrictedWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If this is under the correct web site, do not override; let my WebView load the page (return false)
            // otherwise do nothing (merely return true)
//            return (!Uri.parse(url).getHost().equals("www.bjghhnt.com"));
            view.loadUrl(url);
            return true;
        }

    }
}
