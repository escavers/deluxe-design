package com.deluxedesign.app.ui.profile;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProfileBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class ProfileFragment extends BaseFragment {
  private FragmentProfileBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProfileBinding.inflate(inflater, parent, false);
    return binding;
  }

  private final androidx.activity.result.ActivityResultLauncher<String[]> picker =
      registerForActivityResult(
          new androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
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

  protected void configure() {
    click(R.id.vehicles, () -> go(R.id.my_vehicles));
    click(R.id.projects, () -> go(R.id.projects));
    click(R.id.quotes, () -> go(R.id.quotes));
    click(R.id.notifications, () -> go(R.id.notifications));
    click(R.id.password, () -> go(R.id.change_password));
    click(R.id.photo, () -> picker.launch(new String[] {"image/*"}));
    click(R.id.edit, () -> edit());
    click(
        R.id.logout,
        () ->
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("Tus proyectos seguirán guardados.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(
                    "Cerrar sesión",
                    (d, w) -> {
                      vm.repositories.auth().signOut();
                      if (isAdded())
                        androidx.navigation.fragment.NavHostFragment.findNavController(this)
                            .navigate(
                                R.id.login,
                                null,
                                new androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(R.id.main_graph, true)
                                    .build());
                    })
                .show());
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
    new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
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

  protected void render() {
    if (vm.user() == null) return;
    text(R.id.name, vm.user().name);
    text(R.id.email, vm.user().email);
    text(
        R.id.counters,
        AppViewModel.list(vm.favorites).size()
            + " vehículos  ·  "
            + AppViewModel.list(vm.projects).size()
            + " proyectos  ·  "
            + AppViewModel.list(vm.quotes).size()
            + " cotizaciones");
    if (!vm.user().avatar.isEmpty()) {
      if (vm.user().avatar.startsWith("http")) {
        com.deluxedesign.app.util.RemoteImage.load(binding.avatar, vm.user().avatar);
      } else
        try {
          binding.avatar.setImageURI(android.net.Uri.parse(vm.user().avatar));
        } catch (Exception ignored) {
          binding.avatar.setImageResource(R.drawable.ic_brand);
        }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
