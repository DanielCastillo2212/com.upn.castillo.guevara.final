package com.example.comupncastilloguevarafinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.ImageUpload.ImageUploadResponse;
import com.example.comupncastilloguevarafinal.ImageUpload.ImageUploadResquest;
import com.example.comupncastilloguevarafinal.Services.CartaApiService;
import com.example.comupncastilloguevarafinal.Services.CartaDao;
import com.example.comupncastilloguevarafinal.Services.ImageUploadService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrarCartaActivity extends AppCompatActivity implements LocationListener {
    private EditText etNombre;
    private EditText etPuntosAtaque;
    private EditText etPuntosDefensa;
    private TextView tvLatitud;
    private TextView tvLongitud;
    private Button btnRegistrar;
    private Button btnAgregarImagen;

    private TextView tvurlimagen;

    private CartaDao cartaDao;
    private long duelistaId;

    public Double Latitude;
    public Double Longitude;

    private String urlImagen;

    private LocationManager mLocationManager;

    private Uri selectedImageUri;

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_GALLERY = 2;

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
        btnAgregarImagen = findViewById(R.id.btn_agregarimagen);
        tvurlimagen = findViewById(R.id.tv_url_imagen);


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

        btnAgregarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    openImagePicker();

            }
        });
        verificarConexionYActualizarDatos();
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
                cartaDao.deleteAllCartas();
            }
        });

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
                            cartaDao.insertCartas(cartas);
                        }
                    });
                    Toast.makeText(RegistrarCartaActivity.this, "Datos actualizados desde la API", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistrarCartaActivity.this, "Error al obtener datos de la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Carta>> call, Throwable t) {
                Toast.makeText(RegistrarCartaActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
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

        String imageBase64 = convertImageToBase64(selectedImageUri);

        // Subir la imagen a la API
        uploadImageToApi(imageBase64);


        final Carta carta = new Carta(nombre, puntosAtaque, puntosDefensa, urlImagen, latitud, longitud, duelistaId);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Insertar la carta en la base de datos local y obtener el ID generado
                long cartaId = cartaDao.insertCarta(carta);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Mostrar un mensaje de éxito
                        Toast.makeText(RegistrarCartaActivity.this, "Carta registrada con ID: " + cartaId, Toast.LENGTH_SHORT).show();
                        etNombre.setText("");
                        etPuntosAtaque.setText("");
                        etPuntosDefensa.setText("");
                        tvLatitud.setText("");
                        tvLongitud.setText("");

                        // Llamar al método createCarta() del servicio CartaApiService para subir la carta a la API
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://64a5ba5100c3559aa9c01d7a.mockapi.io/") // Reemplazar con la URL base de tu API MockAPI
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        CartaApiService apiService = retrofit.create(CartaApiService.class);
                        Call<Carta> call = apiService.createCarta(carta);
                        call.enqueue(new Callback<Carta>() {
                            @Override
                            public void onResponse(Call<Carta> call, Response<Carta> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    // Mostrar un mensaje de éxito
                                    Toast.makeText(RegistrarCartaActivity.this, "Carta subida a la API correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Mostrar un mensaje de error
                                    Toast.makeText(RegistrarCartaActivity.this, "Error al subir la carta a la API", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Carta> call, Throwable t) {
                                // Mostrar un mensaje de error
                                Toast.makeText(RegistrarCartaActivity.this, "Error en la llamada a la API", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }


    private void openImagePicker() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            // Obtener la URI de la imagen seleccionada desde la galería
            selectedImageUri = data.getData();

            // Mostrar un Toast indicando que la imagen se agregó correctamente
            Toast.makeText(RegistrarCartaActivity.this, "Imagen agregada correctamente", Toast.LENGTH_SHORT).show();

            // Obtener la imagen en base64
            String imageBase64 = convertImageToBase64(selectedImageUri);

            // Subir la imagen a la API
            uploadImageToApi(imageBase64);
        }
    }



    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadImageToApi(String base64Image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://demo-upn.bit2bittest.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ImageUploadService imageUploadService = retrofit.create(ImageUploadService.class);

        ImageUploadResquest request = new ImageUploadResquest(base64Image);

        Call<ImageUploadResponse> call = imageUploadService.uploadImage(request);
        call.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ImageUploadResponse uploadResponse = response.body();
                    String imageUrl = uploadResponse.getImageUrl();

                    // Actualizar el campo urlImagen con la imageUrl
                    tvurlimagen.setText("https://demo-upn.bit2bittest.com/" + imageUrl);

                    urlImagen = tvurlimagen.getText().toString();
                    Log.d("URL", "URL de la imagen: " + urlImagen);
                } else {
                    // Mostrar mensaje de error en caso de respuesta no exitosa o cuerpo nulo
                    Toast.makeText(RegistrarCartaActivity.this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                // Mostrar mensaje de error en caso de fallo en la llamada
                Toast.makeText(RegistrarCartaActivity.this, "Error en la llamada al servidor", Toast.LENGTH_SHORT).show();
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