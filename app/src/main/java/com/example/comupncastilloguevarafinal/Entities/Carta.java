package com.example.comupncastilloguevarafinal.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cartas", foreignKeys = @ForeignKey(entity = Duelista.class, parentColumns = "id", childColumns = "duelistaId", onDelete = ForeignKey.CASCADE))
public class Carta {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String nombre;
    private int puntosAtaque;
    private int puntosDefensa;
    private String imagen;
    private double latitud;
    private double longitud;
    private long duelistaId;

    public Carta(String nombre, int puntosAtaque, int puntosDefensa, String imagen, double latitud, double longitud, long duelistaId) {
        this.nombre = nombre;
        this.puntosAtaque = puntosAtaque;
        this.puntosDefensa = puntosDefensa;
        this.imagen = imagen;
        this.latitud = latitud;
        this.longitud = longitud;
        this.duelistaId = duelistaId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntosAtaque() {
        return puntosAtaque;
    }

    public void setPuntosAtaque(int puntosAtaque) {
        this.puntosAtaque = puntosAtaque;
    }

    public int getPuntosDefensa() {
        return puntosDefensa;
    }

    public void setPuntosDefensa(int puntosDefensa) {
        this.puntosDefensa = puntosDefensa;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public long getDuelistaId() {
        return duelistaId;
    }

    public void setDuelistaId(long duelistaId) {
        this.duelistaId = duelistaId;
    }
}