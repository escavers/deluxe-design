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
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.data.local.ProjectEntity;
import com.example.deluxedesignv42.databinding.FragmentConfirmDesignBinding;

public class ConfirmDesignFragment extends Fragment {
    private FragmentConfirmDesignBinding binding;
    private boolean isSaving = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmDesignBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        String vehicleId = args != null ? args.getString("vehicleId") : "";
        String angle = args != null ? args.getString("angle") : "Frente";
        String color = args != null ? args.getString("color") : "Rojo";
        String finish = args != null ? args.getString("finish") : "Sólido";
        int resId = args != null ? args.getInt("resId") : R.drawable.img_placeholder;
        int existingId = args != null ? args.getInt("existingId", -1) : -1;

        binding.imgConfirmPreview.setImageResource(resId);
        binding.txtConfirmSpecs.setText("Pintura: " + color + "\nAcabado: " + finish + "\nÁngulo: " + angle);

        binding.btnFinalizeConfirm.setOnClickListener(v -> {
            String projName = binding.edtProjectName.getText().toString().trim();
            if (TextUtils.isEmpty(projName)) {
                binding.tilProjectName.setError("El nombre del proyecto es obligatorio");
                return;
            }

            if (isSaving) return;
            isSaving = true;
            binding.btnFinalizeConfirm.setEnabled(false);

            ProjectEntity entity = new ProjectEntity();
            if (existingId != -1) entity.id = existingId;
            entity.name = projName;
            entity.vehicleId = vehicleId;
            entity.angle = angle;
            entity.paintColor = color;
            entity.finishType = finish;
            entity.previewImageResId = resId;
            entity.dateLong = System.currentTimeMillis();
            entity.status = "Borrador";
            entity.progressPercentage = 10;

            if (existingId != -1) {
                ProjectRepository.getInstance(getContext()).updateProject(entity, () -> 
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Proyecto actualizado", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_confirmDesignFragment_to_projectReadyFragment, args);
                    })
                );
            } else {
                ProjectRepository.getInstance(getContext()).saveProject(entity, newId -> 
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Proyecto guardado", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_confirmDesignFragment_to_projectReadyFragment, args);
                    })
                );
            }
        });

        binding.btnBackConfirm.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}