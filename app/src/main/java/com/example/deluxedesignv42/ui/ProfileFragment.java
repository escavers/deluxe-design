package com.example.deluxedesignv42.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.DemoAuthRepository;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProjectRepository.getInstance(getContext()).getAllProjects(projects -> 
            getActivity().runOnUiThread(() -> {
                binding.txtProjCount.setText(String.valueOf(projects.size()));
            })
        );

        binding.btnEditProfile.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_editProfileFragment)
        );

        binding.btnMyVehiclesList.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.myVehiclesFragment)
        );

        binding.btnMyQuotesList.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.quotesFragment)
        );

        binding.btnMyProjectsList.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.projectsFragment)
        );

        binding.btnProfileNotifications.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.notificationsFragment)
        );

        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que deseas salir?")
                .setPositiveButton("Salir", (dialog, which) -> {
                    DemoAuthRepository.getInstance().logoutDemo();
                    Navigation.findNavController(view).navigate(R.id.welcomeFragment);
                })
                .setNegativeButton("Cancelar", null)
                .show();
        });

        binding.btnBackProfile.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}