package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.Adapters.CartaAdapter;
import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Services.CartaApiService;
import com.example.comupncastilloguevarafinal.Services.CartaDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaCartasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartaAdapter adapter;

    private CartaDao cartaDao;
    private long duelistaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cartas);

        recyclerView = findViewById(R.id.recyclerview_cartas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        cartaDao = db.cartaDao();

        // Recuperar el ID del Duelista del Intent
        Intent intent = getIntent();
        duelistaId = intent.getLongExtra("duelistaId", -1);

        actualizarDatosDesdeApi();

        // Ejecutar la operación en un hilo de fondo utilizando AsyncTask
        new GetCartasAsyncTask().execute(duelistaId);
    }

    private class GetCartasAsyncTask extends AsyncTask<Long, Void, List<Carta>> {

        @Override
        protected List<Carta> doInBackground(Long... duelistaIds) {
            long duelistaId = duelistaIds[0];
            return cartaDao.getCartasByDuelistaId(duelistaId);
        }

        @Override
        protected void onPostExecute(List<Carta> cartas) {
            // Configurar el adaptador con la lista de cartas obtenidas
            adapter = new CartaAdapter(ListaCartasActivity.this, cartas);
            recyclerView.setAdapter(adapter);
        }
    }

    private void actualizarDatosDesdeApi() {
        // Obtener los datos de la API MockAPI y guardarlos en la base de datos local
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64a5ba5100c3559aa9c01d7a.mockapi.io/") // Reemplazar con la URL base de tu API MockAPI
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CartaApiService apiService = retrofit.create(CartaApiService.class);
        Call<List<Carta>> call = apiService.getCartas();
        call.enqueue(new Callback<List<Carta>>() {
            @Override
            public void onResponse(Call<List<Carta>> call, Response<List<Carta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Carta> cartas = response.body();
                    // Guardar las cartas en la base de datos local
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            cartaDao.deleteAllCartas();
                            cartaDao.insertCartas(cartas);
                            // Actualizar la lista de cartas
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new GetCartasAsyncTask().execute(duelistaId);
                                }
                            });
                        }
                    });
                    Toast.makeText(ListaCartasActivity.this, "Datos actualizados desde la API", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListaCartasActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Carta>> call, Throwable t) {
                Toast.makeText(ListaCartasActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
