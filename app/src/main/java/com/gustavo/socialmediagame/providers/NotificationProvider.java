package com.gustavo.socialmediagame.providers;

import com.gustavo.socialmediagame.models.FCMBody;
import com.gustavo.socialmediagame.models.FCMResponse;
import com.gustavo.socialmediagame.retrofit.IFCMApi;
import com.gustavo.socialmediagame.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

        private String url = "https://fcm.googleapis.com";

        public  NotificationProvider(){

        }

       public Call<FCMResponse> sendNotification(FCMBody body){
            return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
        }
}
