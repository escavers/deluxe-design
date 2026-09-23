package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.DemoRepository;
import com.example.deluxedesignv42.data.FavoritesManager;
import com.example.deluxedesignv42.databinding.FragmentVehicleDetailBinding;
import com.example.deluxedesignv42.model.Vehicle;
import com.example.deluxedesignv42.model.VehicleSpecification;

public class VehicleDetailFragment extends Fragment {
    private FragmentVehicleDetailBinding binding;
    private Vehicle vehicle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVehicleDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String id = getArguments() != null ? getArguments().getString("vehicleId") : "";
        for (Vehicle v : DemoRepository.getInstance().getCatalog()) {
            if (v.getId().equals(id)) {
                vehicle = v;
                break;
            }
        }

        if (vehicle != null) {
            binding.txtDetailName.setText(vehicle.getFullName());
            binding.txtDetailCategory.setText(vehicle.getCategory());
            
            if (!vehicle.getImageResIds().isEmpty()) {
                binding.imgMainVehicle.setImageResource(vehicle.getImageResIds().get(0));
            }

            VehicleSpecification specs = vehicle.getSpecifications();
            String specsText = "• Motor: " + specs.getEngine() + "\n" +
                               "• Transmisión: " + specs.getTransmission() + "\n" +
                               "• Potencia: " + specs.getPower() + "\n" +
                               "• Tracción: " + specs.getTraction() + "\n" +
                               "• Peso: " + specs.getWeight();
            binding.txtSpecEngine.setText(specsText);
            
            updateFavoriteIcon();
        }

        binding.btnBackDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        binding.btnFavoriteDetail.setOnClickListener(v -> {
            if (vehicle != null) {
                FavoritesManager.getInstance().toggleFavorite(vehicle.getId());
                updateFavoriteIcon();
            }
        });

        binding.btnPersonalize.setOnClickListener(v -> {
            if (vehicle != null) {
                Bundle bundle = new Bundle();
                bundle.putString("vehicleId", vehicle.getId());
                Navigation.findNavController(v).navigate(R.id.action_vehicleDetailFragment_to_editorFragment, bundle);
            }
        });
    }

    private void updateFavoriteIcon() {
        if (vehicle != null && FavoritesManager.getInstance().isFavorite(vehicle.getId())) {
            binding.btnFavoriteDetail.setColorFilter(getResources().getColor(R.color.red_primary));
        } else {
            binding.btnFavoriteDetail.clearColorFilter();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}