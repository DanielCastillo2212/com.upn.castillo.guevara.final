package com.example.comupncastilloguevarafinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.R;

import java.util.List;

public class DuelistaAdapter extends RecyclerView.Adapter<DuelistaAdapter.ViewHolder> {
    private Context context;
    private List<Duelista> duelistas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Duelista duelist);
    }

    public DuelistaAdapter(Context context, List<Duelista> duelistas, OnItemClickListener listener) {
        this.context = context;
        this.duelistas = duelistas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_duelista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Duelista duelist = duelistas.get(position);

        // Configurar la vista del elemento de la lista con los datos del duelist
        holder.tvNombre.setText(duelist.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(duelist);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return duelistas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_duelista);
        }
    }
    public void setDuelistas(List<Duelista> duelistas) {
        this.duelistas = duelistas;
        notifyDataSetChanged();
    }
}


