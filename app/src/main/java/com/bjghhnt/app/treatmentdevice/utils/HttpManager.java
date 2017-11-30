package com.bjghhnt.app.treatmentdevice.utils;

import com.bjghhnt.app.treatmentdevice.interfaces.HttpRequestService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 * Created by Development_Android on 2017/1/6.
 */

public class HttpManager {
    private static final long DEFAULT_TIMEOUT = 10;
    private static final String BASE_URL = "http://192.168.10.225:9090/";
//    private static final String BASE_URL = "http://114.215.142.13/";
    private volatile static HttpManager INSTANCE;

    interface OnResponse{
        void onSuccess(ResponseBody responseBody);
        void onFailed(String error);
    }

    public HttpRequestService getHttpService() {
        return httpService;
    }

    private final HttpRequestService httpService;

    //构造方法私有
    private HttpManager() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("client","android")
                            .addHeader("Connection","keep-alive")
                            .build();
                    return chain.proceed(request);
                });
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        httpService = retrofit.create(HttpRequestService.class);
    }



        //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    public void doHttpRequestGetRespones(Observable<ResponseBody> observable, OnResponse onResponse) {
//        Observable observable = httpService.in(httpService)
        observable
               /*失败后的retry配置*/
//                .retryWhen(new RetryWhenNetworkException())
               /*生命周期管理*/
//                .compose(basePar.getRxAppCompatActivity().bindToLifecycle())
               /*http请求线程*/
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
               /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
               /*结果判断*/
//                .map(basePar);
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onResponse.onFailed(e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        onResponse.onSuccess(responseBody);
                    }
                });
    }
}
