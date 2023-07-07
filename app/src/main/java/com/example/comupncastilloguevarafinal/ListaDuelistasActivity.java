package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.comupncastilloguevarafinal.Adapters.DuelistaAdapter;
import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaApiService;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListaDuelistasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DuelistaAdapter adapter;

    private DuelistaDao duelistaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_duelistas);

        recyclerView = findViewById(R.id.recyclerview_duelistas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        duelistaDao = db.duelistaDao();

        actualizarDatosDesdeApi();
        // Cargar los Duelistas en un hilo separado
        new LoadDuelistasTask().execute();
    }

    private void showDuelistas(List<Duelista> duelistas) {
        adapter = new DuelistaAdapter(this, duelistas, new DuelistaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Duelista duelist) {
                openDetalleDuelistaActivity(duelist.getId(), duelist.getNombre());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void openDetalleDuelistaActivity(long duelistaId, String nombreDuelista) {
        Intent intent = new Intent(ListaDuelistasActivity.this, DetalleDuelistaActivity.class);
        intent.putExtra("duelistaId", duelistaId);
        intent.putExtra("nombreDuelista", nombreDuelista);
        startActivity(intent);
    }

    private class LoadDuelistasTask extends AsyncTask<Void, Void, List<Duelista>> {
        @Override
        protected List<Duelista> doInBackground(Void... voids) {
            List<Duelista> duelistas = duelistaDao.getAllDuelistas();
            Log.d("LoadDuelistasTask", "Cantidad de Duelistas: " + duelistas.size());
            return duelistas;
        }

        @Override
        protected void onPostExecute(List<Duelista> duelistas) {
            super.onPostExecute(duelistas);

            // Mostrar los Duelistas en el RecyclerView
            showDuelistas(duelistas);
        }
    }
    private void actualizarDatosDesdeApi() {
        // Obtener los datos de la API MockAPI y guardarlos en la base de datos local
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64a5ba5100c3559aa9c01d7a.mockapi.io/") // Reemplazar con la URL base de tu API MockAPI
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DuelistaApiService apiService = retrofit.create(DuelistaApiService.class);
        Call<List<Duelista>> call = apiService.getDuelistas();
        call.enqueue(new Callback<List<Duelista>>() {
            @Override
            public void onResponse(Call<List<Duelista>> call, Response<List<Duelista>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Duelista> duelistas = response.body();
                    // Guardar los duelistas en la base de datos local
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            duelistaDao.deleteAllDuelistas();
                            duelistaDao.insertDuelistas(duelistas);
                            // Actualizar la lista de duelistas
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new LoadDuelistasTask().execute();
                                }
                            });
                        }
                    });
                    Toast.makeText(ListaDuelistasActivity.this, "Datos actualizados desde la API", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListaDuelistasActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                Toast.makeText(ListaDuelistasActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

