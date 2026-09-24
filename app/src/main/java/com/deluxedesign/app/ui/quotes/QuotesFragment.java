package com.deluxedesign.app.ui.quotes;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentQuotesBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class QuotesFragment extends BaseFragment {
  private FragmentQuotesBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentQuotesBinding.inflate(inflater, parent, false);
    return binding;
  }

  private String filter = "Todas";

  protected void configure() {
    click(R.id.create, () -> go(R.id.new_quote));
    com.deluxedesign.app.ui.common.Ui.spinner(
        binding.statusFilter,
        java.util.Arrays.asList("Todas", "Pendiente", "En revisión", "Aprobada"),
        i -> {
          filter = java.util.Arrays.asList("Todas", "Pendiente", "En revisión", "Aprobada").get(i);
          render();
        });
  }

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    int pending = 0, approved = 0, review = 0;
    for (com.deluxedesign.app.domain.model.Quote q : AppViewModel.list(vm.quotes)) {
      if (q.status.equals("Pendiente")) pending++;
      if (q.status.equals("Aprobada")) approved++;
      if (q.status.equals("En revisión")) review++;
      if (!filter.equals("Todas") && !filter.equals(q.status)) continue;
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              q.id,
              q.customerName
                  + "\n"
                  + com.deluxedesign.app.util.Formatters.money(q.totalCents)
                  + " · "
                  + com.deluxedesign.app.util.Formatters.date(q.createdAt),
              "",
              q.status,
              -1,
              () -> {
                vm.set("quote", q.id);
                go(R.id.quote_detail);
              }));
    }
    text(
        R.id.counters,
        "Pendientes " + pending + "  ·  Aprobadas " + approved + "  ·  En revisión " + review);
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
