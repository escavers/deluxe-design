package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentResetBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class ResetFragment extends BaseFragment {
  private FragmentResetBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentResetBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.email.setText(vm.value("resetEmail", ""));
    click(
        R.id.save,
        () -> {
          if (!input(R.id.password).equals(input(R.id.confirmation))) {
            vm.error.setValue("Las contraseñas no coinciden.");
            return;
          }
          vm.repositories
              .auth()
              .resetPassword(
                  input(R.id.email),
                  input(R.id.token),
                  input(R.id.password),
                  vm.task(
                      x -> {
                        toast("Contraseña actualizada.");
                        go(R.id.login);
                      }));
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
