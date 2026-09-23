package com.example.deluxedesignv42.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.databinding.FragmentEditorBinding;
import com.example.deluxedesignv42.model.DesignConfiguration;

public class EditorFragment extends Fragment {
    private FragmentEditorBinding binding;
    private EditorViewModel viewModel;
    private boolean hasUnsavedChanges = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditorViewModel.class);

        String vehicleId = getArguments() != null ? getArguments().getString("vehicleId") : "";
        viewModel.initialize(vehicleId);

        setupObservers();
        setupListeners();
    }

    private void setupObservers() {
        viewModel.getCurrentConfig().observe(getViewLifecycleOwner(), config -> {
            updateButtonSelectionUI();
        });

        viewModel.getPreviewImageResId().observe(getViewLifecycleOwner(), resId -> {
            binding.imgEditorPreview.setImageResource(resId);
        });
    }

    private void setupListeners() {
        binding.btnBackEditor.setOnClickListener(v -> handleBackNavigation());
        
        binding.btnSaveEditor.setOnClickListener(v -> {
            DesignConfiguration config = viewModel.getCurrentConfig().getValue();
            if (config != null) {
                Bundle bundle = new Bundle();
                bundle.putString("vehicleId", config.getVehicle().getId());
                bundle.putString("angle", viewModel.getCurrentAngle());
                bundle.putString("color", viewModel.getCurrentPaintColor());
                bundle.putString("finish", viewModel.getCurrentFinishType());
                bundle.putInt("resId", viewModel.getPreviewImageResId().getValue() != null ? viewModel.getPreviewImageResId().getValue() : R.drawable.img_placeholder);
                
                if (getArguments() != null && getArguments().containsKey("existingId")) {
                    bundle.putInt("existingId", getArguments().getInt("existingId"));
                }

                hasUnsavedChanges = false;
                Navigation.findNavController(v).navigate(R.id.action_editorFragment_to_confirmDesignFragment, bundle);
            }
        });

        binding.btnUndo.setOnClickListener(v -> {
            viewModel.undo();
            hasUnsavedChanges = true;
        });
        
        binding.btnRedo.setOnClickListener(v -> {
            viewModel.redo();
            hasUnsavedChanges = true;
        });

        binding.btnAngleFront.setOnClickListener(v -> {
            viewModel.updateAngle("Frente");
            hasUnsavedChanges = true;
        });

        binding.btnOptionSolid.setOnClickListener(v -> viewModel.updatePaint(viewModel.getCurrentPaintColor(), "Sólido"));
        binding.btnOptionMatte.setOnClickListener(v -> viewModel.updatePaint(viewModel.getCurrentPaintColor(), "Mate"));
        binding.btnOptionMetallic.setOnClickListener(v -> viewModel.updatePaint(viewModel.getCurrentPaintColor(), "Metálico"));
        binding.btnOptionPearled.setOnClickListener(v -> viewModel.updatePaint(viewModel.getCurrentPaintColor(), "Perlado"));

        binding.btnColorBlack.setOnClickListener(v -> viewModel.updatePaint("Negro", viewModel.getCurrentFinishType()));
        binding.btnColorBlue.setOnClickListener(v -> viewModel.updatePaint("Azul", viewModel.getCurrentFinishType()));
        binding.btnColorWhite.setOnClickListener(v -> viewModel.updatePaint("Blanco", viewModel.getCurrentFinishType()));
        binding.btnColorRed.setOnClickListener(v -> viewModel.updatePaint("Rojo", viewModel.getCurrentFinishType()));
        binding.btnColorGray.setOnClickListener(v -> viewModel.updatePaint("Gris", viewModel.getCurrentFinishType()));
    }

    private void updateButtonSelectionUI() {
        binding.btnUndo.setEnabled(viewModel.canUndo());
        binding.btnRedo.setEnabled(viewModel.canRedo());
    }

    private void handleBackNavigation() {
        if (hasUnsavedChanges) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cambios sin guardar")
                    .setMessage("Tienes modificaciones pendientes. ¿Deseas descartarlas y salir?")
                    .setPositiveButton("Descartar", (dialog, which) -> Navigation.findNavController(binding.getRoot()).navigateUp())
                    .setNegativeButton("Continuar editando", null)
                    .show();
        } else {
            Navigation.findNavController(binding.getRoot()).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}