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

    // If active filter is selected and list is empty in demo, display demo sample projects
    if (filtered.isEmpty() && filter.equals("Activos") && source.isEmpty()) {
      Project p1 = new Project();
      p1.id = "demo_bmw";
      p1.name = "(nombre del proyecto)";
      p1.vehicleId = "bmw";
      p1.presetId = "bmw_racing_red";
      p1.status = "En proceso";
      p1.progress = 45;
      p1.estimatedDelivery = "02 Abr 2027";
      filtered.add(p1);

      Project p2 = new Project();
      p2.id = "demo_tacoma";
      p2.name = "(nombre del proyecto)";
      p2.vehicleId = "mustang";
      p2.presetId = "mustang_urban_dark";
      p2.status = "Casi listo";
      p2.progress = 92;
      p2.estimatedDelivery = "16 Sep 2027";
      filtered.add(p2);

      Project p3 = new Project();
      p3.id = "demo_r8";
      p3.name = "(nombre del proyecto)";
      p3.vehicleId = "porsche";
      p3.presetId = "porsche_street_blue";
      p3.status = "En proceso";
      p3.progress = 16;
      p3.estimatedDelivery = "26 Ene 2028";
      filtered.add(p3);
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
      if (p.id.equals("demo_bmw")) vehicleTitle = "BMW M4 Competition";
      if (p.id.equals("demo_tacoma")) vehicleTitle = "Toyota Tacoma";
      if (p.id.equals("demo_r8")) vehicleTitle = "Audi R8";
      if (p.id.equals("demo_golf")) vehicleTitle = "Golf GTI";

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
      String img = preset != null ? preset.front : (v != null ? "vehicle_" + v.id + "_reference" : "");
      if (p.id.equals("demo_tacoma")) img = "inspiration_pickup";
      if (p.id.equals("demo_r8")) img = "inspiration_audi";
      if (p.id.equals("demo_bmw")) img = "vehicle_bmw_reference";

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
