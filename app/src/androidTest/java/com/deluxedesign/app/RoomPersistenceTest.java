package com.deluxedesign.app;

import static org.junit.Assert.*;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.deluxedesign.app.data.local.*;
import com.deluxedesign.app.domain.model.*;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoomPersistenceTest {
  @Test
  public void projectsAndQuotesSurviveDatabaseReopening() {
    Context c = ApplicationProvider.getApplicationContext();
    String name = "test-deluxe-" + System.currentTimeMillis() + ".db";
    DeluxeDatabase first = Room.databaseBuilder(c, DeluxeDatabase.class, name).build();
    Project p = new Project();
    p.id = "p1";
    p.userId = "alice";
    p.name = "Mi Porsche";
    p.presetId = "porsche_urban_dark";
    first.dao().put(p);
    Quote q = new Quote();
    q.id = "q1";
    q.projectId = "p1";
    q.userId = "alice";
    q.totalCents = 2140000;
    first.dao().put(q);
    first.close();
    DeluxeDatabase second = Room.databaseBuilder(c, DeluxeDatabase.class, name).build();
    assertEquals("Mi Porsche", second.dao().project("p1").name);
    assertEquals("porsche_urban_dark", second.dao().project("p1").presetId);
    assertEquals(2140000, second.dao().quote("q1").totalCents);
    second.close();
    c.deleteDatabase(name);
  }
}
