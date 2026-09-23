package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.data.local.ProjectEntity;
import com.example.deluxedesignv42.data.local.QuoteEntity;
import com.example.deluxedesignv42.databinding.FragmentNewQuoteBinding;

import java.util.ArrayList;
import java.util.List;

public class NewQuoteFragment extends Fragment {
    private FragmentNewQuoteBinding binding;
    private List<ProjectEntity> availableProjects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewQuoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProjectRepository.getInstance(getContext()).getAllProjects(projects -> 
            getActivity().runOnUiThread(() -> {
                availableProjects = projects;
                List<String> names = new ArrayList<>();
                for (ProjectEntity p : projects) {
                    names.add(p.name + " (" + p.paintColor + ")");
                }
                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerProjects.setAdapter(adapter);
                }
            })
        );

        binding.btnSubmitQuote.setOnClickListener(v -> {
            String clientName = binding.edtClientName.getText().toString().trim();
            if (TextUtils.isEmpty(clientName)) {
                binding.tilClientName.setError("Tu nombre es obligatorio");
                return;
            }

            if (availableProjects.isEmpty()) {
                Toast.makeText(getContext(), "Debes tener al menos un proyecto para cotizar", Toast.LENGTH_SHORT).show();
                return;
            }

            int position = binding.spinnerProjects.getSelectedItemPosition();
            ProjectEntity selectedProject = availableProjects.get(position);

            QuoteEntity quote = new QuoteEntity();
            quote.projectId = selectedProject.id;
            quote.projectName = selectedProject.name;
            quote.vehicleName = "Coche Ref: " + selectedProject.vehicleId;
            quote.clientName = clientName;
            quote.notes = binding.edtQuoteNotes.getText().toString().trim();
            quote.status = "Pendiente de revisión";
            quote.dateLong = System.currentTimeMillis();
            quote.estimatedAmount = 0.0;

            ProjectRepository.getInstance(getContext()).saveQuote(quote, newId -> 
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Solicitud enviada con éxito", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("quoteId", newId.intValue());
                    Navigation.findNavController(view).navigate(R.id.action_newQuoteFragment_to_quoteDetailFragment, bundle);
                })
            );
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}