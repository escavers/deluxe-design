package com.deluxedesign.app.ui.projects;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectDetailBinding;
import com.deluxedesign.app.databinding.ItemProjectChangeBinding;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.AssetImages;
import com.deluxedesign.app.util.Formatters;
import java.util.Locale;

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
    Project project = vm.project();
    if (project == null) return;

    String name = project.name == null ? "" : project.name.replace(" · ", " - ");
    binding.projectName.setText(name.toUpperCase(new Locale("es")));
    binding.status.setText(project.status + " - " + project.progress + "%");
    binding.date.setText("Creado el " + titleCaseMonth(Formatters.date(project.createdAt)));

    com.deluxedesign.app.domain.model.CustomizationPreset config = vm.configurationFor(project);
    if (config == null) return;

    java.util.Map<String, com.deluxedesign.app.domain.model.CustomizationOption> byCat =
        new java.util.HashMap<>();
    if (config.options != null)
      for (com.deluxedesign.app.domain.model.CustomizationOption o : config.options)
        byCat.put(o.category, o);

    setChange(binding.paintRow, "Pintura", label(byCat, "paint") + " · " + label(byCat, "finish"));
    setChange(binding.vinylRow, "Vinilos", label(byCat, "vinyl"));
    setChange(binding.wheelsRow, "Llantas", label(byCat, "wheels"));
    setChange(binding.bodyKitRow, "Body Kit", label(byCat, "bodykit"));

    AssetImages.show(binding.hero, config.image("front"));
  }

  private static String label(
      java.util.Map<String, com.deluxedesign.app.domain.model.CustomizationOption> byCat,
      String category) {
    com.deluxedesign.app.domain.model.CustomizationOption o = byCat.get(category);
    return o == null || o.label == null ? "" : o.label;
  }

  private static void setChange(ItemProjectChangeBinding row, String label, String value) {
    row.changeLabel.setText(label);
    row.changeValue.setText(value);
  }

  private static String titleCaseMonth(String date) {
    if (date == null || date.length() < 4) return date;
    return date.substring(0, 3)
        + Character.toUpperCase(date.charAt(3))
        + date.substring(4);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
