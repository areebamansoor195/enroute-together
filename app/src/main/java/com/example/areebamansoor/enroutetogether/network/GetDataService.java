package com.example.areebamansoor.enroutetogether.network;


import com.example.areebamansoor.enroutetogether.utils.Constants;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GetDataService {


    @POST(Constants.FCM_URL)
    Call<JsonElement> sendFCMRequest(@Body JSONObject fcmRequest, @Header("Authorization") String serverKey, @Header("Content-Type") String content_type);

}
