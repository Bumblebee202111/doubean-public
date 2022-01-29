package com.doubean.ford.api;

import com.doubean.ford.data.GroupApiModel;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DoubanService {
    String BASE_URL = "https://frodo.douban.com/api/v2/";

    @GET("group/{groupId}")
    @Headers("User-Agent: api-client/1 com.douban.frodo/7.5.0(211) Android/28 product/product vendor/Huawei model/MI 8  rom/miui6  network/wifi  udid/gaaa2fed5d8c6cs616bfeff12ba9uc099c54d4f0  platform/mobile\n" +
            "Host: frodo.douban.com\n" +
            "Connection: Keep-Alive\n" +
            "Accept-Encoding: gzip")
    public Call<GroupApiModel> getGroup(@Path("groupId") int groupId, @Query("access") int access);

    default DoubanService create() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DoubanService.class);
    }
}
