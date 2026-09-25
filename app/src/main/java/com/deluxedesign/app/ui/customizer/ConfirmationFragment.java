package com.deluxedesign.app.ui.customizer;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.data.OptionsCatalog;
import com.deluxedesign.app.databinding.FragmentConfirmationBinding;
import com.deluxedesign.app.domain.model.CustomizationOption;
import com.deluxedesign.app.domain.model.CustomizationPreset;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.AssetImages;
import com.deluxedesign.app.util.Formatters;

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
            : defaultProjectName());
    click(R.id.edit, () -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    click(R.id.confirm, () -> vm.saveProject(input(R.id.projectName), p -> go(R.id.project_ready)));
  }

  private String defaultProjectName() {
    String vehicle = vm.vehicle() == null ? "" : vm.vehicle().name;
    String template = vm.templateOfCurrent();
    String style;
    if (template == null || template.isEmpty()) style = "Personalizado";
    else style = OptionsCatalog.templateName(template);
    return (vehicle.isEmpty() ? "Proyecto" : vehicle) + " · " + style;
  }

  protected void render() {
    CustomizationPreset config = vm.configuration();
    if (config == null) return;
    StringBuilder summary = new StringBuilder();
    for (String category : OptionsCatalog.CATEGORIES) {
      CustomizationOption option = vm.option(category);
      if (option == null) continue;
      if (summary.length() > 0) summary.append("\n");
      summary
          .append("- ")
          .append(com.deluxedesign.app.util.QuoteCalculator.categoryLabel(category))
          .append(": ")
          .append(option.label);
    }
    text(R.id.summary, summary.length() == 0 ? "" : summary.toString());
    text(
        R.id.price,
        "Estimado: " + Formatters.money(vm.configurationPrice()));

    AssetImages.show(binding.hero, config.image("front"));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}