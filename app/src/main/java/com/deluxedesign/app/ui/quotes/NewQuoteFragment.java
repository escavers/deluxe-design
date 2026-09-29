package com.deluxedesign.app.ui.quotes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentNewQuoteBinding;
import com.deluxedesign.app.domain.model.CustomizationPreset;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.ui.common.Ui;
import com.deluxedesign.app.util.Formatters;
import java.util.ArrayList;
import java.util.List;

public class NewQuoteFragment extends BaseFragment {
  private FragmentNewQuoteBinding binding;
  private String projectSignature = "";
  private List<Project> options = new ArrayList<>();
  private boolean updating;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentNewQuoteBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(
        R.id.generate,
        () -> vm.createQuote(input(R.id.customer), input(R.id.notes), q -> go(R.id.quote_detail)));
  }

  protected void render() {
    List<Project> current = AppViewModel.list(vm.projects);
    StringBuilder signature = new StringBuilder();
    for (Project project : current) signature.append(project.id).append(project.name);
    if (!projectSignature.equals(signature.toString())
        || binding.projectSelect.getAdapter() == null) {
      projectSignature = signature.toString();
      options = new ArrayList<>(current);
      if (vm.value("project", "").isEmpty() && !options.isEmpty())
        vm.set("project", options.get(0).id);
      List<String> names = new ArrayList<>();
      for (Project project : options) names.add(project.name);
      if (names.isEmpty()) names.add("Primero guarda un proyecto");
      Ui.spinner(
          binding.projectSelect,
          names,
          index -> {
            if (!updating
                && !options.isEmpty()
                && !options.get(index).id.equals(vm.value("project", "")))
              vm.set("project", options.get(index).id);
          });
    }

    updating = true;
    for (int index = 0; index < options.size(); index++)
      if (options.get(index).id.equals(vm.value("project", "")))
        binding.projectSelect.setSelection(index, false);
    updating = false;

    Project project = vm.project();
    boolean available = !options.isEmpty() && project != null;
    binding.generate.setEnabled(available);
    binding.generate.setAlpha(available ? 1f : .5f);
    if (project == null) {
      binding.projectName.setText("Primero guarda un proyecto");
      binding.projectImage.setImageResource(R.drawable.placeholder_vehicle);
      binding.total.setText("");
      return;
    }

    com.deluxedesign.app.domain.model.Vehicle vehicle = vm.vehicle(project.vehicleId);
    binding.projectName.setText(vehicle == null ? project.name : vehicle.name);
    com.deluxedesign.app.util.VehicleImages.show(
        binding.projectImage, project.vehicleId, vm.projectColor(project));
    Long price = project.priceCents;
    if (price == null) {
      com.deluxedesign.app.domain.model.CustomizationPreset config = vm.configurationFor(project);
      if (config != null) price = config.priceCents;
    }
    binding.total.setText(price == null ? "" : Formatters.money(price));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
