package com.example.beautyinside.network;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface EyeApiService {

    @Multipart
    @POST("infer")
    Call<InferResponse> uploadImage(
            @Part MultipartBody.Part file
    );

}
