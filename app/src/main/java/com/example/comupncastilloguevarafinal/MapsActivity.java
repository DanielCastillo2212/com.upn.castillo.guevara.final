package com.example.comupncastilloguevarafinal;

import androidx.fragment.app.FragmentActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Services.CartaApiService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private long duelistaId;
    private List<Carta> listaCartas; // Lista de cartas para el Duelista específico

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtener el ID del Duelista desde el Intent
        duelistaId = getIntent().getLongExtra("duelistaId", -1);

        // Obtener la lista de cartas registradas para el Duelista específico
        obtenerListaCartas();
    }

    private void obtenerListaCartas() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64a5ba5100c3559aa9c01d7a.mockapi.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CartaApiService apiService = retrofit.create(CartaApiService.class);
        Call<List<Carta>> call = apiService.getCartasPorDuelista(duelistaId);
        call.enqueue(new Callback<List<Carta>>() {
            @Override
            public void onResponse(Call<List<Carta>> call, Response<List<Carta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCartas = response.body();

                    for (Carta carta : listaCartas) {
                        Log.d("Carta", "Nombre: " + carta.getNombre() + ", Latitud: " + carta.getLatitud() + ", Longitud: " + carta.getLongitud());
                    }

                    configurarMapa();
                } else {
                    Log.d("API Error", "Error al obtener las cartas desde el API");
                }
            }

            @Override
            public void onFailure(Call<List<Carta>> call, Throwable t) {
                Log.d("API Error", "Error en la llamada al API: " + t.getMessage());
            }
        });
    }

    private void configurarMapa() {
        if (!listaCartas.isEmpty()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (Carta carta : listaCartas) {
            LatLng location = new LatLng(carta.getLatitud(), carta.getLongitud());
            mMap.addMarker(new MarkerOptions().position(location).title(carta.getNombre()));
        }

        if (!listaCartas.isEmpty()) {
            LatLng firstLocation = new LatLng(listaCartas.get(0).getLatitud(), listaCartas.get(0).getLongitud());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10));
        }
    }
}



