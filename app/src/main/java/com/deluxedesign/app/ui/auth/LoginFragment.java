package com.deluxedesign.app.ui.auth;

import android.content.Intent;
import android.view.*;
import com.deluxedesign.app.BuildConfig;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentLoginBinding;
import com.deluxedesign.app.ui.BaseFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;

public class LoginFragment extends BaseFragment {
  private FragmentLoginBinding binding;
  private GoogleSignInClient googleClient;

  private final ActivityResultLauncher<Intent> googleLauncher =
      registerForActivityResult(
          new ActivityResultContracts.StartActivityForResult(),
          result -> {
            try {
              GoogleSignInAccount account =
                  GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                      .getResult(ApiException.class);
              vm.repositories.auth().signInWithGoogle(account.getIdToken(), vm.task(u -> signedIn()));
            } catch (ApiException e) {
              vm.error.setValue(getString(R.string.login_google_error));
            }
          });

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

    boolean cloud = vm.repositories.cloud();
    int clientId =
        getResources()
            .getIdentifier("default_web_client_id", "string", requireContext().getPackageName());
    String fallback = BuildConfig.GOOGLE_WEB_CLIENT_ID;
    boolean hasClient = clientId != 0 || (fallback != null && !fallback.isEmpty());
    binding.googleSignIn.setVisibility(cloud && hasClient ? View.VISIBLE : View.GONE);
    if (cloud && hasClient) {
      String token = clientId != 0 ? getString(clientId) : fallback;
      GoogleSignInOptions options =
          new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .requestIdToken(token)
              .build();
      googleClient = GoogleSignIn.getClient(requireContext(), options);
      binding.googleSignIn.setOnClickListener(
          v -> googleLauncher.launch(googleClient.getSignInIntent()));
    }
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
