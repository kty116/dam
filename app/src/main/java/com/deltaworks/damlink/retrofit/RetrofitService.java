package com.deltaworks.damlink.retrofit;


import com.deltaworks.damlink.model.TokenModel;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by kyoungae on 2018-03-12.
 */

public interface RetrofitService {


    @POST("/api/push/token")
    Call<TokenModel> sendTokenPost(@Query("device_token") String token, @Query("userId") String userId);

    @DELETE("/api/push/token")
    Call<TokenModel> sendTokenDelete(@Query("device_token") String token, @Query("userId") String userId);

    @PUT("/api/push/token")
    Call<TokenModel> sendTokenPut(@Query("device_token") String token, @Query("userId") String userId);


}
