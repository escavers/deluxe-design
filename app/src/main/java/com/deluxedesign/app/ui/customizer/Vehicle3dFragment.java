package com.deluxedesign.app.ui.customizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentVehicle3dBinding;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.Car3D;

public class Vehicle3dFragment extends BaseFragment {

  private FragmentVehicle3dBinding binding;
  private Car3D car3d;
  private boolean rotating;

  @Override
  protected androidx.viewbinding.ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentVehicle3dBinding.inflate(inflater, parent, false);
    return binding;
  }

  @Override
  protected void configure() {
    String title = getArguments() == null ? "" : getArguments().getString("name", "");
    if (title != null && !title.trim().isEmpty())
      text(R.id.screenTitle, title.toUpperCase(java.util.Locale.ROOT));

    String vehicleId = sanitize(getArguments() == null ? null : getArguments().getString("vehicleId"));
    car3d = new Car3D(binding.viewer, requireContext(), "vehicle_" + vehicleId + ".glb");

    int[] viewButtons = {R.id.viewFront, R.id.viewSide, R.id.viewRear, R.id.viewTop};
    String[] views = {"front34", "side", "rear", "top"};
    for (int i = 0; i < viewButtons.length; i++) {
      final String view = views[i];
      click(viewButtons[i], () -> car3d.js("setPreset('view','" + view + "')"));
    }

    int[] backButtons = {R.id.backGraphite, R.id.backSky, R.id.backMint, R.id.backBlack};
    String[] backdrops = {"graphite", "sky", "mint", "black"};
    for (int i = 0; i < backButtons.length; i++) {
      final String backdrop = backdrops[i];
      click(backButtons[i], () -> car3d.js("setPreset('backdrop','" + backdrop + "')"));
    }

    click(
        R.id.rotateToggle,
        () -> {
          rotating = !rotating;
          car3d.js("setPreset('rotate','" + rotating + "')");
        });
  }

  private String sanitize(@Nullable String value) {
    if (value == null) return "porsche";
    String cleaned = value.replaceAll("[^a-z0-9_]", "");
    return cleaned.isEmpty() ? "porsche" : cleaned;
  }

  @Override
  public void onDestroyView() {
    if (car3d != null) car3d.destroy();
    super.onDestroyView();
    binding = null;
  }
}