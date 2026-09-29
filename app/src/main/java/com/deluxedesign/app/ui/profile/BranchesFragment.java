package com.deluxedesign.app.ui.profile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentBranchesBinding;
import com.deluxedesign.app.databinding.ItemBranchBinding;
import com.deluxedesign.app.domain.model.Branch;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BranchesFragment extends BaseFragment {
  private static final String OSM_EMBED =
      "https://www.openstreetmap.org/export/embed.html?bbox=%f,%f,%f,%f&layer=mapnik&marker=%f,%f";

  private FragmentBranchesBinding binding;
  private boolean mapFailed;
  private String selectedId = "";

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentBranchesBinding.inflate(inflater, parent, false);
    return binding;
  }

  @SuppressLint("SetJavaScriptEnabled")
  protected void configure() {
    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.list.setNestedScrollingEnabled(false);

    binding.mapWeb.setBackgroundColor(Color.TRANSPARENT);
    binding.mapWeb.getSettings().setJavaScriptEnabled(true);
    binding.mapWeb.getSettings().setDomStorageEnabled(true);
    binding.mapWeb.getSettings().setBuiltInZoomControls(false);
    binding.mapWeb.setWebViewClient(
        new WebViewClient() {
          @Override
          public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            mapFailed = false;
            if (binding != null) binding.mapSpinner.setVisibility(View.VISIBLE);
          }

          @Override
          public void onPageFinished(WebView view, String url) {
            if (binding == null) return;
            binding.mapSpinner.setVisibility(View.GONE);
            if (mapFailed) {
              binding.mapWeb.setVisibility(View.INVISIBLE);
              return;
            }
            binding.mapWeb.setVisibility(View.VISIBLE);
            view.setAlpha(0f);
            view.animate().alpha(1f).setDuration(300).start();
          }

          @Override
          public void onReceivedError(
              WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
              mapFailed = true;
              if (binding != null) binding.mapSpinner.setVisibility(View.GONE);
            }
          }
        });
  }

  protected void render() {
    List<Branch> branches = AppViewModel.list(vm.branches);
    StringBuilder subtitle = new StringBuilder();
    subtitle.append(branches.size()).append(" sucursal");
    if (branches.size() != 1) subtitle.append("es");
    String cities =
        branches.stream().map(BranchesFragment::city).distinct().collect(Collectors.joining(" · "));
    if (!cities.isEmpty()) subtitle.append(" · ").append(cities);
    binding.subtitle.setText(subtitle.toString());

    binding.list.setAdapter(new BranchAdapter(branches, this::focusMap));
    binding.empty.setVisibility(branches.isEmpty() ? View.VISIBLE : View.GONE);
    if (branches.isEmpty()) return;
    boolean selectedExists =
        branches.stream().anyMatch(branch -> branch.id.equals(selectedId));
    if (!selectedExists) focusMap(branches.get(0));
  }

  /** Centra el mapa en la sucursal marcada sin salir de la aplicación. */
  private void focusMap(Branch branch) {
    if (branch.latitude == 0d && branch.longitude == 0d) {
      toast("Esta sucursal aún no tiene coordenadas.");
      return;
    }
    selectedId = branch.id;
    binding.mapSpinner.setVisibility(View.VISIBLE);
    binding.mapWeb.setAlpha(0f);
    binding.mapCaption.animate()
        .alpha(0f)
        .setDuration(120)
        .withEndAction(
            () -> {
              if (binding == null) return;
              binding.mapCaption.setText(branch.name);
              binding.mapCaption.setAlpha(0f);
              binding.mapCaption.animate().alpha(1f).setDuration(180).start();
            })
        .start();
    binding.mapWeb.loadUrl(embedUrl(branch));
    RecyclerView.Adapter<?> adapter = binding.list.getAdapter();
    if (adapter != null) adapter.notifyDataSetChanged();
  }

  private static String embedUrl(Branch branch) {
    if (branch.latitude == 0d && branch.longitude == 0d) {
      return String.format(Locale.US, OSM_EMBED, -68.19, -16.58, -68.06, -16.45, -16.513, -68.124);
    }
    double span = 0.014;
    double k = 0.6;
    double minLon = branch.longitude - span;
    double minLat = branch.latitude - span * k;
    double maxLon = branch.longitude + span;
    double maxLat = branch.latitude + span * k;
    return String.format(
        Locale.US,
        OSM_EMBED,
        minLon,
        minLat,
        maxLon,
        maxLat,
        branch.latitude,
        branch.longitude);
  }

  private static String city(Branch branch) {
    switch (branch.id) {
      case "central":
      case "san_miguel":
        return "La Paz";
      case "santa_cruz":
        return "Santa Cruz";
      default:
        break;
    }
    String name = branch.name;
    if (name != null) {
      int i = Math.max(name.lastIndexOf(" - "), name.lastIndexOf(" · "));
      if (i >= 0 && i + 3 < name.length()) return name.substring(i + 3).trim();
    }
    return "Bolivia";
  }

  private static String address(Branch branch) {
    if ("central".equals(branch.id)) return "Av. Arce #2412, Edif. Torres Poeta";
    if ("san_miguel".equals(branch.id)) return "Calle Montenegro #882, Zona Sur";
    return branch.address;
  }

  private static String branchInfo(Branch branch) {
    String phone = branch.phone;
    if (phone == null || phone.isEmpty() || phone.startsWith("Datos")) phone = "+591 73596502";
    String hours = branch.hours;
    if (hours == null || hours.isEmpty()) hours = "Lun - Sáb  |  9:00 a 18:00";
    else hours = hours.replace("Lun–Sáb · 09:00–18:00", "Lun - Sáb  |  9:00 a 18:00");
    return "☎ " + phone + "  |  " + hours;
  }

  private final class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.Holder> {
    private final List<Branch> branches;
    private final Consumer<Branch> onFocusMap;

    BranchAdapter(List<Branch> branches, Consumer<Branch> onFocusMap) {
      this.branches = branches;
      this.onFocusMap = onFocusMap;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(
          ItemBranchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      Branch branch = branches.get(position);
      boolean selected = branch.id.equals(selectedId);
      holder.binding.branchName.setText(branch.name);
      holder.binding.branchAddress.setText(address(branch));
      holder.binding.branchInfo.setText(branchInfo(branch));
      holder.binding.cityPill.setText(city(branch));
      holder.binding.mapLink.setText(
          selected ? R.string.branches_in_map : R.string.branches_view_map);
      holder.binding.getRoot().setBackgroundResource(
          selected ? R.drawable.bg_premium_card_selected : R.drawable.bg_premium_card);
      holder.binding.accentBar.setVisibility(selected ? View.VISIBLE : View.GONE);
      holder.binding.mapLink.setOnClickListener(view -> onFocusMap.accept(branch));
      holder.binding.getRoot().setOnClickListener(view -> onFocusMap.accept(branch));
    }

    @Override
    public int getItemCount() {
      return branches.size();
    }

    private final class Holder extends RecyclerView.ViewHolder {
      final ItemBranchBinding binding;

      Holder(ItemBranchBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
      }
    }
  }

  @Override
  public void onDestroyView() {
    if (binding != null) {
      binding.mapWeb.stopLoading();
      binding.mapWeb.destroy();
    }
    super.onDestroyView();
    binding = null;
  }
}