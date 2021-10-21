package com.gustavo.socialmediagame.retrofit;

import com.gustavo.socialmediagame.models.FCMBody;
import com.gustavo.socialmediagame.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA_T3ZDlU:APA91bHiCjX7h473CMLZ8pfa6qoxG9sxpfToikdLAx9VFYbojwsSagLB_cJa7vChWAxnG90T7XYiy8mN9qROAOeH_9mAqpiJrJXMCW7QqqXYuvJaW4yxrB_L2WkJhnCGO6ZR3lUw_awE"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
