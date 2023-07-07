package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetalleDuelistaActivity extends AppCompatActivity {
    private TextView tvNombreDuelista;
    private Button btnRegistrarCarta;
    private Button btnVerCartas;
    private Button btnVerMapa;

    private long duelistaId;
    private String duelistaNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_duelista);

        tvNombreDuelista = findViewById(R.id.tv_nombre_duelista);
        btnRegistrarCarta = findViewById(R.id.btn_registrar_carta);
        btnVerCartas = findViewById(R.id.btn_ver_cartas);
        btnVerMapa = findViewById(R.id.btn_ver_mapa);

        // Recuperar los datos del Intent
        Intent intent = getIntent();
        duelistaId = intent.getLongExtra("duelistaId", -1);
        duelistaNombre = intent.getStringExtra("duelistaNombre");

        // Mostrar el nombre del Duelista en el TextView
        tvNombreDuelista.setText(duelistaNombre);

        btnRegistrarCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de registro de carta y pasar el ID del Duelista
                Intent intent = new Intent(DetalleDuelistaActivity.this, RegistrarCartaActivity.class);
                intent.putExtra("duelistaId", duelistaId);
                startActivity(intent);
            }
        });

        btnVerCartas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de ver cartas y pasar el ID del Duelista
                Intent intent = new Intent(DetalleDuelistaActivity.this, ListaCartasActivity.class);
                intent.putExtra("duelistaId", duelistaId);
                startActivity(intent);
            }
        });

        btnVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad del mapa
                Intent intent = new Intent(DetalleDuelistaActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }
}
