package com.example.deluxedesignv42.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentBranchesBinding;
import com.example.deluxedesignv42.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchesFragment extends Fragment {
    private FragmentBranchesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBranchesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Branch> branches = new ArrayList<>();
        branches.add(new Branch("1", "Deluxe Design Centro", "Calle Principal 123", "555-0101"));
        branches.add(new Branch("2", "Deluxe Design Norte", "Avenida Industrial 456", "555-0102"));

        binding.rvBranches.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBranches.setAdapter(new BranchAdapter(branches));
    }

    private class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {
        private List<Branch> list;
        BranchAdapter(List<Branch> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Branch b = list.get(position);
            holder.txtName.setText(b.getName());
            holder.txtAddr.setText(b.getAddress());
            holder.btnMap.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + b.getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(getContext(), "No se encontró una aplicación de mapas", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtName, txtAddr;
            View btnMap;
            ViewHolder(View v) {
                super(v);
                txtName = v.findViewById(R.id.txtBranchName);
                txtAddr = v.findViewById(R.id.txtBranchAddress);
                btnMap = v.findViewById(R.id.btnViewOnMap);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}