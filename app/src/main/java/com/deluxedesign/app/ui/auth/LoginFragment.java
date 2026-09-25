package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentLoginBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class LoginFragment extends BaseFragment {
  private FragmentLoginBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentLoginBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.showPassword.setOnCheckedChangeListener(
        (b, checked) -> {
          binding.password.setTransformationMethod(
              checked ? null : android.text.method.PasswordTransformationMethod.getInstance());
          binding.password.setSelection(binding.password.length());
        });
    click(
        R.id.login,
        () -> {
          String error =
              com.deluxedesign.app.util.Validators.credentials(
                  input(R.id.email), input(R.id.password));
          if (error != null) {
            vm.error.setValue(error);
            return;
          }
          vm.repositories
              .auth()
              .signIn(input(R.id.email), input(R.id.password), vm.task(u -> signedIn()));
        });
    click(R.id.register, () -> go(R.id.register));
    click(R.id.recover, () -> go(R.id.recover));
    binding.register.setText(
        android.text.Html.fromHtml(
            "¿No tienes cuenta? <font color='#FF383C'><b>Regístrate</b></font>",
            android.text.Html.FROM_HTML_MODE_COMPACT));
    binding.demoInfo.setVisibility(vm.repositories.cloud() ? View.GONE : View.VISIBLE);
  }

  private void signedIn() {
    if (isAdded() && getView() != null)
      androidx.navigation.fragment.NavHostFragment.findNavController(this)
          .navigate(
              R.id.home,
              null,
              new androidx.navigation.NavOptions.Builder().setPopUpTo(R.id.welcome, true).build());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
