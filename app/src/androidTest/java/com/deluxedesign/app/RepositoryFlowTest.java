package com.deluxedesign.app;

import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.deluxedesign.app.domain.model.User;
import com.deluxedesign.app.repository.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RepositoryFlowTest {
  private <T> T awaitResult(Consumer<Result<T>> action) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<T> value = new AtomicReference<>();
    AtomicReference<String> error = new AtomicReference<>();
    InstrumentationRegistry.getInstrumentation()
        .runOnMainSync(
            () ->
                action.accept(
                    new Result<T>() {
                      public void success(T result) {
                        value.set(result);
                        latch.countDown();
                      }

                      public void error(String message) {
                        error.set(message);
                        latch.countDown();
                      }
                    }));
    assertTrue("Repository callback timed out", latch.await(15, TimeUnit.SECONDS));
    if (error.get() != null) throw new IllegalArgumentException(error.get());
    return value.get();
  }

  @Test
  public void registrationRecoveryPasswordChangeAndAccountIsolation() throws Exception {
    DeluxeApplication app = ApplicationProvider.getApplicationContext();
    RepositoryProvider repositories = app.repositories();
    AuthRepository auth = repositories.auth();
    String email = "test-" + System.nanoTime() + "@deluxedesign.app";
    User registered =
        this.<User>awaitResult(r -> auth.register("Prueba", email, "70000000", "Password1", r));
    assertEquals(email, registered.email);
    assertFalse(registered.passwordHash.contains("Password1"));
    this.<Void>awaitResult(
        r -> repositories.vehicleRepository().toggleFavorite(registered.id, "bmw", r));
    User updated =
        this.<User>awaitResult(r -> auth.updateProfile("Perfil editado", "71111111", "", r));
    assertEquals("Perfil editado", updated.name);
    this.<Void>awaitResult(r -> auth.changePassword("Password1", "Password2", r));
    InstrumentationRegistry.getInstrumentation().runOnMainSync(auth::signOut);
    try {
      this.<User>awaitResult(r -> auth.signIn(email, "incorrecta", r));
      fail("Invalid password must be rejected");
    } catch (IllegalArgumentException expected) {
      assertFalse(expected.getMessage().isEmpty());
    }
    String code = this.<String>awaitResult(r -> auth.requestPasswordReset(email, r));
    assertEquals(6, code.length());
    this.<Void>awaitResult(r -> auth.resetPassword(email, code, "Password3", r));
    try {
      this.<Void>awaitResult(r -> auth.resetPassword(email, code, "Password4", r));
      fail("Recovery code must be single use");
    } catch (IllegalArgumentException expected) {
      assertFalse(expected.getMessage().isEmpty());
    }
    User signedIn = this.<User>awaitResult(r -> auth.signIn(email, "Password3", r));
    assertEquals(registered.id, signedIn.id);
    try {
      this.<Void>awaitResult(
          r -> repositories.vehicleRepository().toggleFavorite("another-user", "bmw", r));
      fail("Cannot change another account's favorites");
    } catch (IllegalArgumentException expected) {
      assertFalse(expected.getMessage().isEmpty());
    }
    this.<User>awaitResult(r -> auth.signIn("demo@deluxedesign.app", "Demo1234", r));
  }
}
