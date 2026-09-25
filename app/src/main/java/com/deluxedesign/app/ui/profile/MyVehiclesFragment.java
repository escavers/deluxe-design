package com.deluxedesign.app.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentMyVehiclesBinding;
import com.deluxedesign.app.databinding.ItemMyVehicleBinding;
import com.deluxedesign.app.domain.model.Vehicle;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import java.util.List;
import java.util.function.Consumer;

public class MyVehiclesFragment extends BaseFragment {
  private FragmentMyVehiclesBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentMyVehiclesBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.catalog, () -> go(R.id.catalog));
    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.list.setNestedScrollingEnabled(false);
  }

  protected void render() {
    List<Vehicle> vehicles = new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Favorite f :
        AppViewModel.list(vm.favorites)) {
      Vehicle v = vm.vehicle(f.vehicleId);
      if (v != null) vehicles.add(v);
    }
    binding.list.setAdapter(
        new VehicleAdapter(
            vehicles,
            vehicle -> {
              vm.selectVehicle(vehicle.id);
              go(R.id.vehicle_detail);
            }));
    binding.empty.setVisibility(vehicles.isEmpty() ? View.VISIBLE : View.GONE);
  }

  private static String displayName(Vehicle vehicle) {
    return "mustang".equals(vehicle.id) ? "Mustang GT 2024" : vehicle.name;
  }

  private static String vehicleType(Vehicle vehicle) {
    if ("bmw".equals(vehicle.id)) return "Coupé";
    if ("mustang".equals(vehicle.id)) return "Musculoso";
    return vehicle.type;
  }

  @DrawableRes
  private static int vehicleImage(Vehicle vehicle) {
    if ("bmw".equals(vehicle.id)) return R.drawable.ci_bmw_front34;
    if ("mustang".equals(vehicle.id)) return R.drawable.ci_mustang_front34;
    return R.drawable.ci_porsche_front34;
  }

  private static final class VehicleAdapter
      extends RecyclerView.Adapter<VehicleAdapter.Holder> {
    private final List<Vehicle> vehicles;
    private final Consumer<Vehicle> onSelected;

    VehicleAdapter(List<Vehicle> vehicles, Consumer<Vehicle> onSelected) {
      this.vehicles = vehicles;
      this.onSelected = onSelected;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(
          ItemMyVehicleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      Vehicle vehicle = vehicles.get(position);
      holder.binding.vehicleName.setText(displayName(vehicle));
      holder.binding.vehicleMeta.setText(vehicleType(vehicle) + " | " + vehicle.brand);
      holder.binding.vehicleImage.setImageResource(vehicleImage(vehicle));
      holder.binding.vehicleImage.setContentDescription("Fotografía de " + displayName(vehicle));
      holder.binding.getRoot().setOnClickListener(view -> onSelected.accept(vehicle));
    }

    @Override
    public int getItemCount() {
      return vehicles.size();
    }

    private static final class Holder extends RecyclerView.ViewHolder {
      final ItemMyVehicleBinding binding;

      Holder(ItemMyVehicleBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
