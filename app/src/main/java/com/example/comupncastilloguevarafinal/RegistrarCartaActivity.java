package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Services.CartaDao;

public class RegistrarCartaActivity extends AppCompatActivity {
    private EditText etNombre;
    private EditText etPuntosAtaque;
    private EditText etPuntosDefensa;
    private EditText etLatitud;
    private EditText etLongitud;
    private Button btnRegistrar;

    private CartaDao cartaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_carta);

        etNombre = findViewById(R.id.et_nombre);
        etPuntosAtaque = findViewById(R.id.et_puntos_ataque);
        etPuntosDefensa = findViewById(R.id.et_puntos_defensa);
        etLatitud = findViewById(R.id.et_latitud);
        etLongitud = findViewById(R.id.et_longitud);
        btnRegistrar = findViewById(R.id.btn_registrar);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        cartaDao = db.cartaDao();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarCarta();
            }
        });
    }

    private void registrarCarta() {
        String nombre = etNombre.getText().toString();
        int puntosAtaque = Integer.parseInt(etPuntosAtaque.getText().toString());
        int puntosDefensa = Integer.parseInt(etPuntosDefensa.getText().toString());
        double latitud = Double.parseDouble(etLatitud.getText().toString());
        double longitud = Double.parseDouble(etLongitud.getText().toString());

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre de Carta", Toast.LENGTH_SHORT).show();
            return;
        }

        Carta carta = new Carta(nombre, puntosAtaque, puntosDefensa, latitud, longitud);
        long cartaId = cartaDao.insertCarta(carta);

        Toast.makeText(this, "Carta registrada con ID: " + cartaId, Toast.LENGTH_SHORT).show();
        etNombre.setText("");
        etPuntosAtaque.setText("");
        etPuntosDefensa.setText("");
        etLatitud.setText("");
        etLongitud.setText("");
    }
}