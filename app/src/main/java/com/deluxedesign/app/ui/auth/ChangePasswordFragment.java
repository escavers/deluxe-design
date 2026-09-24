package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentChangePasswordBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class ChangePasswordFragment extends BaseFragment {
  private FragmentChangePasswordBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentChangePasswordBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(
        R.id.save,
        () -> {
          if (!input(R.id.password).equals(input(R.id.confirmation))) {
            vm.error.setValue("Las contraseñas no coinciden.");
            return;
          }
          vm.repositories
              .auth()
              .changePassword(
                  input(R.id.current),
                  input(R.id.password),
                  vm.task(
                      x -> {
                        toast("Contraseña actualizada.");
                        go(R.id.profile);
                      }));
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
