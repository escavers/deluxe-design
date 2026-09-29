package com.deluxedesign.app.ui.projects;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectReadyBinding;
import com.deluxedesign.app.domain.model.CustomizationOption;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.VehicleImages;

public class ProjectReadyFragment extends BaseFragment {
  private FragmentProjectReadyBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProjectReadyBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.save, () -> go(R.id.projects));
    click(R.id.home, () -> go(R.id.home));
    click(R.id.quote, () -> go(R.id.new_quote));
    click(
        R.id.export,
        () -> {
          try {
            com.deluxedesign.app.util.ExportFiles.saveImage(
                requireContext(), binding.hero, "Deluxe-" + System.currentTimeMillis());
            toast("Imagen guardada en Imágenes/DeluxeDesign.");
          } catch (Exception e) {
            vm.error.setValue("No se pudo exportar la imagen: " + e.getMessage());
          }
        });
    click(
        R.id.share,
        () -> {
          if (vm.project() != null)
            com.deluxedesign.app.util.ExportFiles.shareText(
                requireContext(),
                vm.project().name + "\n" + (vm.preset() == null ? "" : vm.preset().summary()));
        });
  }

  protected void render() {
    com.deluxedesign.app.domain.model.Project p = vm.project();
    if (p == null) return;
    text(R.id.projectName, p.name);
    text(R.id.projectMeta, p.status + " · " + p.progress + " %");
    com.deluxedesign.app.domain.model.CustomizationOption paint = vm.option("paint");
    binding.hero.setColor(p.vehicleId, paint == null ? VehicleImages.DEFAULT_COLOR : paint.id);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}