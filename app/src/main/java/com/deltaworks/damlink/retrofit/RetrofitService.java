package com.deltaworks.damlink.retrofit;


import com.deltaworks.damlink.model.TokenModel;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by kyoungae on 2018-03-12.
 */

public interface RetrofitService {


    @FormUrlEncoded
    @POST("/api/push/token")
    Call<TokenModel> sendToken(@Field("device_token") String token, @Field("access_token") String access_token);  //경도 위도 값 보내기


}
