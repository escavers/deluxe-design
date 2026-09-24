package com.deluxedesign.app.ui.quotes;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentNewQuoteBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class NewQuoteFragment extends BaseFragment {
  private FragmentNewQuoteBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentNewQuoteBinding.inflate(inflater, parent, false);
    return binding;
  }

  private String projectSignature = "";
  private java.util.List<com.deluxedesign.app.domain.model.Project> options =
      new java.util.ArrayList<>();
  private boolean updating;

  protected void configure() {
    if (vm.user() != null) binding.customer.setText(vm.user().name);
    click(
        R.id.generate,
        () -> vm.createQuote(input(R.id.customer), input(R.id.notes), q -> go(R.id.quote_detail)));
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.domain.model.Project> current =
        AppViewModel.list(vm.projects);
    StringBuilder signature = new StringBuilder();
    for (com.deluxedesign.app.domain.model.Project p : current)
      signature.append(p.id).append(p.name);
    if (!projectSignature.equals(signature.toString())
        || binding.projectSelect.getAdapter() == null) {
      projectSignature = signature.toString();
      options = new java.util.ArrayList<>(current);
      java.util.List<String> names = new java.util.ArrayList<>();
      for (com.deluxedesign.app.domain.model.Project p : options) names.add(p.name);
      if (names.isEmpty()) names.add("Primero guarda un proyecto");
      com.deluxedesign.app.ui.common.Ui.spinner(
          binding.projectSelect,
          names,
          i -> {
            if (!updating
                && !options.isEmpty()
                && !options.get(i).id.equals(vm.value("project", "")))
              vm.set("project", options.get(i).id);
          });
    }
    updating = true;
    for (int i = 0; i < options.size(); i++)
      if (options.get(i).id.equals(vm.value("project", "")))
        binding.projectSelect.setSelection(i, false);
    updating = false;
    com.deluxedesign.app.domain.model.Project p = vm.project();
    binding.generate.setEnabled(!options.isEmpty() && p != null);
    if (p != null) {
      com.deluxedesign.app.domain.model.CustomizationPreset style = vm.preset(p.presetId);
      if (style != null) {
        text(R.id.summary, style.name + "\nGarantía Deluxe: incluida (demo)");
        text(R.id.total, "Total " + com.deluxedesign.app.util.Formatters.money(style.priceCents));
      }
    } else {
      text(R.id.summary, "No hay proyectos guardados. Crea uno desde el catálogo.");
      text(R.id.total, "");
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
