package com.example.deluxedesignv42.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.local.ProjectEntity;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<ProjectEntity> list;
    private OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onClick(ProjectEntity entity);
    }

    public ProjectAdapter(List<ProjectEntity> list, OnProjectClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<ProjectEntity> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectEntity project = list.get(position);
        holder.txtName.setText(project.name);
        holder.txtStatus.setText("Estado: " + project.status + " | Avance: " + project.progressPercentage + "% (DEMO)");
        holder.imgThumb.setImageResource(project.previewImageResId);
        holder.itemView.setOnClickListener(v -> listener.onClick(project));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView txtName;
        TextView txtStatus;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.imgProjThumb);
            txtName = itemView.findViewById(R.id.txtProjName);
            txtStatus = itemView.findViewById(R.id.txtProjStatus);
        }
    }
}