package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentRecoverBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class RecoverFragment extends BaseFragment {
  private FragmentRecoverBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentRecoverBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.reset.setVisibility(View.GONE);
    click(
        R.id.send,
        () -> {
          vm.set("resetEmail", input(R.id.email));
          vm.repositories
              .auth()
              .requestPasswordReset(
                  input(R.id.email),
                  vm.task(
                      code -> {
                        if (!isAdded() || binding == null) return;
                        if (vm.repositories.cloud()) {
                          text(
                              R.id.result,
                              "Revisa tu correo. El enlace permite cambiar tu contraseña.");
                        } else {
                          text(
                              R.id.result,
                              "DEMOSTRACIÓN LOCAL · no se ha enviado un correo.\nCódigo de prueba: "
                                  + code
                                  + "\nVálido durante 10 minutos.");
                          binding.reset.setVisibility(View.VISIBLE);
                        }
                      }));
        });
    click(R.id.reset, () -> go(R.id.reset));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
