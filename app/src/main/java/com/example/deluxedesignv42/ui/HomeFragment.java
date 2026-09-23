package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        binding.btnNewProject.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.catalogFragment)
        );

        binding.btnVerTodosHome.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.projectsFragment)
        );

        binding.gridBranches.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.branchesFragment)
        );
        
        binding.imgHomeNotifications.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.notificationsFragment)
        );

        binding.btnMyVehicles.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.myVehiclesFragment)
        );

        binding.btnMyDesigns.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.projectsFragment));
        binding.imgHomeAvatar.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.profileFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}