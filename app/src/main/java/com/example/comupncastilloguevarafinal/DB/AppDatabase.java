package com.example.comupncastilloguevarafinal.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.Services.CartaDao;
import com.example.comupncastilloguevarafinal.Services.DuelistaDao;

@Database(entities = {Duelista.class, Carta.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DuelistaDao duelistaDao();
    public abstract CartaDao cartaDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "app_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
