package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentForgotPasswordBinding;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRecover.setOnClickListener(v -> {
            String email = binding.edtForgotEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Validación exitosa (Modo DEMO).", Toast.LENGTH_LONG).show();
            Navigation.findNavController(v).navigate(R.id.action_forgotPasswordFragment_to_resetPasswordFragment);
        });

        binding.btnBackToLoginFromForgot.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.btnBackForgot.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}