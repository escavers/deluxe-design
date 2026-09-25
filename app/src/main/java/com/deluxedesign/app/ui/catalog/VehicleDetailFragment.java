package com.deluxedesign.app.ui.catalog;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentVehicleDetailBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class VehicleDetailFragment extends BaseFragment {
  private FragmentVehicleDetailBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentVehicleDetailBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    for (int id : new int[] {R.id.front, R.id.side, R.id.rear})
      click(
          id, () -> vm.set("angle", id == R.id.side ? "side" : id == R.id.rear ? "rear" : "front"));
    click(R.id.customize, () -> go(R.id.customizer));
    click(
        R.id.favorite,
        () -> {
          if (vm.user() != null && vm.vehicle() != null)
            vm.repositories
                .vehicleRepository()
                .toggleFavorite(vm.user().id, vm.vehicle().id, vm.task(x -> {}));
        });
  }

  protected void render() {
    com.deluxedesign.app.domain.model.Vehicle v = vm.vehicle();
    com.deluxedesign.app.domain.model.CustomizationPreset p = vm.preset();
    if (v == null) return;
    String angle = vm.value("angle", "front");
    setActive(R.id.front, "front".equals(angle));
    setActive(R.id.side, "side".equals(angle));
    setActive(R.id.rear, "rear".equals(angle));
    text(R.id.vehicleName, v.name);
    text(R.id.vehicleMeta, v.type + " | " + v.brand + " · " + v.year);
    text(
        R.id.specifications,
        "Motor: "
            + v.engine
            + "\nTransmisión: "
            + v.transmission
            + "\nPotencia: "
            + v.power
            + "\nTracción: "
            + v.traction
            + "\nPeso: "
            + v.weight);
    text(
        R.id.favorite,
        vm.favorite(v.id) ? "♥  Guardado en mis vehículos" : "♡  Añadir a mis vehículos");
    if (p != null)
      com.deluxedesign.app.util.AssetImages.show(binding.hero, p.image(vm.value("angle", "front")));
  }

  private void setActive(int id, boolean active) {
    android.view.View v = root.findViewById(id);
    if (v == null) return;
    v.setBackgroundResource(active ? R.drawable.bg_primary : R.drawable.bg_card);
    v.setPressed(active);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
