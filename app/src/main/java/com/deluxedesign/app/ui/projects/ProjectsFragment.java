package com.deluxedesign.app.ui.projects;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectsBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class ProjectsFragment extends BaseFragment {
  private FragmentProjectsBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProjectsBinding.inflate(inflater, parent, false);
    return binding;
  }

  private String filter = "Todos";

  protected void configure() {
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.statusFilter,
        java.util.Arrays.asList("Todos", "Activos", "Entregados", "Borradores"),
        i -> {
          filter = java.util.Arrays.asList("Todos", "Activos", "Entregados", "Borradores").get(i);
          render();
        });
    click(R.id.newProject, () -> go(R.id.catalog));
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Project p : AppViewModel.list(vm.projects)) {
      if (filter.equals("Activos") && !p.status.equals("Activo")) continue;
      if (filter.equals("Entregados") && !p.status.equals("Entregado")) continue;
      if (filter.equals("Borradores") && !p.status.equals("Borrador")) continue;
      com.deluxedesign.app.domain.model.CustomizationPreset style = vm.preset(p.presetId);
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              p.name,
              p.status
                  + " · "
                  + p.progress
                  + " %\n"
                  + com.deluxedesign.app.util.Formatters.date(p.createdAt),
              style == null ? "" : style.front,
              p.estimatedDelivery,
              p.progress,
              () -> {
                vm.set("project", p.id);
                go(R.id.project_detail);
              }));
    }
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
