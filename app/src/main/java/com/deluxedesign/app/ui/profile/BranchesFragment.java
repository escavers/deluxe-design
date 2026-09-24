package com.deluxedesign.app.ui.profile;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentBranchesBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class BranchesFragment extends BaseFragment {
  private FragmentBranchesBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentBranchesBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {}

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Branch b : AppViewModel.list(vm.branches))
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              b.name,
              b.address + "\n" + b.hours + "\n" + b.phone,
              "",
              "Ver en mapa ›",
              -1,
              () -> {
                try {
                  String q =
                      android.net.Uri.encode(b.latitude + "," + b.longitude + "(" + b.name + ")");
                  startActivity(
                      new android.content.Intent(
                          android.content.Intent.ACTION_VIEW,
                          android.net.Uri.parse(
                              "geo:" + b.latitude + "," + b.longitude + "?q=" + q)));
                } catch (android.content.ActivityNotFoundException e) {
                  toast("No hay una aplicación de mapas instalada.");
                }
              }));
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
