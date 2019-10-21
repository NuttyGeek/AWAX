package com.example.bhati.routeapplication.helpers;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface FrameUploadApiClient {

    @Multipart
    @POST("/")
    Call<ResponseBody> uploadFrame(@Part("video_name") RequestBody video_name, @Part MultipartBody.Part photo);

    @GET("/")
    Call<ResponseBody> getResult(@QueryMap Map<String, String> queryMap);
}
