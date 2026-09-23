package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.databinding.FragmentProjectDetailBinding;

public class ProjectDetailFragment extends Fragment {
    private FragmentProjectDetailBinding binding;
    private int projectId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        projectId = getArguments() != null ? getArguments().getInt("projectId", -1) : -1;

        binding.btnBackProjDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        ProjectRepository.getInstance(getContext()).getProjectById(projectId, entity -> 
            getActivity().runOnUiThread(() -> {
                if (entity != null) {
                    binding.txtProjDetailName.setText(entity.name);
                    binding.imgProjDetailPreview.setImageResource(entity.previewImageResId);
                    binding.txtProjDetailConfig.setText("Detalles guardados:\n• Color: " + entity.paintColor + "\n• Acabado: " + entity.finishType + "\n• Ángulo: " + entity.angle);
                    binding.txtProjDetailStatus.setText("Estado: " + entity.status + " | Avance taller: " + entity.progressPercentage + "% (DEMO)");
                    
                    binding.btnEditProject.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("vehicleId", entity.vehicleId);
                        bundle.putInt("existingId", entity.id);
                        Navigation.findNavController(v).navigate(R.id.editorFragment, bundle);
                    });
                }
            })
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}