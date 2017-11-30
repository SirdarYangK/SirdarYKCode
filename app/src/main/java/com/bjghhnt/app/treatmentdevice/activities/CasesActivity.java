package com.bjghhnt.app.treatmentdevice.activities;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bjghhnt.app.treatmentdevice.R;
import com.bjghhnt.app.treatmentdevice.utils.MediaUtility;
import com.bjghhnt.app.treatmentdevice.utils.OpenFileWebChromeClient;


import java.io.File;

/**
 * Created by Development_Android on 2016/12/21.
 */

public class CasesActivity extends AppCompatActivity {
    private WebView webView;

//    @BindView(R.id.et_name)
//    EditText etName;
//    @BindView(R.id.et_sex)
//    EditText etSex;
//    @BindView(R.id.et_age)
//    EditText etAge;
//    @BindView(R.id.post)
//    Button post;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cases);
//        ButterKnife.bind(this);
        setContentView(R.layout.test_view);
        webView = (WebView) findViewById(R.id.wb);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(this.mOpenFileWebChromeClient);
        webView.loadUrl("http://pic.sogou.com/");
    }
    public OpenFileWebChromeClient mOpenFileWebChromeClient = new OpenFileWebChromeClient(
            this);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == OpenFileWebChromeClient.REQUEST_FILE_PICKER) {
            if (mOpenFileWebChromeClient.mFilePathCallback != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(uri);
                } else {
                    mOpenFileWebChromeClient.mFilePathCallback
                            .onReceiveValue(null);
                }
            }
            if (mOpenFileWebChromeClient.mFilePathCallbacks != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    String path = MediaUtility.getPath(getApplicationContext(),
                            result);
                    Uri uri = Uri.fromFile(new File(path));
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(new Uri[] { uri });
                } else {
                    mOpenFileWebChromeClient.mFilePathCallbacks
                            .onReceiveValue(null);
                }
            }

            mOpenFileWebChromeClient.mFilePathCallback = null;
            mOpenFileWebChromeClient.mFilePathCallbacks = null;
        }
    }

//    @OnClick(R.id.post)
//    public void onClick() {
//        if (!etName.getText().toString().isEmpty()) {
//            Map<String, String> date = new HashMap<>();
//            date.put("name", etName.getText().toString());
//            date.put("age", etAge.getText().toString());
//            JSONObject jsonObject = new JSONObject(date);
//            System.out.println(jsonObject.toString());
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());
//
//            HttpManager httpManager = HttpManager.getInstance();
//            httpManager.doHttpRequestGetRespones(
//                    httpManager.getHttpService().test(requestBody),
//                    new HttpManager.OnResponse() {
//                        @Override
//                        public void onSuccess(ResponseBody responseBody) {
//                            byte[] bytes = new byte[0];
//                            try {
//                                bytes = responseBody.bytes();
//                                System.out.println(new String(bytes));
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailed(String error) {
//
//                        }
//                    });
//        }
//    }
}
