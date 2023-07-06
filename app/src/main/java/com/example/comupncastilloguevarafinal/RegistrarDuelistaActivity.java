package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

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
        long duelistaId = duelistaDao.insertDuelista(duelista);

        Toast.makeText(this, "Duelista registrado con ID: " + duelistaId, Toast.LENGTH_SHORT).show();
        etNombre.setText("");
    }
}