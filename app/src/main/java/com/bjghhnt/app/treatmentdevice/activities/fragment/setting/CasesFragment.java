package com.bjghhnt.app.treatmentdevice.activities.fragment.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bjghhnt.app.treatmentdevice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bxhan on 2017/3/28.
 */

public class CasesFragment extends Fragment {

    @BindView(R.id.wv_cases)
    WebView mWvCases;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.fragment_cases_set, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        WebSettings webSettings = mWvCases.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWvCases.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        mWvCases.loadUrl("http://124.202.175.142:8090/");
    }
}
