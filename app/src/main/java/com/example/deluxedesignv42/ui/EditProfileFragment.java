package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.databinding.FragmentEditProfileBinding;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSaveProfile.setOnClickListener(v -> {
            String name = binding.edtEditName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getContext(), "Perfil actualizado localmente (DEMO)", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigateUp();
        });

        binding.btnBackEditProfile.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        
        binding.btnGoToChangePass.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Función de cambio de contraseña DEMO", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}