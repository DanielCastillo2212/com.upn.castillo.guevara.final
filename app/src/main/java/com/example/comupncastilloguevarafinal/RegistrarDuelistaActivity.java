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
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

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

        // Ejecutar la operación en un hilo separado
        new InsertDuelistaTask().execute(duelista);
    }

    private class InsertDuelistaTask extends AsyncTask<Duelista, Void, Long> {
        @Override
        protected Long doInBackground(Duelista... duelistas) {
            // Insertar el duelistas en la base de datos y devolver el ID generado
            return duelistaDao.insertDuelista(duelistas[0]);
        }

        @Override
        protected void onPostExecute(Long duelistaId) {
            super.onPostExecute(duelistaId);

            if (duelistaId != null) {
                Toast.makeText(RegistrarDuelistaActivity.this, "Duelista registrado con ID: " + duelistaId, Toast.LENGTH_SHORT).show();
                etNombre.setText("");
            } else {
                Toast.makeText(RegistrarDuelistaActivity.this, "Error al registrar el Duelista", Toast.LENGTH_SHORT).show();
            }
        }
    }
}