package com.deluxedesign.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.*;
import androidx.room.Room;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.repository.*;
import com.deluxedesign.app.util.*;
import java.util.*;
import java.util.concurrent.*;

public class DemoRepositoryProvider
    implements RepositoryProvider,
        AuthRepository,
        VehicleRepository,
        ProjectRepository,
        QuoteRepository,
        NotificationRepository,
        BranchRepository {
  private final DeluxeDao dao;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final Handler main = new Handler(Looper.getMainLooper());
  private final SharedPreferences preferences;
  private final MutableLiveData<User> session = new MutableLiveData<>();
  private final MutableLiveData<Boolean> ready = new MutableLiveData<>(false);
  private final MutableLiveData<String> issue = new MutableLiveData<>("");
  private String resetEmail = "", resetCode = "";
  private long resetExpires;

  public DemoRepositoryProvider(Context context) {
    preferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    DeluxeDatabase database =
        Room.databaseBuilder(context, DeluxeDatabase.class, "deluxe.db")
            .addMigrations(DeluxeDatabase.MIGRATION_1_2)
            .build();
    dao = database.dao();
    executor.execute(
        () -> {
          try {
            database.runInTransaction(() -> SeedData.install(dao));
            User user = dao.user(preferences.getString("userId", ""));
            main.post(
                () -> {
                  session.setValue(user);
                  ready.setValue(true);
                });
          } catch (Exception e) {
            main.post(
                () ->
                    issue.setValue(
                        "No se pudo abrir la base local. Cierra y vuelve a abrir la aplicación."));
          }
        });
  }

  private interface Work<T> {
    T run() throws Exception;
  }

  private <T> void run(Work<T> work, Result<T> result) {
    executor.execute(
        () -> {
          try {
            T value = work.run();
            main.post(() -> result.success(value));
          } catch (Exception e) {
            main.post(
                () ->
                    result.error(
                        e.getMessage() == null
                            ? "No se pudo completar la operación."
                            : e.getMessage()));
          }
        });
  }

  private User requireUser() {
    User u = session.getValue();
    if (u == null) throw new IllegalStateException("Inicia sesión para continuar.");
    return u;
  }

  private void establish(User user) {
    preferences.edit().putString("userId", user.id).apply();
    session.setValue(user);
  }

  public LiveData<Boolean> ready() {
    return ready;
  }

  public LiveData<String> issue() {
    return issue;
  }

  public boolean cloud() {
    return false;
  }

  public AuthRepository auth() {
    return this;
  }

  public VehicleRepository vehicleRepository() {
    return this;
  }

  public ProjectRepository projects() {
    return this;
  }

  public QuoteRepository quotes() {
    return this;
  }

  public NotificationRepository notifications() {
    return this;
  }

  public BranchRepository branchRepository() {
    return this;
  }

  public LiveData<User> session() {
    return session;
  }

  public void signIn(String email, String password, Result<User> result) {
    run(
        () -> {
          User u = dao.userByEmail(Validators.normalizeEmail(email));
          if (u == null || !PasswordHasher.verify(password, u.passwordHash))
            throw new IllegalArgumentException("Correo o contraseña incorrectos.");
          return u;
        },
        new Result<User>() {
          public void success(User u) {
            establish(u);
            result.success(u);
          }

          public void error(String m) {
            result.error(m);
          }
        });
  }

  public void register(
      String name, String email, String phone, String password, Result<User> result) {
    run(
        () -> {
          String error = Validators.credentials(email, password);
          if (error != null) throw new IllegalArgumentException(error);
          if (name.trim().isEmpty()) throw new IllegalArgumentException("Escribe tu nombre.");
          String normalized = Validators.normalizeEmail(email);
          if (dao.userByEmail(normalized) != null)
            throw new IllegalArgumentException("Este correo ya tiene una cuenta.");
          User u = new User();
          u.id = UUID.randomUUID().toString();
          u.name = name.trim();
          u.email = normalized;
          u.phone = phone.trim();
          u.passwordHash = PasswordHasher.hash(password);
          dao.put(u);
          return u;
        },
        new Result<User>() {
          public void success(User u) {
            establish(u);
            result.success(u);
          }

          public void error(String m) {
            result.error(m);
          }
        });
  }

  public void signOut() {
    preferences.edit().remove("userId").apply();
    session.setValue(null);
    resetCode = "";
  }

  public void signInWithGoogle(String idToken, Result<User> result) {
    result.error("El acceso con Google está disponible con el proveedor Firebase.");
  }

  public void updateProfile(String name, String phone, String avatar, Result<User> result) {
    run(
        () -> {
          if (name.trim().isEmpty()) throw new IllegalArgumentException("Escribe tu nombre.");
          User u = dao.user(requireUser().id);
          u.name = name.trim();
          u.phone = phone.trim();
          u.avatar = avatar;
          dao.put(u);
          return u;
        },
        new Result<User>() {
          public void success(User u) {
            establish(u);
            result.success(u);
          }

          public void error(String m) {
            result.error(m);
          }
        });
  }

  public void requestPasswordReset(String email, Result<String> result) {
    run(
        () -> {
          if (dao.userByEmail(Validators.normalizeEmail(email)) == null)
            throw new IllegalArgumentException("No hay una cuenta local con ese correo.");
          resetEmail = Validators.normalizeEmail(email);
          resetCode =
              String.format(
                  java.util.Locale.US, "%06d", new java.security.SecureRandom().nextInt(1000000));
          resetExpires = System.currentTimeMillis() + 600000;
          return resetCode;
        },
        result);
  }

  public void resetPassword(String email, String token, String password, Result<Void> result) {
    run(
        () -> {
          if (!Validators.password(password))
            throw new IllegalArgumentException("Usa al menos 8 caracteres.");
          if (resetCode.isEmpty()
              || System.currentTimeMillis() > resetExpires
              || !resetEmail.equals(Validators.normalizeEmail(email))
              || !resetCode.equals(token))
            throw new IllegalArgumentException("Código incorrecto o vencido. Solicita otro.");
          User u = dao.userByEmail(resetEmail);
          u.passwordHash = PasswordHasher.hash(password);
          dao.put(u);
          resetCode = "";
          return null;
        },
        result);
  }

  public void changePassword(String current, String password, Result<Void> result) {
    run(
        () -> {
          User u = dao.user(requireUser().id);
          if (!PasswordHasher.verify(current, u.passwordHash))
            throw new IllegalArgumentException("Contraseña actual incorrecta.");
          if (!Validators.password(password))
            throw new IllegalArgumentException("Usa al menos 8 caracteres.");
          u.passwordHash = PasswordHasher.hash(password);
          dao.put(u);
          return null;
        },
        result);
  }

  public LiveData<List<Vehicle>> vehicles() {
    return dao.vehicles();
  }

  public LiveData<List<CustomizationPreset>> presets() {
    return dao.presets();
  }

  public LiveData<List<Favorite>> favorites(String userId) {
    return dao.favorites(userId);
  }

  public LiveData<List<CustomizationOption>> options() {
    return dao.options();
  }

  public void toggleFavorite(String userId, String vehicleId, Result<Void> result) {
    run(
        () -> {
          if (!requireUser().id.equals(userId))
            throw new IllegalStateException("Sesión no válida.");
          String id = userId + "_" + vehicleId;
          if (dao.favorite(id) == null) {
            Favorite f = new Favorite();
            f.id = id;
            f.userId = userId;
            f.vehicleId = vehicleId;
            dao.put(f);
          } else dao.deleteFavorite(id);
          return null;
        },
        result);
  }

  public LiveData<List<Project>> projects(String userId) {
    return dao.projects(userId);
  }

  public void saveProject(Project p, Result<Project> result) {
    run(
        () -> {
          p.userId = requireUser().id;
          Project existing = dao.project(p.id);
          if (existing != null && !existing.userId.equals(p.userId))
            throw new IllegalArgumentException("Proyecto no disponible.");
          if (p.id.isEmpty()) p.id = UUID.randomUUID().toString();
          if (p.createdAt == 0) p.createdAt = System.currentTimeMillis();
          dao.put(p);
          return p;
        },
        result);
  }

  public LiveData<List<Quote>> quotes(String userId) {
    return dao.quotes(userId);
  }

  public void saveQuote(Quote q, Result<Quote> result) {
    run(
        () -> {
          q.userId = requireUser().id;
          Project p = dao.project(q.projectId);
          if (p == null || !p.userId.equals(q.userId))
            throw new IllegalArgumentException("Selecciona un proyecto válido.");
          if (q.id.isEmpty())
            q.id = "COT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
          if (q.createdAt == 0) q.createdAt = System.currentTimeMillis();
          dao.put(q);
          NotificationItem n = new NotificationItem();
          n.id = UUID.randomUUID().toString();
          n.userId = q.userId;
          n.title = "Cotización creada";
          n.message = q.id + " está pendiente de revisión.";
          n.createdAt = System.currentTimeMillis();
          dao.put(n);
          return q;
        },
        result);
  }

  public LiveData<List<NotificationItem>> notifications(String userId) {
    return dao.notifications(userId);
  }

  public void markRead(NotificationItem n, Result<Void> result) {
    run(
        () -> {
          if (!n.userId.equals(requireUser().id))
            throw new IllegalArgumentException("Notificación no disponible.");
          n.read = true;
          dao.put(n);
          return null;
        },
        result);
  }

  public LiveData<List<Branch>> branches() {
    return dao.branches();
  }
}
