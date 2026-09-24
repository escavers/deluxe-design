package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
  @PrimaryKey @NonNull public String id = "";
  public String name = "";
  public String email = "";
  public String phone = "";
  public String passwordHash = "";
  public String avatar = "";

  public User() {}
}
