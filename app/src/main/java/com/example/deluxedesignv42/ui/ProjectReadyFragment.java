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
import com.example.deluxedesignv42.databinding.FragmentProjectReadyBinding;

public class ProjectReadyFragment extends Fragment {
    private FragmentProjectReadyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectReadyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int resId = getArguments() != null ? getArguments().getInt("resId") : R.drawable.img_placeholder;
        binding.imgReadyPreview.setImageResource(resId);

        binding.btnViewAllProjects.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.projectsFragment)
        );

        binding.btnShareProject.setOnClickListener(v -> {
            Toast.makeText(getContext(), "La exportación real requiere almacenamiento externo en fases avanzadas (DEMO)", Toast.LENGTH_LONG).show();
        });

        binding.btnGenQuoteReady.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.quotesFragment)
        );

        binding.btnBackToHome.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.homeFragment)
        );

        binding.btnBackReady.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}