package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentRegisterBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class RegisterFragment extends BaseFragment {
  private FragmentRegisterBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentRegisterBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.login, () -> go(R.id.login));
    click(
        R.id.create,
        () -> {
          if (!input(R.id.password).equals(input(R.id.confirmation))) {
            vm.error.setValue("Las contraseñas no coinciden.");
            return;
          }
          vm.repositories
              .auth()
              .register(
                  input(R.id.name),
                  input(R.id.email),
                  input(R.id.phone),
                  input(R.id.password),
                  vm.task(
                      u -> {
                        if (isAdded())
                          androidx.navigation.fragment.NavHostFragment.findNavController(this)
                              .navigate(
                                  R.id.home,
                                  null,
                                  new androidx.navigation.NavOptions.Builder()
                                      .setPopUpTo(R.id.welcome, true)
                                      .build());
                      }));
        });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
