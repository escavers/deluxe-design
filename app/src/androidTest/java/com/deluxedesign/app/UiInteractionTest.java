package com.deluxedesign.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.deluxedesign.app.ui.AppViewModel;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UiInteractionTest {
  // Espresso 3.4's built-in action does not recognize AndroidX NestedScrollView.
  private static androidx.test.espresso.ViewAction scrollTo() {
    return new androidx.test.espresso.ViewAction() {
      public org.hamcrest.Matcher<android.view.View> getConstraints() {
        return withEffectiveVisibility(Visibility.VISIBLE);
      }

      public String getDescription() {
        return "Scroll nested container to the control";
      }

      public void perform(androidx.test.espresso.UiController ui, android.view.View view) {
        android.graphics.Rect bounds = new android.graphics.Rect();
        view.getDrawingRect(bounds);
        view.requestRectangleOnScreen(bounds, true);
        ui.loopMainThreadUntilIdle();
      }
    };
  }

  private void waitUntil(java.util.function.BooleanSupplier condition) throws Exception {
    long limit = System.currentTimeMillis() + 15000;
    while (!condition.getAsBoolean() && System.currentTimeMillis() < limit) Thread.sleep(100);
    assertTrue(condition.getAsBoolean());
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
  }

  @Test
  public void controlsSearchFavoritePresetAndBackConfirmation() throws Exception {
    try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
      AtomicReference<AppViewModel> model = new AtomicReference<>();
      scenario.onActivity(a -> model.set(new ViewModelProvider(a).get(AppViewModel.class)));
      AppViewModel vm = model.get();
      waitUntil(() -> Boolean.TRUE.equals(vm.repositories.ready().getValue()));
      scenario.onActivity(
          a -> {
            vm.repositories.auth().signOut();
            ((NavHostFragment) a.getSupportFragmentManager().findFragmentById(R.id.navHost))
                .getNavController()
                .navigate(
                    R.id.login,
                    null,
                    new NavOptions.Builder().setPopUpTo(R.id.main_graph, true).build());
          });
      onView(withId(R.id.email)).perform(replaceText("demo@deluxedesign.app"));
      onView(withId(R.id.password)).perform(replaceText("Demo1234"), closeSoftKeyboard());
      onView(withId(R.id.login)).perform(scrollTo(), click());
      waitUntil(() -> vm.user() != null && !Boolean.TRUE.equals(vm.busy.getValue()));
      onView(withId(R.id.catalog)).perform(click());
      onView(withId(R.id.search)).perform(replaceText("NoExiste"), closeSoftKeyboard());
      onView(withId(R.id.count)).check(matches(withText("0 vehículos disponibles")));
      onView(withId(R.id.search)).perform(replaceText("BMW"), closeSoftKeyboard());
      onView(withId(R.id.count)).check(matches(withText("1 vehículos disponibles")));
      onView(withText("BMW M4 Competition")).perform(click());
      boolean previous = vm.favorite("bmw");
      onView(withId(R.id.favorite)).perform(scrollTo(), click());
      waitUntil(() -> vm.favorite("bmw") != previous);
      onView(withId(R.id.customize)).perform(scrollTo(), click());
      onView(withId(R.id.heroFrame)).check(matches(isDisplayed()));
      onView(withText("Urban Dark")).perform(scrollTo(), click());
      assertEquals("urban_dark", vm.templateOfCurrent());
      pressBack();
      onView(withText("¿Salir sin guardar?")).check(matches(isDisplayed()));
      onView(withText("Seguir editando")).perform(click());
      onView(withId(R.id.save)).perform(scrollTo(), click());
      onView(withId(R.id.projectName))
          .perform(scrollTo(), replaceText("Proyecto desde controles"), closeSoftKeyboard());
      onView(withId(R.id.confirm)).perform(scrollTo(), click());
      waitUntil(() -> vm.project() != null && vm.project().name.equals("Proyecto desde controles"));
      onView(withId(R.id.quote)).perform(scrollTo(), click());
      onView(withId(R.id.generate)).perform(scrollTo(), click());
      waitUntil(() -> vm.quote() != null && vm.quote().projectId.equals(vm.project().id));
      onView(withId(R.id.total)).check(matches(isDisplayed()));
    }
  }
}
