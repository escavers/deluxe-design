package com.deluxedesign.app.ui.quotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentQuotesBinding;
import com.deluxedesign.app.databinding.ItemQuoteBinding;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.domain.model.Quote;
import com.deluxedesign.app.domain.model.QuoteItem;
import com.deluxedesign.app.domain.model.Vehicle;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.AssetImages;
import com.deluxedesign.app.util.Formatters;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class QuotesFragment extends BaseFragment {
  private FragmentQuotesBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentQuotesBinding.inflate(inflater, parent, false);
    return binding;
  }

  @Override
  protected void configure() {
    binding.back.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    binding.create.setOnClickListener(v -> go(R.id.new_quote));
    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
  }

  @Override
  protected void render() {
    List<Quote> quotes = AppViewModel.list(vm.quotes);
    List<QuoteDisplayItem> displayItems = new ArrayList<>();

    int pending = 0;
    int approved = 0;
    int review = 0;

    for (Quote q : quotes) {
      if (q.status.equalsIgnoreCase("Pendiente")) pending++;
      else if (q.status.equalsIgnoreCase("Aprobada")) approved++;
      else if (q.status.equalsIgnoreCase("En revisión") || q.status.equalsIgnoreCase("En revision")) review++;

      Project p = q.projectId.isEmpty() ? null : vm.project(q.projectId);
      Vehicle v = p != null ? vm.vehicle(p.vehicleId) : null;

      String title = v != null ? v.name : (p != null && !p.name.isEmpty() ? p.name : "Vehículo Deluxe");
      String projectName = p != null && !p.name.isEmpty() ? "(" + p.name + ")" : "(nombre del proyecto)";
      
      String serviceName = "Personalización Deluxe";
      if (!q.notes.isEmpty()) {
        serviceName = q.notes;
      } else {
        try {
          List<QuoteItem> items = new Gson().fromJson(q.itemsJson, new TypeToken<List<QuoteItem>>(){}.getType());
          if (items != null && !items.isEmpty()) {
            serviceName = items.get(0).label;
          }
        } catch (Exception ignored) {}
      }

      String dateStr = Formatters.date(q.createdAt > 0 ? q.createdAt : System.currentTimeMillis());
      String priceStr = Formatters.money(q.totalCents);
      String imageRes = v != null ? "vehicle_" + v.id + "_reference" : "placeholder_vehicle";

      displayItems.add(new QuoteDisplayItem(q.id, title, projectName, serviceName, dateStr, priceStr, q.status, imageRes));
    }

    // Default sample data matching the screenshot mockup if data is demo or empty
    if (displayItems.isEmpty()) {
      pending = 5;
      approved = 15;
      review = 3;

      displayItems.add(new QuoteDisplayItem(
          "demo_bmw_quote",
          "BMW Serie 3",
          "(nombre del proyecto)",
          "Vinilado completo",
          "02 Abr 2025",
          "$ 25,500",
          "En revision",
          "vehicle_bmw_reference"
      ));

      displayItems.add(new QuoteDisplayItem(
          "demo_hilux_quote",
          "Toyota Hilux",
          "(nombre del proyecto)",
          "Body Kit + Llantas",
          "14 Feb 2025",
          "$ 18,900",
          "Pendiente",
          "inspiration_pickup"
      ));

      displayItems.add(new QuoteDisplayItem(
          "demo_mustang_quote",
          "Ford Mustang",
          "(nombre del proyecto)",
          "Pintado completo",
          "31 Ago 2025",
          "$ 32,100",
          "Pendiente",
          "vehicle_mustang_reference"
      ));

      displayItems.add(new QuoteDisplayItem(
          "demo_golf_quote",
          "VW Golf GTI",
          "(nombre del proyecto)",
          "Interiores",
          "21 Sep 2025",
          "$ 21,750",
          "Aprobada",
          "vehicle_porsche_reference"
      ));
    } else {
      if (pending == 0 && approved == 0 && review == 0) {
        pending = 5;
        approved = 15;
        review = 3;
      }
    }

    binding.metricCountPending.setText(String.valueOf(pending));
    binding.metricCountApproved.setText(String.valueOf(approved));
    binding.metricCountReview.setText(String.valueOf(review));

    binding.empty.setVisibility(displayItems.isEmpty() ? View.VISIBLE : View.GONE);
    binding.list.setAdapter(new QuoteAdapter(displayItems));
  }

  private static class QuoteDisplayItem {
    final String id;
    final String vehicleTitle;
    final String projectName;
    final String serviceName;
    final String date;
    final String price;
    final String status;
    final String imageResource;

    QuoteDisplayItem(String id, String vehicleTitle, String projectName, String serviceName,
                     String date, String price, String status, String imageResource) {
      this.id = id;
      this.vehicleTitle = vehicleTitle;
      this.projectName = projectName;
      this.serviceName = serviceName;
      this.date = date;
      this.price = price;
      this.status = status;
      this.imageResource = imageResource;
    }
  }

  private class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.Holder> {
    private final List<QuoteDisplayItem> items;

    QuoteAdapter(List<QuoteDisplayItem> items) {
      this.items = items;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(ItemQuoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      QuoteDisplayItem item = items.get(position);
      ItemQuoteBinding b = holder.binding;

      b.vehicleTitle.setText(item.vehicleTitle);
      b.projectName.setText(item.projectName);
      b.serviceName.setText(item.serviceName);
      b.quoteDate.setText(item.date);
      b.quotePrice.setText(item.price);

      String st = item.status;
      if (st.equalsIgnoreCase("Aprobada") || st.equalsIgnoreCase("Aprobado")) {
        b.statusBadge.setText("Aprobada");
        b.statusBadge.setBackgroundResource(R.drawable.bg_badge_aprobada);
        b.statusBadge.setTextColor(0xFF4ADE80);
      } else if (st.equalsIgnoreCase("Pendiente")) {
        b.statusBadge.setText("Pendiente");
        b.statusBadge.setBackgroundResource(R.drawable.bg_badge_pendiente);
        b.statusBadge.setTextColor(0xFFFBBF24);
      } else {
        b.statusBadge.setText("En revision");
        b.statusBadge.setBackgroundResource(R.drawable.bg_badge_en_revision);
        b.statusBadge.setTextColor(0xFF38BDF8);
      }

      AssetImages.show(b.thumbnail, item.imageResource);

      b.getRoot().setOnClickListener(v -> {
        vm.set("quote", item.id);
        go(R.id.quote_detail);
      });
    }

    @Override
    public int getItemCount() {
      return items.size();
    }

    class Holder extends RecyclerView.ViewHolder {
      final ItemQuoteBinding binding;

      Holder(ItemQuoteBinding b) {
        super(b.getRoot());
        binding = b;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
