package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.comupncastilloguevarafinal.Adapters.DuelistaAdapter;
import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comupncastilloguevarafinal.DB.AppDatabase;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

import java.util.List;

public class ListaDuelistasActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DuelistaAdapter adapter;

    private DuelistaDao duelistaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_duelistas);

        recyclerView = findViewById(R.id.recyclerview_duelistas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        duelistaDao = db.duelistaDao();

        // Cargar los Duelistas en un hilo separado
        new LoadDuelistasTask().execute();
    }

    private void showDuelistas(List<Duelista> duelistas) {
        adapter = new DuelistaAdapter(this, duelistas, new DuelistaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Duelista duelist) {
                // Manejar el evento de clic en el Duelista
                openDetalleDuelistaActivity(duelist.getId(), duelist.getNombre());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void openDetalleDuelistaActivity(long duelistaId, String nombreDuelista) {
        Intent intent = new Intent(ListaDuelistasActivity.this, DetalleDuelistaActivity.class);
        intent.putExtra("duelistaId", duelistaId);
        intent.putExtra("nombreDuelista", nombreDuelista);
        startActivity(intent);
    }

    private class LoadDuelistasTask extends AsyncTask<Void, Void, List<Duelista>> {
        @Override
        protected List<Duelista> doInBackground(Void... voids) {
            // Obtener los Duelistas en segundo plano
            return duelistaDao.getAllDuelistas();
        }

        @Override
        protected void onPostExecute(List<Duelista> duelistas) {
            super.onPostExecute(duelistas);

            // Mostrar los Duelistas en el RecyclerView
            showDuelistas(duelistas);
        }
    }
}

