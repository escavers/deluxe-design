package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.DemoRepository;
import com.example.deluxedesignv42.data.FavoritesManager;
import com.example.deluxedesignv42.databinding.FragmentMyVehiclesBinding;
import com.example.deluxedesignv42.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class MyVehiclesFragment extends Fragment {
    private FragmentMyVehiclesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyVehiclesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnBackMyVehicles.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnVerCatalogo.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.catalogFragment));

        List<Vehicle> favoriteVehicles = new ArrayList<>();
        List<Vehicle> all = DemoRepository.getInstance().getCatalog();
        for (Vehicle v : all) {
            if (FavoritesManager.getInstance().isFavorite(v.getId())) {
                favoriteVehicles.add(v);
            }
        }

        binding.rvMyVehicles.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMyVehicles.setAdapter(new VehicleAdapter(favoriteVehicles, vehicle -> {
            Bundle bundle = new Bundle();
            bundle.putString("vehicleId", vehicle.getId());
            Navigation.findNavController(view).navigate(R.id.action_global_vehicleDetailFragment, bundle);
        }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}