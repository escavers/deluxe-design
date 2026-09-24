package com.deluxedesign.app.ui.quotes;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentQuoteDetailBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class QuoteDetailFragment extends BaseFragment {
  private FragmentQuoteDetailBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentQuoteDetailBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.share, () -> export(true));
    click(R.id.download, () -> export(false));
  }

  private void export(boolean share) {
    if (vm.quote() == null) return;
    try {
      java.io.File file = com.deluxedesign.app.util.ExportFiles.pdf(requireContext(), vm.quote());
      if (share) com.deluxedesign.app.util.ExportFiles.sharePdf(requireContext(), file);
      else {
        com.deluxedesign.app.util.ExportFiles.savePdf(requireContext(), file);
        toast("PDF guardado en Descargas/DeluxeDesign.");
      }
    } catch (Exception e) {
      vm.error.setValue("No se pudo generar el PDF: " + e.getMessage());
    }
  }

  protected void render() {
    com.deluxedesign.app.domain.model.Quote q = vm.quote();
    if (q == null) return;
    text(R.id.quoteName, q.id);
    text(R.id.status, q.status + " · " + com.deluxedesign.app.util.Formatters.date(q.createdAt));
    text(R.id.customer, q.customerName);
    StringBuilder body = new StringBuilder();
    com.deluxedesign.app.domain.model.QuoteItem[] items =
        new com.google.gson.Gson()
            .fromJson(q.itemsJson, com.deluxedesign.app.domain.model.QuoteItem[].class);
    for (com.deluxedesign.app.domain.model.QuoteItem i : items)
      body.append(i.label)
          .append("\n")
          .append(com.deluxedesign.app.util.Formatters.money(i.amountCents))
          .append("\n\n");
    text(R.id.items, body.toString());
    text(R.id.total, "Total " + com.deluxedesign.app.util.Formatters.money(q.totalCents));
    text(R.id.notes, q.notes);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
