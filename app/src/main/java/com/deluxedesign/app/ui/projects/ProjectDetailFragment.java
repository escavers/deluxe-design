package com.deluxedesign.app.ui.projects;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentProjectDetailBinding;
import com.deluxedesign.app.databinding.ItemProjectChangeBinding;
import com.deluxedesign.app.domain.model.CustomizationPreset;
import com.deluxedesign.app.domain.model.Project;
import com.deluxedesign.app.ui.BaseFragment;
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
    binding.hero.setImageResource(vehicleImage(project.vehicleId));

    CustomizationPreset style = vm.preset(project.presetId);
    if (style == null) return;
    setChange(binding.paintRow, "Pintura", style.paint + " " + style.finish);
    setChange(binding.vinylRow, "Vinilos", style.vinyl);
    setChange(binding.wheelsRow, "Llantas", style.wheels);
    setChange(binding.bodyKitRow, "Body Kit", style.bodyKit);
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

  @DrawableRes
  private static int vehicleImage(String vehicleId) {
    if ("bmw".equals(vehicleId)) return R.drawable.vehicle_bmw_reference;
    if ("mustang".equals(vehicleId)) return R.drawable.vehicle_mustang_reference;
    return R.drawable.vehicle_porsche_reference;
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
