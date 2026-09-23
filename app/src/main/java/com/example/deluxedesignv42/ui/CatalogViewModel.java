package com.example.deluxedesignv42.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.deluxedesignv42.data.DemoRepository;
import com.example.deluxedesignv42.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class CatalogViewModel extends ViewModel {
    private MutableLiveData<List<Vehicle>> filteredVehicles = new MutableLiveData<>();
    private List<Vehicle> allVehicles;
    
    private String currentQuery = "";
    private String currentCategory = "Todos";

    public CatalogViewModel() {
        allVehicles = DemoRepository.getInstance().getCatalog();
        filteredVehicles.setValue(new ArrayList<>(allVehicles));
    }

    public LiveData<List<Vehicle>> getFilteredVehicles() {
        return filteredVehicles;
    }

    public void filter(String query, String category) {
        this.currentQuery = query.toLowerCase().trim();
        this.currentCategory = category;
        applyFilters();
    }

    private void applyFilters() {
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle v : allVehicles) {
            boolean matchesQuery = currentQuery.isEmpty() || 
                    v.getBrand().toLowerCase().contains(currentQuery) || 
                    v.getModel().toLowerCase().contains(currentQuery) || 
                    String.valueOf(v.getYear()).contains(currentQuery);
            
            boolean matchesCategory = currentCategory.equals("Todos") || 
                    v.getCategory().equalsIgnoreCase(currentCategory) ||
                    (currentCategory.equals("Deportivos") && (v.getCategory().equalsIgnoreCase("Supercar") || v.getCategory().equalsIgnoreCase("Sport Coupe")));

            if (matchesQuery && matchesCategory) {
                result.add(v);
            }
        }
        filteredVehicles.setValue(result);
    }
}