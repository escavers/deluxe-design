package com.example.deluxedesignv42.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.deluxedesignv42.data.ProjectRepository;
import com.example.deluxedesignv42.databinding.FragmentQuoteDetailBinding;

public class QuoteDetailFragment extends Fragment {
    private FragmentQuoteDetailBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentQuoteDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int quoteId = getArguments() != null ? getArguments().getInt("quoteId", -1) : -1;

        binding.btnBackQuoteDetail.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        ProjectRepository.getInstance(getContext()).getAllQuotes(quotes -> 
            getActivity().runOnUiThread(() -> {
                com.example.deluxedesignv42.data.local.QuoteEntity currentQuote = null;
                for (com.example.deluxedesignv42.data.local.QuoteEntity q : quotes) {
                    if (q.id == quoteId) {
                        currentQuote = q;
                        break;
                    }
                }

                if (currentQuote != null) {
                    String info = "Identificador Solicitud: #" + currentQuote.id + "\n" +
                                  "Proyecto vinculado: " + currentQuote.projectName + "\n" +
                                  "Cliente: " + currentQuote.clientName + "\n" +
                                  "Notas adicionales:\n" + currentQuote.notes + "\n\n" +
                                  "• Presupuesto formal: Pendiente de presupuesto del taller (DEMO).";
                    binding.txtQuoteDetailInfo.setText(info);
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