package com.deluxedesign.app.ui.customizer;

import android.graphics.Color;
import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.data.OptionsCatalog;
import com.deluxedesign.app.databinding.FragmentCustomizerBinding;
import com.deluxedesign.app.databinding.ItemCostRowBinding;
import com.deluxedesign.app.databinding.ItemCustomizerCategoryBinding;
import com.deluxedesign.app.databinding.ItemCustomizerOptionBinding;
import com.deluxedesign.app.domain.model.CustomizationOption;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.Formatters;
import com.deluxedesign.app.util.QuoteCalculator;
import com.deluxedesign.app.util.VehicleImages;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomizerFragment extends BaseFragment {
  private FragmentCustomizerBinding binding;
  private final List<ItemCustomizerCategoryBinding> rows = new ArrayList<>();
  private final List<android.widget.FrameLayout> paintChips = new ArrayList<>();
  

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
    buildColorChips();
    buildRows();
  }

  private void buildColorChips() {
    binding.paintRow.removeAllViews();
    paintChips.clear();
    for (CustomizationOption option : OptionsCatalog.forCategory("paint")) {
      android.widget.FrameLayout wrap = new android.widget.FrameLayout(requireContext());
      int size = dp(48);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
      lp.setMarginEnd(dp(10));
      wrap.setLayoutParams(lp);

      android.view.View circle = new android.view.View(requireContext());
      android.widget.FrameLayout.LayoutParams cl =
          new android.widget.FrameLayout.LayoutParams(dp(30), dp(30));
      cl.gravity = android.view.Gravity.CENTER;
      circle.setLayoutParams(cl);
      circle.setBackground(swatchDrawable(option.swatch, false));
      circle.setContentDescription(option.label);
      wrap.addView(circle);

      final String paintId = option.id;
      wrap.setOnClickListener(
          v -> {
            vm.selectOption("paint", paintId);
            toast("Color: " + option.label);
          });
      binding.paintRow.addView(wrap);
      paintChips.add(wrap);
    }
  }

  private void paintChipFocus() {
    String selected = vm.selections().get("paint");
    List<CustomizationOption> paints = OptionsCatalog.forCategory("paint");
    for (int i = 0; i < paintChips.size() && i < paints.size(); i++) {
      boolean active = paints.get(i).id.equals(selected);
      android.view.View circle = paintChips.get(i).getChildAt(0);
      circle.setBackground(swatchDrawable(paints.get(i).swatch, active));
    }
  }

  private int dp(int value) {
    return (int) (value * getResources().getDisplayMetrics().density);
  }

  private void buildRows() {
    binding.optionsList.removeAllViews();
    rows.clear();
    for (String category : OptionsCatalog.CATEGORIES) {
      ItemCustomizerCategoryBinding row =
          ItemCustomizerCategoryBinding.inflate(getLayoutInflater(), binding.optionsList, false);
      row.label.setText(QuoteCalculator.categoryLabel(category).toUpperCase(Locale.ROOT));
      row.getRoot().setOnClickListener(v -> showOptions(category));
      binding.optionsList.addView(row.getRoot());
      rows.add(row);
    }
  }

  private void showOptions(String category) {
    List<CustomizationOption> options = OptionsCatalog.forCategory(category);
    if (options.isEmpty()) return;
    BottomSheetDialog sheet = new BottomSheetDialog(requireContext());
    LinearLayout content = new LinearLayout(requireContext());
    content.setOrientation(LinearLayout.VERTICAL);
    android.widget.TextView title = new android.widget.TextView(requireContext());
    title.setText(QuoteCalculator.categoryLabel(category));
    title.setTextColor(0xFFFFFFFF);
    title.setTextSize(17f);
    title.setTypeface(
        android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD));
    title.setPadding(24, 22, 24, 6);
    content.addView(title);
    String selectedId = vm.option(category) == null ? "" : vm.option(category).id;
    for (CustomizationOption option : options) {
      ItemCustomizerOptionBinding item =
          ItemCustomizerOptionBinding.inflate(getLayoutInflater(), content, false);
      item.label.setText(option.label);
      item.detail.setText(option.detail);
      if (option.swatch != null && !option.swatch.isEmpty()) {
        item.swatch.setBackground(swatchDrawable(option.swatch, false));
      }
      long delta = option.priceDeltaCents == null ? 0 : option.priceDeltaCents;
      item.delta.setText(delta > 0 ? "+ " + Formatters.money(delta) : "Incluido");
      boolean selected = option.id.equals(selectedId);
      item.check.setVisibility(selected ? View.VISIBLE : View.GONE);
      item.row.setBackgroundResource(selected ? R.drawable.bg_card : android.R.color.transparent);
      final CustomizationOption target = option;
      item.getRoot().setOnClickListener(v -> {
            vm.selectOption(category, target.id);
            sheet.dismiss();
          });
      content.addView(item.getRoot());
    }
    android.widget.Button cancel = new android.widget.Button(requireContext());
    cancel.setText("Cerrar");
    cancel.setTextColor(0xFFB6B9C4);
    cancel.setBackground(null);
    cancel.setLayoutParams(
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 52));
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

  /** Burbuja ovalada de color con borde: blanco normal, cian cuando está activo. */
  private android.graphics.drawable.GradientDrawable swatchDrawable(String hex, boolean active) {
    android.graphics.drawable.GradientDrawable g = new android.graphics.drawable.GradientDrawable();
    g.setShape(android.graphics.drawable.GradientDrawable.OVAL);
    g.setColor(parseColor(hex));
    int strokeColor =
        active
            ? androidx.core.content.ContextCompat.getColor(requireContext(), R.color.cyan)
            : 0xFFFFFFFF;
    g.setStroke(dp(active ? 3 : 2), strokeColor);
    return g;
  }

  protected void render() {
    com.deluxedesign.app.domain.model.Vehicle vehicle = vm.vehicle();
    if (vehicle == null) return;
    text(R.id.vehicleName, vehicle.name);
    paintChipFocus();

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
        row.swatch.setBackground(swatchDrawable(option.swatch, false));
      } else row.swatch.setVisibility(View.GONE);
    }

    CustomizationOption paint = vm.option("paint");
    String paintId = paint == null ? VehicleImages.DEFAULT_COLOR : paint.id;
    binding.heroPhoto.setColor(vehicle.id, paintId);

    binding.breakdown.removeAllViews();
    long base = vm.basePrice();
    addCostRow("Base del vehículo", vehicle.name, base, 0xffB6B9C4);
    long extras = 0;
    for (String category : OptionsCatalog.CATEGORIES) {
      CustomizationOption option = vm.option(category);
      if (option == null) continue;
      long delta = option.priceDeltaCents == null ? 0 : option.priceDeltaCents;
      extras += delta;
      addCostRow(QuoteCalculator.categoryLabel(category), option.label, delta, 0xff4CD964);
    }
    text(R.id.total, Formatters.money(base + extras));

    binding.undo.setEnabled(vm.canUndo());
    binding.redo.setEnabled(vm.canRedo());
    binding.undo.setAlpha(vm.canUndo() ? 1f : .5f);
    binding.redo.setAlpha(vm.canRedo() ? 1f : .5f);
  }

  private void addCostRow(String category, String option, long amount, int color) {
    ItemCostRowBinding rowBinding =
        ItemCostRowBinding.inflate(getLayoutInflater(), binding.breakdown, false);
    rowBinding.category.setText(category);
    if (option != null && !option.isEmpty()) {
      rowBinding.option.setVisibility(View.VISIBLE);
      rowBinding.option.setText(option);
    }
    rowBinding.delta.setText(amount > 0 ? "+ " + Formatters.money(amount) : Formatters.money(amount));
    rowBinding.delta.setTextColor(color);
    binding.breakdown.addView(rowBinding.getRoot());
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
    rows.clear();
  }
}