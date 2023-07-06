package com.example.comupncastilloguevarafinal.Services;

import com.example.comupncastilloguevarafinal.Entities.Duelista;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DuelistaApiService {
    @POST("duelistas")
    Call<Duelista> createDuelista(@Body Duelista duelista);

    @GET("duelistas")
    Call<List<Duelista>> getDuelistas();
}
