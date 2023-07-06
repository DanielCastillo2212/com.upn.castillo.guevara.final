package com.example.comupncastilloguevarafinal.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.comupncastilloguevarafinal.Entities.Duelista;
import com.example.comupncastilloguevarafinal.R;

import java.util.List;

public class DuelistaAdapter extends RecyclerView.Adapter<DuelistaAdapter.ViewHolder> {
    private Context context;
    private List<Duelista> duelistas;

    public DuelistaAdapter(Context context, List<Duelista> duelistas) {
        this.context = context;
        this.duelistas = duelistas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duelista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Duelista duelista = duelistas.get(position);
        holder.tvNombre.setText(duelista.getNombre());
    }

    @Override
    public int getItemCount() {
        return duelistas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_duelista);
        }
    }
}
