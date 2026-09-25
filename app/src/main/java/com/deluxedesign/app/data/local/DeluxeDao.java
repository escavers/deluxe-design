package com.deluxedesign.app.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

@Dao
public interface DeluxeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(User value);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(Vehicle value);

  @Query("SELECT * FROM vehicles")
  LiveData<List<Vehicle>> vehicles();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(CustomizationPreset value);

  @Query("SELECT * FROM presets")
  LiveData<List<CustomizationPreset>> presets();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(Project value);

  @Query("SELECT * FROM projects WHERE userId = :userId ORDER BY createdAt DESC")
  LiveData<List<Project>> projects(String userId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(Quote value);

  @Query("SELECT * FROM quotes WHERE userId = :userId ORDER BY createdAt DESC")
  LiveData<List<Quote>> quotes(String userId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(NotificationItem value);

  @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
  LiveData<List<NotificationItem>> notifications(String userId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(Branch value);

  @Query("SELECT * FROM branches")
  LiveData<List<Branch>> branches();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(Favorite value);

  @Query("SELECT * FROM favorites WHERE userId = :userId")
  LiveData<List<Favorite>> favorites(String userId);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void put(CustomizationOption value);

  @Query("SELECT * FROM options")
  LiveData<List<CustomizationOption>> options();

  @Query("SELECT COUNT(*) FROM options")
  int optionCount();

  @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
  User userByEmail(String email);

  @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
  User user(String id);

  @Query("SELECT COUNT(*) FROM vehicles")
  int vehicleCount();

  @Query("SELECT * FROM favorites WHERE id = :id")
  Favorite favorite(String id);

  @Query("DELETE FROM favorites WHERE id = :id")
  void deleteFavorite(String id);

  @Query("SELECT * FROM projects WHERE id = :id")
  Project project(String id);

  @Query("SELECT * FROM quotes WHERE id = :id")
  Quote quote(String id);
}
