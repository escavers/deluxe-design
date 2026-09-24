package com.deluxedesign.app.ui.common;

import android.view.View;
import android.widget.*;
import java.util.List;

public final class Ui {
  public static void spinner(
      Spinner spinner, List<String> values, java.util.function.IntConsumer selected) {
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, values);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          public void onItemSelected(AdapterView<?> p, View v, int position, long id) {
            selected.accept(position);
          }

          public void onNothingSelected(AdapterView<?> p) {}
        });
  }
}
