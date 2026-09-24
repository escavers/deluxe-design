package com.deluxedesign.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.deluxedesign.app.domain.model.*;

@Database(
    entities = {
      User.class,
      Vehicle.class,
      CustomizationPreset.class,
      Project.class,
      Quote.class,
      NotificationItem.class,
      Branch.class,
      Favorite.class
    },
    version = 1,
    exportSchema = false)
public abstract class DeluxeDatabase extends RoomDatabase {
  public abstract DeluxeDao dao();
}
