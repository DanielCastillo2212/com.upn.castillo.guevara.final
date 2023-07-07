package com.example.comupncastilloguevarafinal.Services;

import com.example.comupncastilloguevarafinal.Entities.Carta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartaApiService {
    @POST("cartas")
    Call<Carta> createCarta(@Body Carta carta);

    @GET("cartas")
    Call<List<Carta>> getCartas();

    @GET("cartas/duelista/{duelistaId}")
    Call<List<Carta>> getCartasPorDuelista(@Path("duelistaId") long duelistaId);
}
