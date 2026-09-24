package com.deluxedesign.app.ui.projects;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectReadyBinding;
import com.deluxedesign.app.ui.BaseFragment;

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
    for (int id : new int[] {R.id.front, R.id.side, R.id.rear})
      click(
          id, () -> vm.set("angle", id == R.id.side ? "side" : id == R.id.rear ? "rear" : "front"));
    click(
        R.id.gallery,
        () ->
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Galería")
                .setItems(
                    new String[] {"Frontal", "Lateral", "Trasera"},
                    (d, i) -> vm.set("angle", new String[] {"front", "side", "rear"}[i]))
                .show());
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
    com.deluxedesign.app.domain.model.CustomizationPreset preset = vm.preset(p.presetId);
    if (preset != null)
      com.deluxedesign.app.util.AssetImages.show(
          binding.hero, preset.image(vm.value("angle", "front")));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
