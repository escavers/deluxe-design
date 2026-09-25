package com.deluxedesign.app.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
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
      Favorite.class,
      CustomizationOption.class
    },
    version = 2,
    exportSchema = false)
public abstract class DeluxeDatabase extends RoomDatabase {
  public static final Migration MIGRATION_1_2 =
      new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
          db.execSQL(
              "CREATE TABLE IF NOT EXISTS `options` (`id` TEXT NOT NULL, `category` TEXT, `label` TEXT, `detail` TEXT, `priceDeltaCents` INTEGER, `swatch` TEXT, PRIMARY KEY(`id`))");
          db.execSQL("ALTER TABLE `vehicles` ADD COLUMN `basePriceCents` INTEGER");
          db.execSQL("ALTER TABLE `projects` ADD COLUMN `optionsJson` TEXT");
          db.execSQL("ALTER TABLE `projects` ADD COLUMN `priceCents` INTEGER");
        }
      };

  public abstract DeluxeDao dao();
}
