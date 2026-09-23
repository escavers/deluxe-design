package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentCatalogBinding;
import com.example.deluxedesignv42.model.Vehicle;

import java.util.ArrayList;

public class CatalogFragment extends Fragment {
    private FragmentCatalogBinding binding;
    private CatalogViewModel viewModel;
    private VehicleAdapter adapter;
    private String selectedCategory = "Todos";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CatalogViewModel.class);

        adapter = new VehicleAdapter(new ArrayList<>(), vehicle -> {
            Bundle bundle = new Bundle();
            bundle.putString("vehicleId", vehicle.getId());
            Navigation.findNavController(view).navigate(R.id.action_catalogFragment_to_vehicleDetailFragment, bundle);
        });

        binding.rvCatalog.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCatalog.setAdapter(adapter);

        viewModel.getFilteredVehicles().observe(getViewLifecycleOwner(), vehicles -> {
            adapter.updateData(vehicles);
            binding.txtResults.setVisibility(vehicles.isEmpty() ? View.VISIBLE : View.GONE);
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filter(s.toString(), selectedCategory);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        setupCategoryChips();
        
        binding.txtResults.setOnClickListener(v -> {
            binding.edtSearch.setText("");
            updateChips("Todos");
        });
    }

    private void setupCategoryChips() {
        binding.chipAll.setOnClickListener(v -> updateChips("Todos"));
        binding.chipSports.setOnClickListener(v -> updateChips("Deportivos"));
        binding.chipSuv.setOnClickListener(v -> updateChips("SUV"));
        binding.chipSedan.setOnClickListener(v -> updateChips("Sedán"));
        binding.chipPickUp.setOnClickListener(v -> updateChips("PickUp"));
    }

    private void updateChips(String category) {
        selectedCategory = category;
        resetChipStyles();
        
        Button selectedButton;
        if (category.equals("Todos")) selectedButton = binding.chipAll;
        else if (category.equals("Deportivos")) selectedButton = binding.chipSports;
        else if (category.equals("SUV")) selectedButton = binding.chipSuv;
        else if (category.equals("Sedán")) selectedButton = binding.chipSedan;
        else selectedButton = binding.chipPickUp;

        selectedButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.red_primary)));
        viewModel.filter(binding.edtSearch.getText().toString(), selectedCategory);
    }

    private void resetChipStyles() {
        int charcoal = getResources().getColor(R.color.charcoal);
        binding.chipAll.setBackgroundTintList(android.content.res.ColorStateList.valueOf(charcoal));
        binding.chipSports.setBackgroundTintList(android.content.res.ColorStateList.valueOf(charcoal));
        binding.chipSuv.setBackgroundTintList(android.content.res.ColorStateList.valueOf(charcoal));
        binding.chipSedan.setBackgroundTintList(android.content.res.ColorStateList.valueOf(charcoal));
        binding.chipPickUp.setBackgroundTintList(android.content.res.ColorStateList.valueOf(charcoal));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}