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
import com.example.deluxedesignv42.databinding.FragmentQuotesBinding;

import java.util.ArrayList;

public class QuotesFragment extends Fragment {
    private FragmentQuotesBinding binding;
    private QuoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnNewQuote.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.action_quotesFragment_to_newQuoteFragment)
        );

        adapter = new QuoteAdapter(new ArrayList<>(), entity -> {
            Bundle bundle = new Bundle();
            bundle.putInt("quoteId", entity.id);
            Navigation.findNavController(view).navigate(R.id.action_quotesFragment_to_quoteDetailFragment, bundle);
        });

        binding.rvQuotes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvQuotes.setAdapter(adapter);

        loadQuotes();
    }

    private void loadQuotes() {
        ProjectRepository.getInstance(getContext()).getAllQuotes(quotes -> 
            getActivity().runOnUiThread(() -> {
                adapter.updateData(quotes);
                binding.txtHistory.setVisibility(quotes.isEmpty() ? View.GONE : View.VISIBLE);
            })
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}