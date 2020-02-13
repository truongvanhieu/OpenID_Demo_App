package com.map4d.openid_demo_app.API_Interface;

import com.map4d.openid_demo_app.Model.Model_loginApi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Login_interface {
//    @Headers({
//            "Content-Type: application/x-www-form-urlencoded"
//    })
    //https://accounts.vimap.vn/auth/realms/smartcodes/protocol/openid-connect/logout"
    @FormUrlEncoded
    @POST("/auth/realms/smartcodes/protocol/openid-connect/token")
    Call<Model_loginApi> loginAccount(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret

            );
    @FormUrlEncoded
    @POST("/auth/realms/smartcodes/protocol/openid-connect/logout")
    Call<Model_loginApi> logoutAccount(
            @Field("username") String username

    );
//    @Headers({
//        "Content-Type: application/x-www-form-urlencoded"
//    })
//    @Multipart
//    @POST("/auth/realms/smartcodes/protocol/openid-connect/token")
//    Call<Model_loginApi> loginAccount(
//            @Part("username") String username,
//            @Part("password") String password,
//            @Part("grant_type") String grant_type,
//            @Part("client_id") String client_id,
//            @Part("client_secret") String client_secret
//    );
}
