package com.example.deluxedesignv42.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.model.Vehicle;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder> {

    private List<Vehicle> vehicleList;
    private OnVehicleClickListener listener;

    public interface OnVehicleClickListener {
        void onVehicleClick(Vehicle vehicle);
    }

    public VehicleAdapter(List<Vehicle> vehicleList, OnVehicleClickListener listener) {
        this.vehicleList = vehicleList;
        this.listener = listener;
    }

    public void updateData(List<Vehicle> newList) {
        this.vehicleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_vehicle, parent, false);
        return new VehicleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder, int position) {
        Vehicle vehicle = vehicleList.get(position);
        holder.txtName.setText(vehicle.getFullName());
        holder.txtCategory.setText(vehicle.getCategory());
        if (!vehicle.getImageResIds().isEmpty()) {
            holder.imgThumbnail.setImageResource(vehicle.getImageResIds().get(0));
        } else {
            holder.imgThumbnail.setImageResource(R.drawable.img_placeholder);
        }
        holder.itemView.setOnClickListener(v -> listener.onVehicleClick(vehicle));
    }

    @Override
    public int getItemCount() {
        return vehicleList.size();
    }

    static class VehicleViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView txtName;
        TextView txtCategory;

        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgVehicleThumbnail);
            txtName = itemView.findViewById(R.id.txtVehicleName);
            txtCategory = itemView.findViewById(R.id.txtVehicleCategory);
        }
    }
}