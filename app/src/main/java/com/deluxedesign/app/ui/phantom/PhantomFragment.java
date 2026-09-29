package com.deluxedesign.app.ui.phantom;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.databinding.FragmentPhantomBinding;
import com.deluxedesign.app.ui.BaseFragment;

public class PhantomFragment extends BaseFragment {
  private FragmentPhantomBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentPhantomBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    try {
      String version =
          requireContext()
              .getPackageManager()
              .getPackageInfo(requireContext().getPackageName(), 0)
              .versionName;
      binding.version.setText("App · v" + version);
    } catch (Exception ignored) {
      binding.version.setText("App");
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}