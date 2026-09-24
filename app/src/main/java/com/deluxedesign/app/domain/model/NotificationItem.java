package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notifications")
public class NotificationItem {
  @PrimaryKey @NonNull public String id = "";
  public String userId = "";
  public String title = "";
  public String message = "";
  public long createdAt;
  public boolean read;

  public NotificationItem() {}
}
