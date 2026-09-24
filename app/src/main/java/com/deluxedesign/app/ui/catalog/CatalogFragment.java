package com.deluxedesign.app.ui.catalog;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentCatalogBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class CatalogFragment extends BaseFragment {
  private FragmentCatalogBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentCatalogBinding.inflate(inflater, parent, false);
    return binding;
  }

  private String brand = "Todas las marcas", year = "Todos los años", type = "Todos", query = "";

  protected void configure() {
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.brandFilter,
        java.util.Arrays.asList("Todas las marcas", "Porsche", "BMW", "Ford"),
        i -> {
          brand = java.util.Arrays.asList("Todas las marcas", "Porsche", "BMW", "Ford").get(i);
          render();
        });
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.yearFilter,
        java.util.Arrays.asList("Todos los años", "2024"),
        i -> {
          year = i == 0 ? "Todos los años" : "2024";
          render();
        });
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.typeFilter,
        java.util.Arrays.asList("Todos", "Deportivo", "SUV", "Sedán", "PickUp"),
        i -> {
          type = java.util.Arrays.asList("Todos", "Deportivo", "SUV", "Sedán", "PickUp").get(i);
          render();
        });
    observeText(
        R.id.search,
        s -> {
          query = s.toLowerCase(java.util.Locale.ROOT);
          render();
        });
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Vehicle v : AppViewModel.list(vm.vehicles)) {
      if (!brand.equals("Todas las marcas") && !brand.equals(v.brand)) continue;
      if (!year.equals("Todos los años") && !year.equals(String.valueOf(v.year))) continue;
      if (!type.equals("Todos") && !type.equals(v.type)) continue;
      if (!(v.name + " " + v.brand + " " + v.year)
          .toLowerCase(java.util.Locale.ROOT)
          .contains(query)) continue;
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              v.name,
              v.type + " | " + v.brand + " · " + v.year,
              "vehicle_" + v.id + "_reference",
              "Ver detalles ›",
              -1,
              () -> {
                vm.selectVehicle(v.id);
                go(R.id.vehicle_detail);
              }));
    }
    text(R.id.count, rows.size() + " vehículos disponibles");
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
