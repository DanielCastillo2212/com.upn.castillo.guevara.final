package com.example.comupncastilloguevarafinal.Services;

import com.example.comupncastilloguevarafinal.ImageUpload.ImageUploadResponse;
import com.example.comupncastilloguevarafinal.ImageUpload.ImageUploadResquest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ImageUploadService {
    @POST("image")
    Call<ImageUploadResponse> uploadImage(@Body ImageUploadResquest request);
}
