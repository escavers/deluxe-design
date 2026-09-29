package com.deluxedesign.app.ui.catalog;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentVehicleDetailBinding;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.Car3D;

public class VehicleDetailFragment extends BaseFragment {
  private FragmentVehicleDetailBinding binding;
  private Car3D car3d;
  private String currentModel = "";

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentVehicleDetailBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    for (int id : new int[] {R.id.front, R.id.side, R.id.rear})
      click(
          id, () -> {
            String angle =
                id == R.id.side ? "side" : id == R.id.rear ? "rear" : "front34";
            vm.set("angle", angle);
            viewerMove(angle);
          });
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
    if (v == null) return;
    String angle = vm.value("angle", "front34");
    setActive(R.id.front, "front".equals(angle) || "front34".equals(angle));
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

    String model = "vehicle_" + v.id + ".glb";
    if (!model.equals(currentModel)) {
      currentModel = model;
      if (car3d != null) {
        car3d.destroy();
        car3d = null;
      }
      car3d = new Car3D(binding.hero, requireContext(), model);
    }
    viewerMove(angle);
  }

  private void viewerMove(String angle) {
    if (car3d == null) return;
    String preset =
        "side".equals(angle) ? "side" : "rear".equals(angle) ? "rear" : "front34";
    car3d.js("setPreset('view','" + preset + "')");
  }

  private void setActive(int id, boolean active) {
    android.view.View v = root.findViewById(id);
    if (v == null) return;
    v.setBackgroundResource(active ? R.drawable.bg_primary : R.drawable.bg_card);
    v.setPressed(active);
  }

  @Override
  public void onDestroyView() {
    if (car3d != null) {
      car3d.destroy();
      car3d = null;
    }
    super.onDestroyView();
    binding = null;
  }
}