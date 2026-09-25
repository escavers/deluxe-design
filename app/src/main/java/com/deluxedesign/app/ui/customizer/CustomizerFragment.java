package com.deluxedesign.app.ui.customizer;

import android.graphics.Color;
import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.data.OptionsCatalog;
import com.deluxedesign.app.databinding.FragmentCustomizerBinding;
import com.deluxedesign.app.databinding.ItemCustomizerCategoryBinding;
import com.deluxedesign.app.databinding.ItemCustomizerOptionBinding;
import com.deluxedesign.app.domain.model.CustomizationOption;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.AssetImages;
import com.deluxedesign.app.util.Car3D;
import com.deluxedesign.app.util.Formatters;
import com.deluxedesign.app.util.QuoteCalculator;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomizerFragment extends BaseFragment {
  private FragmentCustomizerBinding binding;
  private final List<ItemCustomizerCategoryBinding> rows = new ArrayList<>();
  private String photo = "preview";
  private Car3D car3d;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentCustomizerBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.undo, vm::undo);
    click(R.id.redo, vm::redo);
    click(R.id.reset, vm::discard);
    click(R.id.save, () -> go(R.id.confirmation));
    for (int id : new int[] {R.id.front, R.id.side, R.id.rear})
      click(
          id, () -> vm.set("angle", id == R.id.side ? "side" : id == R.id.rear ? "rear" : "front"));
    click(R.id.togglePreview, () -> setPhotoMode("preview"));
    click(R.id.togglePhoto, () -> setPhotoMode("photo"));
    click(
        R.id.view3d,
        () -> {
          android.os.Bundle args = new android.os.Bundle();
          args.putString(
              "vehicleId", vm.vehicle() == null ? "porsche" : vm.vehicle().id);
          args.putString("name", vm.vehicle() == null ? "" : vm.vehicle().name);
          go(R.id.vehicle_3d, args);
        });
    setup3d();
    buildTemplateChips();
    buildRows();
  }

  private void setup3d() {
    String id = vm.vehicle() == null ? "porsche" : vm.vehicle().id;
    car3d = new Car3D(binding.preview, requireContext(), "vehicle_" + id.replaceAll("[^a-z0-9_]", "") + ".glb");
  }

  private void setPhotoMode(String mode) {
    photo = mode;
    render();
  }

  private void buildTemplateChips() {
    binding.templateChips.removeAllViews();
    for (String template : OptionsCatalog.TEMPLATES) {
      android.widget.TextView chip = new android.widget.TextView(requireContext());
      chip.setText(OptionsCatalog.templateName(template));
      chip.setTextColor(0xFFFFFFFF);
      chip.setTextSize(13f);
      chip.setGravity(Gravity.CENTER);
      chip.setBackgroundResource(R.drawable.bg_chip);
      LinearLayout.LayoutParams lp =
          new LinearLayout.LayoutParams(0, 40, 1);
      lp.setMarginEnd(8);
      chip.setLayoutParams(lp);
      chip.setClickable(true);
      chip.setFocusable(true);
      chip.setOnClickListener(
          v -> {
            vm.selectTemplate(template);
            toast("Plantilla aplicada: " + OptionsCatalog.templateName(template));
          });
      binding.templateChips.addView(chip);
    }
  }

  private void buildRows() {
    binding.optionsList.removeAllViews();
    rows.clear();
    for (String category : OptionsCatalog.CATEGORIES) {
      ItemCustomizerCategoryBinding row =
          ItemCustomizerCategoryBinding.inflate(getLayoutInflater(), binding.optionsList, false);
      row.label.setText(QuoteCalculator.categoryLabel(category).toUpperCase(Locale.ROOT));
      row.getRoot()
          .setOnClickListener(v -> showOptions(category));
      binding.optionsList.addView(row.getRoot());
      rows.add(row);
    }
  }

  private void showOptions(String category) {
    List<CustomizationOption> options = OptionsCatalog.forCategory(category);
    if (options.isEmpty()) return;
    BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
    LinearLayout content =
        new LinearLayout(requireContext());
    content.setOrientation(LinearLayout.VERTICAL);
    android.widget.TextView title = new android.widget.TextView(requireContext());
    title.setText(QuoteCalculator.categoryLabel(category));
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(17f);
    title.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD));
    title.setPadding(24, 22, 24, 6);
    content.addView(title);
    String selectedId = vm.option(category) == null ? "" : vm.option(category).id;
    for (CustomizationOption option : options) {
      ItemCustomizerOptionBinding item =
          ItemCustomizerOptionBinding.inflate(getLayoutInflater(), content, false);
      item.label.setText(option.label);
      item.detail.setText(option.detail);
      if (option.swatch != null && !option.swatch.isEmpty()) {
        item.swatch.setBackgroundResource(R.drawable.bg_swatch);
        item.swatch.getBackground().setTint(parseColor(option.swatch));
      }
      long delta = option.priceDeltaCents == null ? 0 : option.priceDeltaCents;
      item.delta.setText(delta > 0 ? "+ " + Formatters.money(delta) : "Incluido");
      boolean selected = option.id.equals(selectedId);
      item.check.setVisibility(selected ? View.VISIBLE : View.GONE);
      item.row.setBackgroundResource(selected ? R.drawable.bg_card : android.R.color.transparent);
      final CustomizationOption target = option;
      item.getRoot()
          .setOnClickListener(
              v -> {
                vm.selectOption(category, target.id);
                sheet.dismiss();
              });
      content.addView(item.getRoot());
    }
    android.widget.Button cancel = new android.widget.Button(requireContext());
    cancel.setText("Cerrar");
    cancel.setTextColor(0xFFB6B9C4);
    cancel.setBackground(null);
    cancel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 52));
    cancel.setOnClickListener(v -> sheet.dismiss());
    content.addView(cancel);
    sheet.setContentView(content);
    sheet.show();
  }

  private int parseColor(String hex) {
    try {
      return Color.parseColor(hex);
    } catch (Exception e) {
      return 0xFFFFFFFF;
    }
  }

  protected void render() {
    com.deluxedesign.app.domain.model.CustomizationPreset config = vm.configuration();
    if (config == null) return;
    text(R.id.vehicleName, vm.vehicle() == null ? "" : vm.vehicle().name);

    for (int i = 0; i < rows.size() && i < OptionsCatalog.CATEGORIES.length; i++) {
      String category = OptionsCatalog.CATEGORIES[i];
      ItemCustomizerCategoryBinding row = rows.get(i);
      CustomizationOption option = vm.option(category);
      if (option == null) continue;
      row.value.setText(option.label);
      long delta = option.priceDeltaCents == null ? 0 : option.priceDeltaCents;
      row.delta.setText(delta > 0 ? "+ " + Formatters.money(delta) : "");
      if (option.swatch != null && !option.swatch.isEmpty()) {
        row.swatch.setVisibility(View.VISIBLE);
        row.swatch.getBackground().setTint(parseColor(option.swatch));
      } else row.swatch.setVisibility(View.GONE);
    }

    // palanquillas
    for (int i = 0; i < binding.templateChips.getChildCount(); i++) {
      View chip = binding.templateChips.getChildAt(i);
      boolean active = OptionsCatalog.TEMPLATES[i].equals(vm.templateOfCurrent());
      chip.setBackgroundResource(active ? R.drawable.bg_chip_active : R.drawable.bg_chip);
    }

    // ángulos
    String angle = vm.value("angle", "front");
    setActive(R.id.front, "front".equals(angle));
    setActive(R.id.side, "side".equals(angle));
    setActive(R.id.rear, "rear".equals(angle));

    // vista previa / foto
    boolean previewMode = "preview".equals(photo);
    binding.preview.setVisibility(previewMode ? View.VISIBLE : View.GONE);
    binding.heroPhoto.setVisibility(previewMode ? View.GONE : View.VISIBLE);
    setActive(R.id.togglePreview, previewMode);
    setActive(R.id.togglePhoto, !previewMode);
    AssetImages.show(binding.heroPhoto, config.image(angle));
    if (previewMode && car3d != null) {
      String view3d =
          "side".equals(angle) ? "side" : "rear".equals(angle) ? "rear" : "front34";
      car3d.js("setPreset('view','" + view3d + "')");
    }

    // desglose
    binding.breakdown.removeAllViews();
    long base = vm.basePrice();
    addBreakdownRow("Base del vehículo", Formatters.money(base));
    long extras = 0;
    for (String category : OptionsCatalog.CATEGORIES) {
      CustomizationOption option = vm.option(category);
      long delta = option == null || option.priceDeltaCents == null ? 0 : option.priceDeltaCents;
      if (delta <= 0) continue;
      extras += delta;
      addBreakdownRow(
          QuoteCalculator.categoryLabel(category) + " · " + option.label,
          "+ " + Formatters.money(delta));
    }
    text(R.id.total, Formatters.money(base + extras));

    binding.undo.setEnabled(vm.canUndo());
    binding.redo.setEnabled(vm.canRedo());
    binding.undo.setAlpha(vm.canUndo() ? 1f : .5f);
    binding.redo.setAlpha(vm.canRedo() ? 1f : .5f);
  }

  private void addBreakdownRow(String label, String amount) {
    android.widget.TableRow row = new android.widget.TableRow(requireContext());
    android.widget.TextView l = new android.widget.TextView(requireContext());
    l.setText(label);
    l.setTextColor(0xFFB6B9C4);
    l.setTextSize(13f);
    l.setPadding(0, 4, 12, 4);
    android.widget.TextView a = new android.widget.TextView(requireContext());
    a.setText(amount);
    a.setTextColor(0xFFFFFFFF);
    a.setTextSize(13f);
    row.addView(l, new android.widget.TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
    row.addView(a);
    binding.breakdown.addView(row);
  }

  private void setActive(int id, boolean active) {
    View v = root.findViewById(id);
    if (v != null) v.setBackgroundResource(active ? R.drawable.bg_chip_active : R.drawable.bg_card);
  }

  @Override
  public void onDestroyView() {
    if (car3d != null) car3d.destroy();
    car3d = null;
    super.onDestroyView();
    binding = null;
    rows.clear();
  }
}