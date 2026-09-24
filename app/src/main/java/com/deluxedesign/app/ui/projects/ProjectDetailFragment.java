package com.deluxedesign.app.ui.projects;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectDetailBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class ProjectDetailFragment extends BaseFragment {
  private FragmentProjectDetailBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentProjectDetailBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(
        R.id.edit,
        () -> {
          if (vm.project() != null) {
            vm.editProject(vm.project());
            go(R.id.customizer);
          }
        });
    click(R.id.quote, () -> go(R.id.new_quote));
  }

  protected void render() {
    com.deluxedesign.app.domain.model.Project p = vm.project();
    if (p == null) return;
    text(R.id.projectName, p.name);
    text(R.id.status, p.status + " · " + p.progress + " %");
    binding.progress.setProgress(p.progress);
    text(R.id.date, "Creado " + com.deluxedesign.app.util.Formatters.date(p.createdAt));
    com.deluxedesign.app.domain.model.CustomizationPreset style = vm.preset(p.presetId);
    if (style != null) {
      text(R.id.summary, style.summary());
      com.deluxedesign.app.util.AssetImages.show(binding.hero, style.front);
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
