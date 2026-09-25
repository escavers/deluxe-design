package com.deluxedesign.app.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.deluxedesign.app.R;
import com.deluxedesign.app.databinding.FragmentNotificationsBinding;
import com.deluxedesign.app.databinding.ItemNotificationBinding;
import com.deluxedesign.app.domain.model.NotificationItem;
import com.deluxedesign.app.ui.AppViewModel;
import com.deluxedesign.app.ui.BaseFragment;
import com.deluxedesign.app.util.Formatters;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class NotificationsFragment extends BaseFragment {
  private FragmentNotificationsBinding binding;

  @Override
  protected ViewBinding bind(LayoutInflater inflater, ViewGroup parent) {
    binding = FragmentNotificationsBinding.inflate(inflater, parent, false);
    return binding;
  }

  protected void configure() {
    binding.list.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.list.setNestedScrollingEnabled(false);
  }

  protected void render() {
    List<NotificationItem> notifications = AppViewModel.list(vm.notifications);
    binding.list.setAdapter(
        new NotificationAdapter(
            notifications,
            notification ->
                vm.repositories.notifications().markRead(notification, vm.task(value -> {}))));
    binding.empty.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
  }

  private static String relativeTime(long createdAt) {
    long minutes = Math.max(1, (System.currentTimeMillis() - createdAt) / 60000L);
    if (minutes < 60) return "Hace " + minutes + " min";
    long hours = minutes / 60;
    if (hours < 24) return "Hace " + hours + (hours == 1 ? " hora" : " horas");
    if (hours < 48) return "Ayer";
    return Formatters.date(createdAt);
  }

  @DrawableRes
  private static int iconFor(NotificationItem notification) {
    String title = notification.title == null ? "" : notification.title.toLowerCase(new Locale("es"));
    if (title.contains("cotización")) return R.drawable.ic_notification_quote;
    if (title.contains("modelo") || title.contains("catálogo"))
      return R.drawable.ic_notification_catalog;
    return R.drawable.ic_notification_project;
  }

  private static final class NotificationAdapter
      extends RecyclerView.Adapter<NotificationAdapter.Holder> {
    private final List<NotificationItem> notifications;
    private final Consumer<NotificationItem> onRead;

    NotificationAdapter(List<NotificationItem> notifications, Consumer<NotificationItem> onRead) {
      this.notifications = notifications;
      this.onRead = onRead;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      return new Holder(
          ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
      NotificationItem notification = notifications.get(position);
      boolean highlighted = !notification.read && position == 0;
      holder.binding.getRoot().setBackgroundResource(
          highlighted ? R.drawable.bg_notification_unread : R.drawable.bg_notification_read);
      holder.binding.unreadDot.setVisibility(highlighted ? View.VISIBLE : View.GONE);
      holder.binding.notificationIcon.setImageResource(iconFor(notification));
      holder.binding.notificationTitle.setText(notification.title);
      holder.binding.notificationMessage.setText(notification.message);
      holder.binding.notificationTime.setText(relativeTime(notification.createdAt));
      holder.binding.getRoot().setOnClickListener(view -> {
        if (!notification.read) onRead.accept(notification);
      });
    }

    @Override
    public int getItemCount() {
      return notifications.size();
    }

    private static final class Holder extends RecyclerView.ViewHolder {
      final ItemNotificationBinding binding;

      Holder(ItemNotificationBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
      }
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
