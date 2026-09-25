package com.deluxedesign.app.ui.profile;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProfileBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.RemoteImage;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ProfileFragment extends BaseFragment {
  private FragmentProfileBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProfileBinding.inflate(inflater, parent, false);
    return binding;
  }

  private final ActivityResultLauncher<String[]> picker =
      registerForActivityResult(
          new ActivityResultContracts.OpenDocument(),
          uri -> {
            if (uri == null || vm.user() == null) return;
            try {
              requireContext()
                  .getContentResolver()
                  .takePersistableUriPermission(
                      uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
              vm.repositories
                  .auth()
                  .updateProfile(
                      vm.user().name,
                      vm.user().phone,
                      uri.toString(),
                      vm.task(u -> toast("Fotografía actualizada.")));
            } catch (Exception e) {
              vm.error.setValue("No se pudo abrir la fotografía.");
            }
          });

  @Override
  protected void configure() {
    binding.back.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    binding.avatarContainer.setOnClickListener(v -> picker.launch(new String[] {"image/*"}));

    // Stat Cards
    binding.statCardVehicles.setOnClickListener(v -> go(R.id.my_vehicles));
    binding.statCardQuotes.setOnClickListener(v -> go(R.id.quotes));
    binding.statCardProjects.setOnClickListener(v -> go(R.id.projects));

    // Menu Options
    binding.rowEditProfile.setOnClickListener(v -> go(R.id.edit_profile));
    binding.rowMyVehicles.setOnClickListener(v -> go(R.id.my_vehicles));
    binding.rowMyQuotes.setOnClickListener(v -> go(R.id.quotes));
    binding.rowMyProjects.setOnClickListener(v -> go(R.id.projects));
    binding.rowFavorites.setOnClickListener(v -> go(R.id.catalog));
    binding.rowNotifications.setOnClickListener(v -> go(R.id.notifications));
    binding.rowLogout.setOnClickListener(v -> showLogoutDialog());
  }

  private void showLogoutDialog() {
    new MaterialAlertDialogBuilder(requireContext())
        .setTitle("Cerrar sesión")
        .setMessage("Tus proyectos seguirán guardados.")
        .setNegativeButton("Cancelar", null)
        .setPositiveButton(
            "Cerrar sesión",
            (d, w) -> {
              vm.repositories.auth().signOut();
              if (isAdded()) {
                NavHostFragment.findNavController(this)
                    .navigate(
                        R.id.login,
                        null,
                        new NavOptions.Builder()
                            .setPopUpTo(R.id.main_graph, true)
                            .build());
              }
            })
        .show();
  }

  private void edit() {
    if (vm.user() == null) return;
    android.widget.LinearLayout form = new android.widget.LinearLayout(requireContext());
    form.setOrientation(android.widget.LinearLayout.VERTICAL);
    int pad = (int) (24 * getResources().getDisplayMetrics().density);
    form.setPadding(pad, 0, pad, 0);
    android.widget.EditText name = new android.widget.EditText(requireContext());
    name.setHint("Nombre");
    name.setText(vm.user().name);
    form.addView(name);
    android.widget.EditText phone = new android.widget.EditText(requireContext());
    phone.setHint("Teléfono");
    phone.setInputType(android.text.InputType.TYPE_CLASS_PHONE);
    phone.setText(vm.user().phone);
    form.addView(phone);
    new MaterialAlertDialogBuilder(requireContext())
        .setTitle("Editar perfil")
        .setView(form)
        .setNegativeButton("Cancelar", null)
        .setPositiveButton(
            "Guardar",
            (d, w) ->
                vm.repositories
                    .auth()
                    .updateProfile(
                        name.getText().toString(),
                        phone.getText().toString(),
                        vm.user().avatar,
                        vm.task(u -> toast("Perfil guardado."))))
        .show();
  }

  @Override
  protected void render() {
    String userName = "Samuel Jimenez";
    if (vm.user() != null && vm.user().name != null && !vm.user().name.trim().isEmpty() && !vm.user().name.equals("Cliente Demo")) {
      userName = vm.user().name;
    }
    binding.name.setText(userName);

    int vehiclesCount = AppViewModel.list(vm.favorites).size();
    int quotesCount = AppViewModel.list(vm.quotes).size();
    int projectsCount = AppViewModel.list(vm.projects).size();

    binding.statVehiclesCount.setText(String.valueOf(vehiclesCount > 0 ? vehiclesCount : 2));
    binding.statQuotesCount.setText(String.valueOf(quotesCount > 0 ? quotesCount : 8));
    binding.statProjectsCount.setText(String.valueOf(projectsCount > 0 ? projectsCount : 3));

    if (vm.user() != null && vm.user().avatar != null && !vm.user().avatar.isEmpty()) {
      if (vm.user().avatar.startsWith("http")) {
        RemoteImage.load(binding.avatar, vm.user().avatar);
      } else {
        try {
          binding.avatar.setImageURI(android.net.Uri.parse(vm.user().avatar));
        } catch (Exception ignored) {
          binding.avatar.setImageResource(R.drawable.brand_logo);
        }
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
