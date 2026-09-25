package com.deluxedesign.app.ui.quotes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentQuoteDetailBinding;
import com.deluxedesign.app.databinding.ItemQuoteCostBinding;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.domain.model.Quote;
import com.deluxedesign.app.domain.model.QuoteItem;
import com.deluxedesign.app.domain.model.Vehicle;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.ExportFiles;
import com.deluxedesign.app.util.Formatters;
import com.google.gson.Gson;
import java.io.File;
import java.util.Locale;

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
      File file = ExportFiles.pdf(requireContext(), vm.quote());
      if (share) ExportFiles.sharePdf(requireContext(), file);
      else {
        ExportFiles.savePdf(requireContext(), file);
        toast("PDF guardado en Descargas/DeluxeDesign.");
      }
    } catch (Exception exception) {
      vm.error.setValue("No se pudo generar el PDF: " + exception.getMessage());
    }
  }

  protected void render() {
    Quote quote = vm.quote();
    if (quote == null) return;

    Project project = vm.project(quote.projectId);
    Vehicle vehicle = project == null ? null : vm.vehicle(project.vehicleId);

    binding.vehicleName.setText(
        (vehicle == null ? "COTIZACIÓN DELUXE" : vehicle.name).toUpperCase(new Locale("es")));
    binding.quoteName.setText(quote.id.startsWith("#") ? quote.id : "#" + quote.id);
    binding.status.setText(quote.status);
    binding.status.setBackgroundResource(statusBackground(quote.status));

    QuoteItem[] items = new Gson().fromJson(quote.itemsJson, QuoteItem[].class);
    binding.costRows.removeAllViews();
    if (items != null) {
      for (int index = 0; index < items.length; index++) {
        QuoteItem item = items[index];
        ItemQuoteCostBinding row =
            ItemQuoteCostBinding.inflate(getLayoutInflater(), binding.costRows, false);
        row.costLabel.setText(item.label);
        row.costAmount.setText(Formatters.money(item.amountCents));
        binding.costRows.addView(row.getRoot());
      }
    }
    binding.total.setText(Formatters.money(quote.totalCents));
  }

  private static int statusBackground(String status) {
    if ("Aprobada".equalsIgnoreCase(status)) return R.drawable.bg_badge_aprobada;
    if (status != null && status.toLowerCase(new Locale("es")).contains("revisión"))
      return R.drawable.bg_badge_en_revision;
    return R.drawable.bg_badge_pendiente;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
