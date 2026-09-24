package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

public interface NotificationRepository {
  LiveData<List<NotificationItem>> notifications(String userId);

  void markRead(NotificationItem item, Result<Void> result);
}
