package com.deluxedesign.app.ui.customizer;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentConfirmationBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class ConfirmationFragment extends BaseFragment {
  private FragmentConfirmationBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentConfirmationBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    com.deluxedesign.app.domain.model.Project existing = vm.project();
    binding.projectName.setText(
        existing != null && existing.id.equals(vm.value("editing", ""))
            ? existing.name
            : (vm.vehicle() == null ? "" : vm.vehicle().name)
                + " · "
                + (vm.preset() == null ? "" : vm.preset().name));
    click(R.id.confirm, () -> vm.saveProject(input(R.id.projectName), p -> go(R.id.project_ready)));
  }

  protected void render() {
    if (vm.preset() == null) return;
    text(R.id.summary, vm.preset().summary());
    text(
        R.id.price,
        "Estimado: " + com.deluxedesign.app.util.Formatters.money(vm.preset().priceCents));
    com.deluxedesign.app.util.AssetImages.show(
        binding.hero, vm.preset().image(vm.value("angle", "front")));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
