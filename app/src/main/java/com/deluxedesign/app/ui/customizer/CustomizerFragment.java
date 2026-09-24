package com.deluxedesign.app.ui.customizer;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentCustomizerBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class CustomizerFragment extends BaseFragment {
  private FragmentCustomizerBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentCustomizerBinding.inflate(inflater, parent, false);
    return binding;
  }

  private java.util.List<com.deluxedesign.app.domain.model.CustomizationPreset> options =
      new java.util.ArrayList<>();
  private boolean bindingSpinner;

  protected void configure() {
    click(R.id.undo, () -> vm.undo());
    click(R.id.redo, () -> vm.redo());
    for (int id : new int[] {R.id.front, R.id.side, R.id.rear})
      click(
          id, () -> vm.set("angle", id == R.id.side ? "side" : id == R.id.rear ? "rear" : "front"));
    click(R.id.save, () -> go(R.id.confirmation));
    for (int id :
        new int[] {
          R.id.paint,
          R.id.vinyl,
          R.id.wheels,
          R.id.bodyKit,
          R.id.lights,
          R.id.accessories,
          R.id.interior
        }) click(id, () -> chooseStyle());
  }

  private void chooseStyle() {
    String[] names = new String[options.size()];
    for (int i = 0; i < names.length; i++) names[i] = options.get(i).name;
    new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
        .setTitle("Elegir estilo completo")
        .setItems(names, (d, which) -> vm.selectPreset(options.get(which).id))
        .setNeutralButton(
            "Ver detalle",
            (d, w) -> {
              if (vm.preset() != null)
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle(vm.preset().name)
                    .setMessage(vm.preset().summary())
                    .setPositiveButton("Entendido", null)
                    .show();
            })
        .setNegativeButton("Cancelar", null)
        .show();
  }

  protected void render() {
    com.deluxedesign.app.domain.model.CustomizationPreset p = vm.preset();
    if (p == null) return;
    if (options.isEmpty() || !options.get(0).vehicleId.equals(p.vehicleId)) {
      options = vm.vehiclePresets();
      java.util.List<String> names = new java.util.ArrayList<>();
      for (com.deluxedesign.app.domain.model.CustomizationPreset x : options) names.add(x.name);
      com.deluxedesign.app.ui.common.Ui.spinner(
          binding.presetSelect,
          names,
          i -> {
            if (!bindingSpinner && !options.get(i).id.equals(vm.value("preset", "")))
              vm.selectPreset(options.get(i).id);
          });
    }
    bindingSpinner = true;
    for (int i = 0; i < options.size(); i++)
      if (options.get(i).id.equals(p.id)) binding.presetSelect.setSelection(i, false);
    bindingSpinner = false;
    text(R.id.vehicleName, vm.vehicle() == null ? "" : vm.vehicle().name);
    text(R.id.summary, p.summary());
    text(R.id.price, "Estimado: " + com.deluxedesign.app.util.Formatters.money(p.priceCents));
    com.deluxedesign.app.util.AssetImages.show(binding.hero, p.image(vm.value("angle", "front")));
    binding.undo.setEnabled(vm.canUndo());
    binding.redo.setEnabled(vm.canRedo());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
    options.clear();
  }
}
