package com.deluxedesign.app.ui.home;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentHomeBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class HomeFragment extends BaseFragment {
  private FragmentHomeBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentHomeBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    root.findViewById(R.id.back).setVisibility(View.GONE);
    binding.header.setPaddingRelative(
        getResources().getDimensionPixelSize(R.dimen.space_md),
        12,
        binding.header.getPaddingEnd(),
        12);
    click(R.id.newProject, () -> go(R.id.catalog));
    click(R.id.vehicles, () -> go(R.id.my_vehicles));
    click(R.id.designs, () -> go(R.id.projects));
    click(R.id.branches, () -> go(R.id.branches));
    click(R.id.notifications, () -> go(R.id.notifications));
    click(R.id.viewAllProjects, () -> go(R.id.projects));
    click(R.id.avatar, () -> go(R.id.profile));
    String[] photos = {
      "inspiration_audi",
      "inspiration_bmw",
      "inspiration_pickup",
      "inspiration_golf",
      "inspiration_mustang",
      "inspiration_toyota",
      "inspiration_porsche"
    };
    String[] labels = {
      "Deportivo · Audi",
      "Sedán · BMW",
      "Pickup · Toyota",
      "Compacto · Volkswagen",
      "Mustang · galería suministrada",
      "Toyota · preparación visual",
      "Porsche · detalles deportivos"
    };
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> gallery =
        new java.util.ArrayList<>();
    for (int i = 0; i < photos.length; i++) {
      final String photo = photos[i], label = labels[i];
      gallery.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              label,
              "Referencia visual proporcionada",
              photo,
              "Ver imagen ›",
              -1,
              () -> {
                android.widget.ImageView image = new android.widget.ImageView(requireContext());
                image.setAdjustViewBounds(true);
                com.deluxedesign.app.util.AssetImages.show(image, photo);
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle(label)
                    .setView(image)
                    .setPositiveButton("Cerrar", null)
                    .show();
              }));
    }
    binding.inspirationList.setLayoutManager(
        new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
    binding.inspirationList.setNestedScrollingEnabled(false);
    binding.inspirationList.setAdapter(new com.deluxedesign.app.ui.common.CardAdapter(gallery));
  }

  protected void render() {
    String name = (vm.user() == null || vm.user().name == null || vm.user().name.isEmpty()) ? "Samuel Jimenez" : vm.user().name;
    text(R.id.greeting, name);
    android.widget.ImageView avatar = root.findViewById(R.id.avatar);
    String av = vm.user() == null ? "" : vm.user().avatar;
    if (av != null && av.startsWith("http")) com.deluxedesign.app.util.RemoteImage.load(avatar, av);
    else avatar.setImageResource(R.drawable.brand_logo);
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.Project p : AppViewModel.list(vm.projects)) {
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              p.name,
              p.status + " · " + p.progress + " %",
              "ci_" + p.vehicleId + "_front34",
              "Ver proyecto ›",
              p.progress,
              () -> {
                vm.set("project", p.id);
                go(R.id.project_detail);
              }));
      if (rows.size() == 3) break;
    }
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
