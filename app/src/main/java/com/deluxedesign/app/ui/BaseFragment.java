package com.deluxedesign.app.ui;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.ui.common.CardAdapter;
import java.util.List;

public abstract class BaseFragment extends Fragment {
  protected AppViewModel vm;
  protected View root;

  protected abstract ViewBinding bind(LayoutInflater inflater, ViewGroup parent);

  protected abstract void configure();

  protected void render() {}

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved) {
    root = bind(inflater, container).getRoot();
    return root;
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle saved) {
    vm = new ViewModelProvider(requireActivity()).get(AppViewModel.class);
    View back = root.findViewById(R.id.back);
    if (back != null)
      back.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());
    configure();
    vm.vehicles.observe(getViewLifecycleOwner(), x -> render());
    vm.presets.observe(getViewLifecycleOwner(), x -> render());
    vm.projects.observe(getViewLifecycleOwner(), x -> render());
    vm.quotes.observe(getViewLifecycleOwner(), x -> render());
    vm.favorites.observe(getViewLifecycleOwner(), x -> render());
    vm.notifications.observe(getViewLifecycleOwner(), x -> render());
    vm.branches.observe(getViewLifecycleOwner(), x -> render());
    vm.session.observe(getViewLifecycleOwner(), x -> render());
    vm.revision.observe(getViewLifecycleOwner(), x -> render());
    vm.busy.observe(
        getViewLifecycleOwner(),
        busy -> {
          View progress = root.findViewById(R.id.loading);
          if (progress != null)
            progress.setVisibility(Boolean.TRUE.equals(busy) ? View.VISIBLE : View.GONE);
        });
    vm.error.observe(
        getViewLifecycleOwner(),
        message -> {
          TextView error = root.findViewById(R.id.error);
          if (error != null) {
            error.setText(message);
            error.setVisibility(message == null || message.isEmpty() ? View.GONE : View.VISIBLE);
          }
        });
    vm.error.setValue("");
    vm.repositories
        .issue()
        .observe(
            getViewLifecycleOwner(),
            message -> {
              if (message != null && !message.isEmpty()) vm.error.setValue(message);
            });
    render();
  }

  protected void click(int id, Runnable action) {
    View v = root.findViewById(id);
    if (v != null)
      v.setOnClickListener(
          x -> {
            if (!Boolean.TRUE.equals(vm.busy.getValue())) action.run();
          });
  }

  protected String input(int id) {
    return ((TextView) root.findViewById(id)).getText().toString().trim();
  }

  protected void text(int id, String value) {
    TextView v = root.findViewById(id);
    if (v != null) v.setText(value);
  }

  protected void go(int id) {
    if (isAdded() && getView() != null) NavHostFragment.findNavController(this).navigate(id);
  }

  protected void go(int id, Bundle args) {
    if (isAdded() && getView() != null) NavHostFragment.findNavController(this).navigate(id, args);
  }

  private static android.widget.Toast pendingToast;

  protected void toast(String message) {
    if (!isAdded()) return;
    if (pendingToast != null) pendingToast.cancel();
    pendingToast = android.widget.Toast.makeText(requireContext(), message, android.widget.Toast.LENGTH_SHORT);
    pendingToast.show();
  }

  protected void cards(int id, List<CardAdapter.Card> values) {
    androidx.recyclerview.widget.RecyclerView list = root.findViewById(id);
    if (list == null) return;
    if (list.getLayoutManager() == null) {
      list.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
      list.setNestedScrollingEnabled(false);
    }
    list.setAdapter(new CardAdapter(values));
    View empty = root.findViewById(R.id.empty);
    if (empty != null) empty.setVisibility(values.isEmpty() ? View.VISIBLE : View.GONE);
  }

  protected void observeText(int id, java.util.function.Consumer<String> change) {
    ((EditText) root.findViewById(id))
        .addTextChangedListener(
            new android.text.TextWatcher() {
              public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

              public void onTextChanged(CharSequence s, int st, int before, int count) {
                change.accept(s.toString());
              }

              public void afterTextChanged(android.text.Editable e) {}
            });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    root = null;
  }
}
