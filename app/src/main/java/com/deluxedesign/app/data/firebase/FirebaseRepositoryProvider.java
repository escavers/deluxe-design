package com.deluxedesign.app.data.firebase;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.*;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.repository.*;
import com.deluxedesign.app.util.Validators;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import java.util.*;

/**
 * Implementación opcional en la nube. Se activa explícitamente con -Pfirebase=true después de
 * configurar las reglas de seguridad.
 */
public class FirebaseRepositoryProvider
    implements RepositoryProvider,
        AuthRepository,
        VehicleRepository,
        ProjectRepository,
        QuoteRepository,
        NotificationRepository,
        BranchRepository {
  private final FirebaseAuth auth;
  private final FirebaseFirestore db;
  private final FirebaseStorage storage;
  private final MutableLiveData<User> session = new MutableLiveData<>();
  private final MutableLiveData<Boolean> ready = new MutableLiveData<>(false);
  private final MutableLiveData<String> issue = new MutableLiveData<>("");

  public FirebaseRepositoryProvider(Context context) {
    if (FirebaseApp.getApps(context).isEmpty()) FirebaseApp.initializeApp(context);
    auth = FirebaseAuth.getInstance();
    db = FirebaseFirestore.getInstance();
    storage = FirebaseStorage.getInstance();
    try {
      db.setFirestoreSettings(
          new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build());
    } catch (Exception ignored) {
    }
    auth.addAuthStateListener(
        a -> {
          FirebaseUser current = a.getCurrentUser();
          if (current == null) {
            session.setValue(null);
            ready.setValue(true);
            return;
          }
          syncUser(current);
        });
  }

  private User userFrom(FirebaseUser current) {
    User u = new User();
    u.id = current.getUid();
    u.email = current.getEmail() == null ? "" : current.getEmail();
    u.name = current.getDisplayName() == null ? "Cliente" : current.getDisplayName();
    u.avatar = current.getPhotoUrl() == null ? "" : current.getPhotoUrl().toString();
    return u;
  }

  /** Best-effort sync of the user document. Never blocks the session / login flow. */
  private void syncUser(FirebaseUser current) {
    db.collection("users")
        .document(current.getUid())
        .get()
        .addOnSuccessListener(
            d -> {
              User u = d.toObject(User.class);
              if (u == null) {
                u = userFrom(current);
                session.setValue(u);
                putQuietly("users", u.id, u);
              } else {
                if ((u.avatar == null || u.avatar.isEmpty()) && current.getPhotoUrl() != null) {
                  u.avatar = current.getPhotoUrl().toString();
                  putQuietly("users", u.id, u);
                }
                session.setValue(u);
              }
              ready.setValue(true);
            })
        .addOnFailureListener(
            e -> {
              User u = userFrom(current);
              session.setValue(u);
              putQuietly("users", u.id, u);
              ready.setValue(true);
            });
  }

  private <T> void putQuietly(String collection, String id, T value) {
    db.collection(collection)
        .document(id)
        .set(value)
        .addOnFailureListener(e -> {});
  }

  private String uid() {
    FirebaseUser u = auth.getCurrentUser();
    return u == null ? "" : u.getUid();
  }

  private String error(Exception e) {
    return e.getLocalizedMessage() == null
        ? "No se pudo conectar con Firebase."
        : e.getLocalizedMessage();
  }

  public boolean cloud() {
    return true;
  }

  public LiveData<Boolean> ready() {
    return ready;
  }

  public LiveData<String> issue() {
    return issue;
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

  private <T> LiveData<List<T>> query(Query query, Class<T> type) {
    return new LiveData<List<T>>(new ArrayList<>()) {
      ListenerRegistration registration;
      FirebaseAuth.AuthStateListener authListener;

      void attach(boolean signedIn) {
        if (registration != null) {
          registration.remove();
          registration = null;
        }
        if (!signedIn) return;
        registration =
            query.addSnapshotListener(
                (snap, e) -> {
                  if (e != null) issue.setValue(error(e));
                  if (e == null && snap != null) setValue(snap.toObjects(type));
                });
      }

      @Override
      protected void onActive() {
        authListener = a -> attach(a.getCurrentUser() != null);
        auth.addAuthStateListener(authListener);
        attach(auth.getCurrentUser() != null);
      }

      @Override
      protected void onInactive() {
        if (registration != null) {
          registration.remove();
          registration = null;
        }
        if (authListener != null) auth.removeAuthStateListener(authListener);
      }
    };
  }

  private <T> void put(String collection, String id, T value, Result<T> result) {
    db.collection(collection)
        .document(id)
        .set(value)
        .addOnSuccessListener(v -> result.success(value))
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void signIn(String email, String password, Result<User> result) {
    auth.signInWithEmailAndPassword(Validators.normalizeEmail(email), password)
        .addOnSuccessListener(
            r -> {
              FirebaseUser current = auth.getCurrentUser();
              if (current == null) {
                result.error("No se pudo iniciar sesión.");
                return;
              }
              User local = userFrom(current);
              session.setValue(local);
              result.success(local);
              syncUser(current);
            })
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void signInWithGoogle(String idToken, Result<User> result) {
    if (idToken == null || idToken.isEmpty()) {
      result.error("No se pudo obtener el token de Google.");
      return;
    }
    auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
        .addOnSuccessListener(
            r -> {
              FirebaseUser current = auth.getCurrentUser();
              if (current == null) {
                result.error("No se pudo iniciar sesión con Google.");
                return;
              }
              User local = userFrom(current);
              session.setValue(local);
              result.success(local);
              syncUser(current);
            })
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void register(
      String name, String email, String phone, String password, Result<User> result) {
    String invalid = Validators.credentials(email, password);
    if (invalid != null || name.trim().isEmpty()) {
      result.error(invalid == null ? "Escribe tu nombre." : invalid);
      return;
    }
    auth.createUserWithEmailAndPassword(Validators.normalizeEmail(email), password)
        .addOnSuccessListener(
            r -> {
              User u = new User();
              u.id = uid();
              u.name = name.trim();
              u.email = Validators.normalizeEmail(email);
              u.phone = phone;
              put(
                  "users",
                  u.id,
                  u,
                  new Result<User>() {
                    public void success(User user) {
                      session.setValue(user);
                      result.success(user);
                    }

                    public void error(String message) {
                      result.error(message);
                    }
                  });
            })
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void signOut() {
    auth.signOut();
    session.setValue(null);
  }

  public void updateProfile(String name, String phone, String avatar, Result<User> result) {
    User old = session.getValue();
    if (old == null) {
      result.error("Inicia sesión.");
      return;
    }
    if (name.trim().isEmpty()) {
      result.error("Escribe tu nombre.");
      return;
    }
    User u = new User();
    u.id = old.id;
    u.email = old.email;
    u.name = name.trim();
    u.phone = phone;
    u.avatar = avatar;
    Result<User> after =
        new Result<User>() {
          public void success(User value) {
            session.setValue(value);
            result.success(value);
          }

          public void error(String message) {
            result.error(message);
          }
        };
    if (avatar.startsWith("content:")) {
      com.google.firebase.storage.StorageReference ref =
          storage.getReference("profiles/" + u.id + "/avatar");
      ref.putFile(Uri.parse(avatar))
          .continueWithTask(
              t -> {
                if (!t.isSuccessful()) throw t.getException();
                return ref.getDownloadUrl();
              })
          .addOnSuccessListener(
              uri -> {
                u.avatar = uri.toString();
                put("users", u.id, u, after);
              })
          .addOnFailureListener(e -> result.error(error(e)));
    } else put("users", u.id, u, after);
  }

  public void requestPasswordReset(String email, Result<String> result) {
    auth.sendPasswordResetEmail(Validators.normalizeEmail(email))
        .addOnSuccessListener(v -> result.success("email"))
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void resetPassword(String email, String token, String password, Result<Void> result) {
    auth.confirmPasswordReset(token, password)
        .addOnSuccessListener(v -> result.success(null))
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public void changePassword(String current, String password, Result<Void> result) {
    FirebaseUser user = auth.getCurrentUser();
    if (user == null || user.getEmail() == null) {
      result.error("Inicia sesión.");
      return;
    }
    if (!Validators.password(password)) {
      result.error("Usa al menos 8 caracteres.");
      return;
    }
    user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), current))
        .continueWithTask(
            t -> {
              if (!t.isSuccessful()) throw t.getException();
              return user.updatePassword(password);
            })
        .addOnSuccessListener(v -> result.success(null))
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public LiveData<List<Vehicle>> vehicles() {
    return query(db.collection("vehicles"), Vehicle.class);
  }

  public LiveData<List<CustomizationPreset>> presets() {
    return query(db.collection("presets"), CustomizationPreset.class);
  }

  public LiveData<List<CustomizationOption>> options() {
    return query(db.collection("options"), CustomizationOption.class);
  }

  public LiveData<List<Favorite>> favorites(String userId) {
    return query(db.collection("favorites").whereEqualTo("userId", userId), Favorite.class);
  }

  public void toggleFavorite(String userId, String vehicleId, Result<Void> result) {
    if (!uid().equals(userId)) {
      result.error("Sesión no válida.");
      return;
    }
    DocumentReference ref = db.collection("favorites").document(userId + "_" + vehicleId);
    ref.get()
        .addOnSuccessListener(
            snap -> {
              if (snap.exists()) {
                ref.delete()
                    .addOnSuccessListener(v -> result.success(null))
                    .addOnFailureListener(e -> result.error(error(e)));
              } else {
                Favorite f = new Favorite();
                f.id = ref.getId();
                f.userId = userId;
                f.vehicleId = vehicleId;
                ref.set(f)
                    .addOnSuccessListener(v -> result.success(null))
                    .addOnFailureListener(e -> result.error(error(e)));
              }
            })
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public LiveData<List<Project>> projects(String userId) {
    return query(db.collection("projects").whereEqualTo("userId", userId), Project.class);
  }

  public void saveProject(Project p, Result<Project> result) {
    if (uid().isEmpty()) {
      result.error("Inicia sesión.");
      return;
    }
    p.userId = uid();
    if (p.id.isEmpty()) p.id = UUID.randomUUID().toString();
    if (p.createdAt == 0) p.createdAt = System.currentTimeMillis();
    put("projects", p.id, p, result);
  }

  public LiveData<List<Quote>> quotes(String userId) {
    return query(db.collection("quotes").whereEqualTo("userId", userId), Quote.class);
  }

  public void saveQuote(Quote q, Result<Quote> result) {
    if (uid().isEmpty()) {
      result.error("Inicia sesión.");
      return;
    }
    q.userId = uid();
    if (q.id.isEmpty()) q.id = "COT-" + UUID.randomUUID().toString().substring(0, 8);
    if (q.createdAt == 0) q.createdAt = System.currentTimeMillis();
    put("quotes", q.id, q, result);
  }

  public LiveData<List<NotificationItem>> notifications(String userId) {
    return query(
        db.collection("notifications").whereEqualTo("userId", userId), NotificationItem.class);
  }

  public void markRead(NotificationItem n, Result<Void> result) {
    if (!uid().equals(n.userId)) {
      result.error("Sesión no válida.");
      return;
    }
    db.collection("notifications")
        .document(n.id)
        .update("read", true)
        .addOnSuccessListener(v -> result.success(null))
        .addOnFailureListener(e -> result.error(error(e)));
  }

  public LiveData<List<Branch>> branches() {
    return query(db.collection("branches"), Branch.class);
  }
}
