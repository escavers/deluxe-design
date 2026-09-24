package com.deluxedesign.app.ui.profile;

import android.view.*;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentNotificationsBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;

public class NotificationsFragment extends BaseFragment {
  private FragmentNotificationsBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentNotificationsBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {}

  protected void render() {
    java.util.List<com.deluxedesign.app.ui.common.CardAdapter.Card> rows =
        new java.util.ArrayList<>();
    for (com.deluxedesign.app.domain.model.NotificationItem n : AppViewModel.list(vm.notifications))
      rows.add(
          new com.deluxedesign.app.ui.common.CardAdapter.Card(
              n.title,
              n.message + "\n" + com.deluxedesign.app.util.Formatters.date(n.createdAt),
              "",
              n.read ? "Leída" : "● Nueva",
              -1,
              () -> vm.repositories.notifications().markRead(n, vm.task(x -> {}))));
    cards(R.id.list, rows);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
