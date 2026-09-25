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
    click(
        R.id.save,
        () -> {
          String password = input(R.id.password);
          String confirmation = input(R.id.confirmation);
          String repeat = input(R.id.repeat);
          if (password.isEmpty() || confirmation.isEmpty() || repeat.isEmpty()) {
            vm.error.setValue("Completa todos los campos.");
            return;
          }
          if (!com.deluxedesign.app.util.Validators.password(password)) {
            vm.error.setValue("La contraseña debe tener al menos 8 caracteres.");
            return;
          }
          if (!password.equals(confirmation) || !password.equals(repeat)) {
            vm.error.setValue("Las contraseñas no coinciden.");
            return;
          }
          vm.repositories
              .auth()
              .resetPassword(
                  vm.value("resetEmail", ""),
                  vm.value("resetCode", ""),
                  password,
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
