package com.deluxedesign.app.ui.profile;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
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
import com.deluxedesign.app.databinding.FragmentBranchesBinding;
import com.deluxedesign.app.databinding.ItemBranchBinding;
import com.deluxedesign.app.domain.model.Branch;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import java.util.List;
import java.util.function.Consumer;

public class BranchesFragment extends BaseFragment {
  private static final String MAP_URL =
      "https://www.openstreetmap.org/export/embed.html?bbox=-68.154%2C-16.566%2C-68.048%2C-16.476&amp;layer=mapnik&amp;marker=-16.513%2C-68.124";

  private FragmentBranchesBinding binding;
  private boolean mapFailed;

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
          public void onPageFinished(WebView view, String url) {
            if (!mapFailed && binding != null) binding.mapWeb.setVisibility(View.VISIBLE);
          }

          @Override
          public void onReceivedError(
              WebView view, WebResourceRequest request, WebResourceError error) {
            if (request.isForMainFrame()) {
              mapFailed = true;
              if (binding != null) binding.mapWeb.setVisibility(View.INVISIBLE);
            }
          }
        });
    binding.mapWeb.loadUrl(MAP_URL.replace("&amp;", "&"));
  }

  protected void render() {
    List<Branch> branches = AppViewModel.list(vm.branches);
    binding.list.setAdapter(new BranchAdapter(branches, this::openMap));
    binding.empty.setVisibility(branches.isEmpty() ? View.VISIBLE : View.GONE);
  }

  private void openMap(Branch branch) {
    try {
      String query =
          Uri.encode(branch.latitude + "," + branch.longitude + "(" + branch.name + ")");
      startActivity(
          new android.content.Intent(
              android.content.Intent.ACTION_VIEW,
              Uri.parse(
                  "geo:" + branch.latitude + "," + branch.longitude + "?q=" + query)));
    } catch (android.content.ActivityNotFoundException exception) {
      toast("No hay una aplicación de mapas instalada.");
    }
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

  private static final class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.Holder> {
    private final List<Branch> branches;
    private final Consumer<Branch> onOpenMap;

    BranchAdapter(List<Branch> branches, Consumer<Branch> onOpenMap) {
      this.branches = branches;
      this.onOpenMap = onOpenMap;
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
      holder.binding.branchName.setText(branch.name);
      holder.binding.branchAddress.setText(address(branch));
      holder.binding.branchInfo.setText(branchInfo(branch));
      holder.binding.mapLink.setOnClickListener(view -> onOpenMap.accept(branch));
      holder.binding.getRoot().setOnClickListener(view -> onOpenMap.accept(branch));
    }

    @Override
    public int getItemCount() {
      return branches.size();
    }

    private static final class Holder extends RecyclerView.ViewHolder {
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
