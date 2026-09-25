package com.deluxedesign.app.ui.auth;

import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentChangePasswordBinding;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.Validators;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
          String password = input(R.id.password);
          if (!password.equals(input(R.id.confirmation))) {
            vm.error.setValue("Las contraseñas no coinciden.");
            return;
          }
          if (!Validators.password(password)) {
            vm.error.setValue("Usa al menos 8 caracteres.");
            return;
          }
          requestCurrentPassword(password);
        });
  }

  private void requestCurrentPassword(String newPassword) {
    int padding = Math.round(22 * getResources().getDisplayMetrics().density);
    LinearLayout container = new LinearLayout(requireContext());
    container.setPadding(padding, 0, padding, 0);
    EditText current = new EditText(requireContext());
    current.setHint("Contraseña actual");
    current.setSingleLine(true);
    current.setTextColor(Color.WHITE);
    current.setHintTextColor(Color.rgb(127, 130, 140));
    current.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    current.setBackgroundResource(R.drawable.bg_register_field);
    current.setPadding(padding / 2, 0, padding / 2, 0);
    container.addView(
        current,
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            Math.round(48 * getResources().getDisplayMetrics().density)));

    new MaterialAlertDialogBuilder(requireContext())
        .setTitle("Confirma tu identidad")
        .setMessage("Ingresa tu contraseña actual para autorizar el cambio.")
        .setView(container)
        .setNegativeButton("Cancelar", null)
        .setPositiveButton(
            "Actualizar",
            (dialog, which) ->
                vm.repositories
                    .auth()
                    .changePassword(
                        current.getText().toString(),
                        newPassword,
                        vm.task(
                            value -> {
                              toast("Contraseña actualizada.");
                              if (isAdded())
                                requireActivity().getOnBackPressedDispatcher().onBackPressed();
                            })))
        .show();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
