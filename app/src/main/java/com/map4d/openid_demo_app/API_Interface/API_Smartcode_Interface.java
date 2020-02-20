package com.map4d.openid_demo_app.API_Interface;

import com.map4d.openid_demo_app.Model.Model_Smartcode_Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface API_Smartcode_Interface {
    @Headers({
            "Content-Type:application/json"
    })
    @GET("/v2/api/smartcodes")
    Call<Model_Smartcode_Data> getSmartcodeData(
            @Query("location") String location

    );
}
