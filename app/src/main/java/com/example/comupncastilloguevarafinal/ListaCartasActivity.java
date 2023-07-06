package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.comupncastilloguevarafinal.Adapters.CartaAdapter;
import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Services.CartaDao;

import java.util.List;

public class ListaCartasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartaAdapter adapter;

    private CartaDao cartaDao;
    private long duelistaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_cartas);

        recyclerView = findViewById(R.id.recyclerview_cartas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        cartaDao = db.cartaDao();

        // Recuperar el ID del Duelista del Intent
        Intent intent = getIntent();
        duelistaId = intent.getLongExtra("duelistaId", -1);

        // Ejecutar la operación en un hilo de fondo utilizando AsyncTask
        new GetCartasAsyncTask().execute(duelistaId);
    }

    private class GetCartasAsyncTask extends AsyncTask<Long, Void, List<Carta>> {

        @Override
        protected List<Carta> doInBackground(Long... duelistaIds) {
            long duelistaId = duelistaIds[0];
            return cartaDao.getCartasByDuelistaId(duelistaId);
        }

        @Override
        protected void onPostExecute(List<Carta> cartas) {
            // Configurar el adaptador con la lista de cartas obtenidas
            adapter = new CartaAdapter(ListaCartasActivity.this, cartas);
            recyclerView.setAdapter(adapter);
        }
    }
}
