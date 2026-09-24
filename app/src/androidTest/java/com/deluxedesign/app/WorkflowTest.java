package com.deluxedesign.app;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.repository.Result;
import com.deluxedesign.app.ui.AppViewModel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WorkflowTest {
  @Test
  public void catalogToPresetProjectQuoteAndAllLayouts() throws Exception {
    try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
      AtomicReference<AppViewModel> model = new AtomicReference<>();
      scenario.onActivity(a -> model.set(new ViewModelProvider(a).get(AppViewModel.class)));
      AppViewModel vm = model.get();
      long deadline = System.currentTimeMillis() + 15000;
      while (!Boolean.TRUE.equals(vm.repositories.ready().getValue())
          && System.currentTimeMillis() < deadline) Thread.sleep(100);
      assertEquals(Boolean.TRUE, vm.repositories.ready().getValue());
      CountDownLatch login = new CountDownLatch(1);
      AtomicReference<String> error = new AtomicReference<>();
      scenario.onActivity(
          a ->
              vm.repositories
                  .auth()
                  .signIn(
                      "demo@deluxedesign.app",
                      "Demo1234",
                      new Result<User>() {
                        public void success(User u) {
                          login.countDown();
                        }

                        public void error(String m) {
                          error.set(m);
                          login.countDown();
                        }
                      }));
      assertTrue(login.await(10, TimeUnit.SECONDS));
      assertNull(error.get());
      scenario.onActivity(
          a -> {
            vm.selectVehicle("porsche");
            vm.selectPreset("porsche_street_blue");
            vm.undo();
            assertEquals("porsche_racing_red", vm.value("preset", ""));
            vm.redo();
            assertEquals("porsche_street_blue", vm.value("preset", ""));
          });
      CountDownLatch saved = new CountDownLatch(1);
      scenario.onActivity(a -> vm.saveProject("Prueba completa", p -> saved.countDown()));
      assertTrue(saved.await(10, TimeUnit.SECONDS));
      deadline = System.currentTimeMillis() + 10000;
      while (vm.project() == null && System.currentTimeMillis() < deadline) Thread.sleep(100);
      assertNotNull(vm.project());
      CountDownLatch quoted = new CountDownLatch(1);
      scenario.onActivity(
          a -> vm.createQuote("Cliente prueba", "Ensayo automático", q -> quoted.countDown()));
      assertTrue(quoted.await(10, TimeUnit.SECONDS));
      deadline = System.currentTimeMillis() + 10000;
      while (vm.quote() == null && System.currentTimeMillis() < deadline) Thread.sleep(100);
      assertNotNull(vm.quote());
      assertEquals(2390000, vm.quote().totalCents);
      int[] destinations = {
        R.id.home,
        R.id.catalog,
        R.id.vehicle_detail,
        R.id.customizer,
        R.id.confirmation,
        R.id.project_ready,
        R.id.projects,
        R.id.project_detail,
        R.id.quotes,
        R.id.quote_detail,
        R.id.new_quote,
        R.id.profile,
        R.id.my_vehicles,
        R.id.branches,
        R.id.notifications,
        R.id.change_password,
        R.id.welcome,
        R.id.login,
        R.id.register,
        R.id.recover,
        R.id.reset
      };
      for (int id : destinations) {
        scenario.onActivity(
            a ->
                ((NavHostFragment) a.getSupportFragmentManager().findFragmentById(R.id.navHost))
                    .getNavController()
                    .navigate(id));
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        Thread.sleep(150);
        if (InstrumentationRegistry.getArguments()
            .getString("screenshots", "false")
            .equals("true")) {
          Bitmap b =
              InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
          java.io.File dir =
              new java.io.File(
                  InstrumentationRegistry.getInstrumentation()
                      .getTargetContext()
                      .getExternalFilesDir(null),
                  "screenshots");
          dir.mkdirs();
          try (java.io.OutputStream out =
              new java.io.FileOutputStream(new java.io.File(dir, id + ".png"))) {
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
          }
          b.recycle();
        }
      }
      scenario.recreate();
      scenario.onActivity(
          a -> {
            AppViewModel restored = new ViewModelProvider(a).get(AppViewModel.class);
            assertEquals("porsche_street_blue", restored.value("preset", ""));
          });
    }
  }
}
