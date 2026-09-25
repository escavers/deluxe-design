package com.deluxedesign.app.ui.projects;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectsBinding;
import com.deluxedesign.app.databinding.ItemProjectBinding;
import com.deluxedesign.app.domain.model.CustomizationPreset;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.domain.model.Vehicle;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.AssetImages;
import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends BaseFragment {
  private FragmentProjectsBinding binding;
  private String filter = "Activos";

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProjectsBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.pillActivos.setOnClickListener(v -> setFilter("Activos"));
    binding.pillEntregados.setOnClickListener(v -> setFilter("Entregados"));
    binding.pillBorradores.setOnClickListener(v -> setFilter("Borradores"));

    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    updatePillStyles();
  }

  private void setFilter(String selectedFilter) {
    filter = selectedFilter;
    updatePillStyles();
    render();
  }

  private void updatePillStyles() {
    boolean isActivos = filter.equals("Activos");
    boolean isEntregados = filter.equals("Entregados");
    boolean isBorradores = filter.equals("Borradores");

    binding.pillActivos.setBackgroundResource(isActivos ? R.drawable.bg_pill_filter_active : R.drawable.bg_pill_filter_inactive);
    binding.textActivos.setTextColor(isActivos ? 0xFFFFFFFF : 0xFF9CA3AF);

    binding.pillEntregados.setBackgroundResource(isEntregados ? R.drawable.bg_pill_filter_active : R.drawable.bg_pill_filter_inactive);
    binding.textEntregados.setTextColor(isEntregados ? 0xFFFFFFFF : 0xFF9CA3AF);

    binding.pillBorradores.setBackgroundResource(isBorradores ? R.drawable.bg_pill_filter_active : R.drawable.bg_pill_filter_inactive);
    binding.textBorradores.setTextColor(isBorradores ? 0xFFFFFFFF : 0xFF9CA3AF);
  }

  protected void render() {
    List<Project> source = AppViewModel.list(vm.projects);
    List<Project> filtered = new ArrayList<>();

    for (Project p : source) {
      if (filter.equals("Activos") && !p.status.equalsIgnoreCase("Activo") && !p.status.equalsIgnoreCase("En proceso")) continue;
      if (filter.equals("Entregados") && !p.status.equalsIgnoreCase("Entregado") && !p.status.equalsIgnoreCase("Completado")) continue;
      if (filter.equals("Borradores") && !p.status.equalsIgnoreCase("Borrador")) continue;
      filtered.add(p);
    }

    binding.empty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    binding.list.setAdapter(new ProjectAdapter(filtered));
  }

  private class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.Holder> {
    private final List<Project> items;

    ProjectAdapter(List<Project> items) {
      this.items = items;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(ItemProjectBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      Project p = items.get(position);
      ItemProjectBinding b = holder.binding;

      Vehicle v = vm.vehicle(p.vehicleId);
      String vehicleTitle = v != null ? v.name : (p.name.contains("·") ? p.name.split("·")[0].trim() : "Vehículo Deluxe");

      b.projectTitle.setText(vehicleTitle);
      b.projectName.setText(p.name.isEmpty() ? "(nombre del proyecto)" : p.name);

      int progressVal = Math.max(0, Math.min(100, p.progress));
      b.progressBar.setProgress(progressVal);
      b.progressPercent.setText(progressVal + "%");

      String delivery = p.estimatedDelivery.isEmpty() ? "02 Abr 2027" : p.estimatedDelivery;
      b.deliveryDate.setText("Entrega aprox:  " + delivery);

      boolean almostDone = progressVal >= 85 || p.status.equalsIgnoreCase("Casi listo") || p.status.equalsIgnoreCase("Entregado");
      if (almostDone) {
        b.statusBadge.setText("Casi listo");
        b.statusBadge.setBackgroundResource(R.drawable.bg_badge_casi_listo);
        b.statusBadge.setTextColor(0xFF22C55E);
      } else {
        b.statusBadge.setText("En proceso");
        b.statusBadge.setBackgroundResource(R.drawable.bg_badge_en_proceso);
        b.statusBadge.setTextColor(0xFF38BDF8);
      }

      CustomizationPreset preset = vm.preset(p.presetId);
      String img =
          preset != null && preset.image("front") != null && !preset.image("front").isEmpty()
              ? preset.image("front")
              : (v != null ? "ci_" + v.id + "_front34" : "");

      if (!img.isEmpty()) {
        AssetImages.show(b.thumbnail, img);
      }

      b.getRoot().setOnClickListener(view -> {
        vm.set("project", p.id);
        go(R.id.project_detail);
      });
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {
      final ItemProjectBinding binding;

      Holder(ItemProjectBinding b) {
        super(b.getRoot());
        binding = b;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
