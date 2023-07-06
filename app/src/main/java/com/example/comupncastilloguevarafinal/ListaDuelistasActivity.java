package com.example.comupncastilloguevarafinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.comupncastilloguevarafinal.Adapters.DuelistaAdapter;
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

        List<Duelista> duelistas = duelistaDao.getAllDuelistas();
        adapter = new DuelistaAdapter(this, duelistas);
        recyclerView.setAdapter(adapter);
    }
}