package com.example.comupncastilloguevarafinal.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comupncastilloguevarafinal.Entities.Carta;
import com.example.comupncastilloguevarafinal.ListaCartasActivity;
import com.example.comupncastilloguevarafinal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartaAdapter extends RecyclerView.Adapter<CartaAdapter.CartaViewHolder> {
    private Context context;
    private List<Carta> cartas;

    public CartaAdapter(Context context, List<Carta> cartas) {
        this.context = context;
        this.cartas = cartas;
    }

    @NonNull
    @Override
    public CartaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carta, parent, false);
        return new CartaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartaViewHolder holder, int position) {
        Carta carta = cartas.get(position);
        holder.tvNombre.setText(carta.getNombre());
        holder.tvPuntosAtaque.setText(String.valueOf(carta.getPuntosAtaque()));
        holder.tvPuntosDefensa.setText(String.valueOf(carta.getPuntosDefensa()));

        if (!carta.getImagen().isEmpty()) {
            Picasso.get().load(carta.getImagen()).into(holder.ivImagen);
        }
    }

    @Override
    public int getItemCount() {
        return cartas.size();
    }

    public class CartaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre;
        private TextView tvPuntosAtaque;
        private TextView tvPuntosDefensa;
        private ImageView ivImagen;

        public CartaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_carta);
            tvPuntosAtaque = itemView.findViewById(R.id.tv_puntos_ataque_carta);
            tvPuntosDefensa = itemView.findViewById(R.id.tv_puntos_defensa_carta);
            ivImagen = itemView.findViewById(R.id.iv_imagen_carta);
        }
    }
}
