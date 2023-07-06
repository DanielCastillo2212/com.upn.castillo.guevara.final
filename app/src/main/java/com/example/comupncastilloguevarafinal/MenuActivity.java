package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button btnRegistrarDuelistaMenu;
    private Button btnVerDuelistasMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnRegistrarDuelistaMenu = findViewById(R.id.btn_registrar_duelista_menu);
        btnVerDuelistasMenu = findViewById(R.id.btn_ver_duelistas_menu);

        btnRegistrarDuelistaMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de registro de duelista
                Intent intent = new Intent(MenuActivity.this, RegistrarDuelistaActivity.class);
                startActivity(intent);
            }
        });

        btnVerDuelistasMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de ver duelistas
                Intent intent = new Intent(MenuActivity.this, ListaDuelistasActivity.class);
                startActivity(intent);
            }
        });
    }
}