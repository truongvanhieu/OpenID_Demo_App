package com.map4d.openid_demo_app.API_Interface;

import com.map4d.openid_demo_app.Model.Model_loginApi;

import io.reactivex.Completable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Account_interface {
//    @Headers({
//            "Content-Type: application/x-www-form-urlencoded"
//    })
    @FormUrlEncoded
    @POST("/auth/realms/vibus/protocol/openid-connect/token")
    Call<Model_loginApi> loginAccount(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret

            );
    @FormUrlEncoded
    @POST("/auth/realms/vibus/protocol/openid-connect/logout")
    Call<ResponseBody> logoutAccount(
            @Field("client_id") String clientId,
            @Field("refresh_token") String refreshToken
    );

    @FormUrlEncoded
    @POST("/auth/realms/vibus/protocol/openid-connect/registrations")
    Call<Model_loginApi> registationAccount(
            @Field("client_id") String client_id,
            @Field("redirect_uri") String redirect_uri

    );
}
