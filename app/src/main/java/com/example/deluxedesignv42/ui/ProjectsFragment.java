package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.databinding.FragmentProjectsBinding;

import java.util.ArrayList;

public class ProjectsFragment extends Fragment {
    private FragmentProjectsBinding binding;
    private ProjectAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProjectsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ProjectAdapter(new ArrayList<>(), entity -> {
            Bundle bundle = new Bundle();
            bundle.putInt("projectId", entity.id);
            Navigation.findNavController(view).navigate(R.id.action_projectsFragment_to_projectDetailFragment, bundle);
        });

        binding.rvProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProjects.setAdapter(adapter);

        loadProjects();
    }

    private void loadProjects() {
        ProjectRepository.getInstance(getContext()).getAllProjects(projects -> 
            getActivity().runOnUiThread(() -> {
                adapter.updateData(projects);
                binding.txtProjectsEmpty.setVisibility(projects.isEmpty() ? View.VISIBLE : View.GONE);
            })
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}