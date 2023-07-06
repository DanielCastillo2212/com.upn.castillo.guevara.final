package com.example.comupncastilloguevarafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Services.CartaDao;

public class RegistrarCartaActivity extends AppCompatActivity implements LocationListener {
    private EditText etNombre;
    private EditText etPuntosAtaque;
    private EditText etPuntosDefensa;
    private TextView tvLatitud;
    private TextView tvLongitud;
    private Button btnRegistrar;

    private CartaDao cartaDao;
    private long duelistaId;

    public Double Latitude;
    public Double Longitude;

    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_carta);

        etNombre = findViewById(R.id.et_nombre);
        etPuntosAtaque = findViewById(R.id.et_puntos_ataque);
        etPuntosDefensa = findViewById(R.id.et_puntos_defensa);
        tvLatitud = findViewById(R.id.tv_latitud);
        tvLongitud = findViewById(R.id.tv_longitud);
        btnRegistrar = findViewById(R.id.btn_registrar);

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        cartaDao = db.cartaDao();

        // Recuperar el ID del Duelista del Intent
        Intent intent = getIntent();
        duelistaId = intent.getLongExtra("duelistaId", -1);

        //PEDIR PERMISOS DE UBICACION
        if(
                checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };
            requestPermissions(permissions, 3000);

        }
        else {
            // configurar frecuencia de actualización de GPS GPSPROMIDER Y NETWORK
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1, this);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Log.i("MAIN_APP: Location - ",  "Latitude: " + location.getLatitude());
            if(location != null){
                Log.i("MAIN_APP: Location - ",  "Latitude: " + location.getLatitude());
            }
            else {
                Log.i("MAIN_APP: Location - ",  "Location is null");
            }
        }

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
        double latitud = Double.parseDouble(tvLatitud.getText().toString());
        double longitud = Double.parseDouble(tvLongitud.getText().toString());

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese un nombre de Carta", Toast.LENGTH_SHORT).show();
            return;
        }

        final Carta carta = new Carta(nombre, puntosAtaque, puntosDefensa, "", latitud, longitud, duelistaId);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                long cartaId = cartaDao.insertCarta(carta);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistrarCartaActivity.this, "Carta registrada con ID: " + cartaId, Toast.LENGTH_SHORT).show();
                        etNombre.setText("");
                        etPuntosAtaque.setText("");
                        etPuntosDefensa.setText("");
                        tvLatitud.setText("");
                        tvLongitud.setText("");
                    }
                });
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Latitude = location.getLatitude();
        Longitude = location.getLongitude();

        // Actualizar los valores de longitud y latitud en los EditText correspondientes
        TextView etLatitud = findViewById(R.id.tv_latitud);
        TextView etLongitud = findViewById(R.id.tv_longitud);

        etLatitud.setText(String.valueOf(Latitude));
        etLongitud.setText(String.valueOf(Longitude));
        //mandar cordenadas actuales
        // Log.i("MAIN_APP: Location AND", "Latitude: " + latitude);
        // Log.i("MAIN_APP: Location AND", "Longitude: " + longitude);
        mLocationManager.removeUpdates(this);
    }
}