package com.alc.echange.api;

import com.alc.echange.model.Users;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("users")
    Call<Users> createUser(
            @Field("firstname") String firstname,
            @Field("lastname") String lastname,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("password") String password
    );

    //the sign in call
    @FormUrlEncoded
    @POST("login")
    Call<Users> login(
            @Field("phone") String phone,
            @Field("password") String password
    );
}
