package com.deluxedesign.app.ui.catalog;

import android.view.*;
import android.widget.TextView;
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

  private static final java.util.List<String> BRANDS =
      java.util.Arrays.asList("Marca", "Porsche", "BMW", "Ford");
  private static final java.util.List<String> TYPES =
      java.util.Arrays.asList("Tipo", "Deportivo", "SUV", "Sedán", "PickUp");

  protected void configure() {
    click(R.id.notifications, () -> go(R.id.notifications));

    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.brandFilter,
        BRANDS,
        i -> {
          brand = i == 0 ? "Todas las marcas" : BRANDS.get(i);
          render();
        });
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.yearFilter,
        java.util.Arrays.asList("Año", "2024", "2023"),
        i -> {
          year = i == 0 ? "Todos los años" : (i == 1 ? "2024" : "2023");
          render();
        });
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.typeFilter,
        TYPES,
        i -> {
          type = i == 0 ? "Todos" : TYPES.get(i);
          updateChips(type);
          render();
        });

    observeText(
        R.id.search,
        s -> {
          query = s.toLowerCase(java.util.Locale.ROOT);
          render();
        });

    setupChip(binding.chipTodos, "Todos");
    setupChip(binding.chipDeportivos, "Deportivo");
    setupChip(binding.chipSUV, "SUV");
    setupChip(binding.chipSedan, "Sedán");
    setupChip(binding.chipPickUp, "PickUp");
  }

  private void setupChip(TextView chip, String category) {
    chip.setOnClickListener(v -> {
      type = category;
      updateChips(category);
      render();
    });
  }

  private void updateChips(String selected) {
    setChipStyle(binding.chipTodos, selected.equals("Todos"));
    setChipStyle(binding.chipDeportivos, selected.equalsIgnoreCase("Deportivo"));
    setChipStyle(binding.chipSUV, selected.equalsIgnoreCase("SUV"));
    setChipStyle(binding.chipSedan, selected.equalsIgnoreCase("Sedán") || selected.equalsIgnoreCase("Sedan"));
    setChipStyle(binding.chipPickUp, selected.equalsIgnoreCase("PickUp"));
  }

  private void setChipStyle(TextView chip, boolean active) {
    chip.setBackgroundResource(active ? R.drawable.bg_filter_chip_active : R.drawable.bg_filter_chip_inactive);
    chip.setTextColor(active ? 0xFFFFFFFF : 0xFFD1D5DB);
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Vehicle v : AppViewModel.list(vm.vehicles)) {
      if (!brand.equals("Todas las marcas") && !brand.equalsIgnoreCase(v.brand)) continue;
      if (!year.equals("Todos los años") && !year.equals(String.valueOf(v.year))) continue;
      if (!type.equals("Todos") && !matchesType(type, v.type)) continue;
      if (!(v.name + " " + v.brand + " " + v.year)
          .toLowerCase(java.util.Locale.ROOT)
          .contains(query)) continue;
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              v.name,
              v.type,
              "ci_" + v.id + "_front34",
              "Ver Detalles",
              -1,
              () -> {
                vm.selectVehicle(v.id);
                go(R.id.vehicle_detail);
              },
              v.id,
              com.deluxedesign.app.util.VehicleImages.DEFAULT_COLOR));
    }
    text(R.id.count, rows.size() + " vehículos disponibles");
    cards(R.id.list, rows);
  }

  private static boolean matchesType(String selected, String vehicleType) {
    if (selected.equalsIgnoreCase(vehicleType)) return true;
    if (selected.equalsIgnoreCase("Deportivo"))
      return vehicleType.equalsIgnoreCase("Coupé")
          || vehicleType.equalsIgnoreCase("Musculoso");
    if (selected.equalsIgnoreCase("Sedán")
        || selected.equalsIgnoreCase("Sedan")
        || selected.equalsIgnoreCase("Coupe"))
      return vehicleType.equalsIgnoreCase("Coupé");
    return false;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
