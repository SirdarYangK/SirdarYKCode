package com.bjghhnt.app.treatmentdevice.interfaces;


import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Development_Android on 2017/1/5.
 */

public interface HttpRequestService {
    @FormUrlEncoded
    @POST("insert")
    Observable<ResponseBody> insertUserDataDB(@FieldMap Map<String, String> params);

    @GET("insert")
    Observable<ResponseBody> requestService();

    @GET("login.php")
    Observable<ResponseBody> getTopMovie(@Query("mobile") String number, @Query("passwd") String passwd);


    @POST("insert")
    Observable<ResponseBody> test(@Body RequestBody requestBody);
}
