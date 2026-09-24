package com.deluxedesign.app.ui.auth;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentWelcomeBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class WelcomeFragment extends BaseFragment {
  private FragmentWelcomeBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentWelcomeBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    click(R.id.start, () -> go(vm.user() == null ? R.id.login : R.id.home));
    root.findViewById(R.id.back).setVisibility(View.GONE);
    vm.repositories
        .ready()
        .observe(
            getViewLifecycleOwner(), ready -> binding.start.setEnabled(Boolean.TRUE.equals(ready)));
  }

  protected void render() {
    binding.start.setEnabled(Boolean.TRUE.equals(vm.repositories.ready().getValue()));
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
