package com.example.marce.luckypuzzle.io.apiServices;

import com.example.marce.luckypuzzle.model.EmailResponse;
import com.example.marce.luckypuzzle.utils.URLUtils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by marce on 02/04/17.
 */

public interface EmailAPIService {
    @FormUrlEncoded
    @POST(URLUtils.CHECK_EMAIL_URL)
    Call<EmailResponse> checkEmail(@Field("email")String email);
}
