package com.deluxedesign.app.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentEditProfileBinding;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.RemoteImage;

public class EditProfileFragment extends BaseFragment {
  private FragmentEditProfileBinding binding;
  private boolean populated;
  private String avatarValue = "";

  private final ActivityResultLauncher<String[]> picker =
      registerForActivityResult(
          new ActivityResultContracts.OpenDocument(),
          uri -> {
            if (uri == null || binding == null) return;
            try {
              requireContext()
                  .getContentResolver()
                  .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
              avatarValue = uri.toString();
              binding.avatar.setImageURI(uri);
            } catch (Exception exception) {
              vm.error.setValue("No se pudo abrir la fotografía.");
            }
          });

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentEditProfileBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.avatarContainer.setOnClickListener(view -> picker.launch(new String[] {"image/*"}));
    click(R.id.changePassword, () -> go(R.id.change_password));
    click(
        R.id.save,
        () -> {
          if (vm.user() == null) return;
          String name = binding.nameInput.getText().toString().trim();
          String phone = binding.phoneInput.getText().toString().trim();
          String address = binding.addressInput.getText().toString().trim();
          vm.set("profileAddress", address);
          vm.repositories
              .auth()
              .updateProfile(
                  name,
                  phone,
                  avatarValue,
                  vm.task(
                      user -> {
                        toast("Perfil guardado.");
                        if (isAdded())
                          requireActivity().getOnBackPressedDispatcher().onBackPressed();
                      }));
        });
  }

  @Override
  protected void render() {
    if (vm.user() == null || populated) return;
    populated = true;
    binding.nameInput.setText(vm.user().name);
    binding.emailInput.setText(vm.user().email);
    binding.phoneInput.setText(vm.user().phone);
    binding.addressInput.setText(
        vm.value("profileAddress", "Av. Los Sauces #23, La Paz, Bolivia"));
    avatarValue = vm.user().avatar == null ? "" : vm.user().avatar;
    showAvatar(avatarValue);
  }

  private void showAvatar(String avatar) {
    if (avatar == null || avatar.isEmpty()) {
      binding.avatar.setImageResource(R.drawable.brand_logo);
      return;
    }
    if (avatar.startsWith("http")) {
      RemoteImage.load(binding.avatar, avatar);
      return;
    }
    try {
      binding.avatar.setImageURI(Uri.parse(avatar));
    } catch (Exception exception) {
      binding.avatar.setImageResource(R.drawable.brand_logo);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
    populated = false;
  }
}
