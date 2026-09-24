package com.deluxedesign.app.ui.profile;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentMyVehiclesBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class MyVehiclesFragment extends BaseFragment {
  private FragmentMyVehiclesBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentMyVehiclesBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.catalog, () -> go(R.id.catalog));
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Vehicle v : AppViewModel.list(vm.vehicles))
      if (vm.favorite(v.id))
        rows.add(
            new com.deluxedesign.app.ui.common.CardAdapter.Card(
                v.name,
                v.type + " | " + v.brand,
                v.image,
                "♥ Favorito",
                -1,
                () -> {
                  vm.selectVehicle(v.id);
                  go(R.id.vehicle_detail);
                }));
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
