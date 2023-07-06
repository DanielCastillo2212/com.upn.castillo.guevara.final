package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaApiService;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarDuelistaActivity extends AppCompatActivity {
    private EditText etNombre;
    private Button btnRegistrar;

    private DuelistaDao duelistaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_duelista);

        etNombre = findViewById(R.id.et_nombre);
        btnRegistrar = findViewById(R.id.btn_registrar);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        duelistaDao = db.duelistaDao();

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
        private Duelista duelista;

        @Override
        protected Long doInBackground(Duelista... duelistas) {
            // Guardar el Duelista recibido en la variable de instancia
            duelista = duelistas[0];

            // Insertar el Duelista en la base de datos y devolver el ID generado
            return duelistaDao.insertDuelista(duelistas[0]);
        }

        @Override
        protected void onPostExecute(Long duelistaId) {
            super.onPostExecute(duelistaId);

            if (duelistaId != -1) {
                // Duelista insertado correctamente en la base de datos local

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
            } else {
                // Error al insertar el Duelista en la base de datos local
                Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista en la base de datos local", Toast.LENGTH_SHORT).show();
            }
        }
    }
    }

