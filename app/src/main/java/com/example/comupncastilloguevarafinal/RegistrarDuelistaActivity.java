package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.Adapters.DuelistaAdapter;
import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaApiService;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarDuelistaActivity extends AppCompatActivity {
    private EditText etNombre;
    private Button btnRegistrar;

    private DuelistaDao duelistaDao;
    private DuelistaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_duelista);

        etNombre = findViewById(R.id.et_nombre);
        btnRegistrar = findViewById(R.id.btn_registrar);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        duelistaDao = db.duelistaDao();

        adapter = new DuelistaAdapter(this, new ArrayList<>(), new DuelistaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Duelista duelist) {
                // Manejar el evento de clic en el Duelista
                openDetalleDuelistaActivity(duelist.getId(), duelist.getNombre());
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDuelista();
            }
        });
    }

    private void registrarDuelista() {
        String nombre = etNombre.getText().toString();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre de Duelista", Toast.LENGTH_SHORT).show();
            return;
        }

        Duelista duelista = new Duelista(nombre);

        // Ejecutar la operación en un hilo separado utilizando AsyncTask
        new InsertDuelistaTask().execute(duelista);
    }

    private class InsertDuelistaTask extends AsyncTask<Duelista, Void, Long> {
        private Duelista insertedDuelista;

        @Override
        protected Long doInBackground(Duelista... duelistas) {
            // Guardar el Duelista recibido en la variable de instancia
            insertedDuelista = duelistas[0];

            // Insertar el Duelista en la base de datos y devolver el ID generado
            return duelistaDao.insertDuelista(duelistas[0]);
        }

        @Override
        protected void onPostExecute(Long duelistaId) {
            super.onPostExecute(duelistaId);

            if (duelistaId != -1) {
                // Duelista insertado correctamente en la base de datos local

                // Verificar la conexión a Internet y actualizar los datos desde la API
                verificarConexionYActualizarDatos();

                // Subir el Duelista a la API
                subirDuelistaALaApi(insertedDuelista);
            } else {
                // Error al insertar el Duelista en la base de datos local
                Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista en la base de datos local", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void subirDuelistaALaApi(Duelista duelista) {
        // Crear una instancia de Retrofit con la configuración adecuada
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://64a5ba5100c3559aa9c01d7a.mockapi.io/") // Reemplazar con la URL base de tu API MockAPI
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Crear el servicio de la API
        DuelistaApiService apiService = retrofit.create(DuelistaApiService.class);

        // Realizar la llamada para crear el Duelista en la API
        Call<Duelista> call = apiService.createDuelista(duelista);
        call.enqueue(new Callback<Duelista>() {
            @Override
            public void onResponse(Call<Duelista> call, Response<Duelista> response) {
                if (response.isSuccessful()) {
                    Duelista createdDuelista = response.body();
                    if (createdDuelista != null) {
                        // Mostrar el mensaje de éxito
                        Toast.makeText(RegistrarDuelistaActivity.this, "Duelista registrado con ID: " + createdDuelista.getId(), Toast.LENGTH_SHORT).show();
                        etNombre.setText("");
                    } else {
                        // Mostrar un mensaje de error
                        Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista en MockAPI", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar un mensaje de error
                    Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista en MockAPI", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Duelista> call, Throwable t) {
                // Mostrar un mensaje de error
                Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista en MockAPI", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarConexionYActualizarDatos() {
        // Verificar la conexión a Internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet, realizar la sincronización de datos
            actualizarDatosDesdeApi();
        } else {
            // Si no hay conexión a Internet, mostrar un mensaje de error o tomar alguna acción apropiada
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarDatosDesdeApi() {
        // Borrar todos los datos de la base de datos local
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                duelistaDao.deleteAllDuelistas();
            }
        });

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
                            duelistaDao.insertDuelistas(duelistas);
                            // Actualizar la lista de duelistas
                            updateDuelistas();
                        }
                    });
                    Toast.makeText(RegistrarDuelistaActivity.this, "Datos actualizados desde la API", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrarDuelistaActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Duelista>> call, Throwable t) {
                Toast.makeText(RegistrarDuelistaActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDetalleDuelistaActivity(long duelistaId, String nombreDuelista) {
        Intent intent = new Intent(RegistrarDuelistaActivity.this, DetalleDuelistaActivity.class);
        intent.putExtra("duelistaId", duelistaId);
        intent.putExtra("nombreDuelista", nombreDuelista);
        startActivity(intent);
    }

    private void updateDuelistas() {
        List<Duelista> duelistas = duelistaDao.getAllDuelistas();
        adapter.setDuelistas(duelistas);
        adapter.notifyDataSetChanged();
    }
}

