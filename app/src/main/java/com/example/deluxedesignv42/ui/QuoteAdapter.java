package com.example.deluxedesignv42.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.local.QuoteEntity;

import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private List<QuoteEntity> list;
    private OnQuoteClickListener listener;

    public interface OnQuoteClickListener {
        void onClick(QuoteEntity entity);
    }

    public QuoteAdapter(List<QuoteEntity> list, OnQuoteClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<QuoteEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        QuoteEntity quote = list.get(position);
        holder.txtProjName.setText(quote.projectName);
        holder.txtVehicle.setText("Vehículo: " + quote.vehicleName + " | Cliente: " + quote.clientName);
        holder.txtStatus.setText("Estado: " + quote.status + " (DEMO)");
        holder.itemView.setOnClickListener(v -> listener.onClick(quote));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class QuoteViewHolder extends RecyclerView.ViewHolder {
        TextView txtProjName, txtVehicle, txtStatus;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProjName = itemView.findViewById(R.id.txtQuoteProjName);
            txtVehicle = itemView.findViewById(R.id.txtQuoteVehicle);
            txtStatus = itemView.findViewById(R.id.txtQuoteStatusLabel);
        }
    }
}