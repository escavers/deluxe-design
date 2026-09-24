package com.deluxedesign.app.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.deluxedesign.app.DeluxeApplication;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.repository.*;
import com.deluxedesign.app.util.QuoteCalculator;
import com.google.gson.Gson;
import java.util.*;

/** Shared workflow state survives recreation; repositories own durable data. */
public class AppViewModel extends AndroidViewModel {
  public final RepositoryProvider repositories;
  private final SavedStateHandle state;
  public final LiveData<User> session;
  public final LiveData<List<Vehicle>> vehicles;
  public final LiveData<List<CustomizationPreset>> presets;
  public final LiveData<List<Project>> projects;
  public final LiveData<List<Quote>> quotes;
  public final LiveData<List<Favorite>> favorites;
  public final LiveData<List<NotificationItem>> notifications;
  public final LiveData<List<Branch>> branches;
  public final MutableLiveData<Integer> revision = new MutableLiveData<>(0);
  public final MutableLiveData<Boolean> busy = new MutableLiveData<>(false);
  public final MutableLiveData<String> error = new MutableLiveData<>("");

  public AppViewModel(@NonNull Application app, SavedStateHandle saved) {
    super(app);
    state = saved;
    repositories = ((DeluxeApplication) app).repositories();
    session = repositories.auth().session();
    vehicles = repositories.vehicleRepository().vehicles();
    presets = repositories.vehicleRepository().presets();
    branches = repositories.branchRepository().branches();
    projects =
        Transformations.switchMap(
            session, u -> repositories.projects().projects(u == null ? "" : u.id));
    quotes =
        Transformations.switchMap(
            session, u -> repositories.quotes().quotes(u == null ? "" : u.id));
    favorites =
        Transformations.switchMap(
            session, u -> repositories.vehicleRepository().favorites(u == null ? "" : u.id));
    notifications =
        Transformations.switchMap(
            session, u -> repositories.notifications().notifications(u == null ? "" : u.id));
  }

  public String value(String key, String fallback) {
    String v = state.get(key);
    return v == null ? fallback : v;
  }

  public void set(String key, String value) {
    state.set(key, value);
    touch();
  }

  public void touch() {
    revision.setValue(revision.getValue() == null ? 1 : revision.getValue() + 1);
  }

  public static <T> List<T> list(LiveData<List<T>> data) {
    return data.getValue() == null ? Collections.emptyList() : data.getValue();
  }

  public User user() {
    return session.getValue();
  }

  public Vehicle vehicle() {
    String id = value("vehicle", "porsche");
    for (Vehicle v : list(vehicles)) if (v.id.equals(id)) return v;
    return null;
  }

  public CustomizationPreset preset() {
    String id = value("preset", value("vehicle", "porsche") + "_racing_red");
    for (CustomizationPreset p : list(presets)) if (p.id.equals(id)) return p;
    return null;
  }

  public CustomizationPreset preset(String id) {
    for (CustomizationPreset p : list(presets)) if (p.id.equals(id)) return p;
    return null;
  }

  public Project project() {
    for (Project p : list(projects)) if (p.id.equals(value("project", ""))) return p;
    return null;
  }

  public Quote quote() {
    for (Quote q : list(quotes)) if (q.id.equals(value("quote", ""))) return q;
    return null;
  }

  public Vehicle vehicle(String id) {
    for (Vehicle v : list(vehicles)) if (v.id.equals(id)) return v;
    return null;
  }

  public List<CustomizationPreset> vehiclePresets() {
    List<CustomizationPreset> out = new ArrayList<>();
    for (CustomizationPreset p : list(presets))
      if (p.vehicleId.equals(value("vehicle", "porsche"))) out.add(p);
    return out;
  }

  public void selectVehicle(String id) {
    state.set("vehicle", id);
    state.set("preset", id + "_racing_red");
    state.set("originalPreset", id + "_racing_red");
    state.set("editing", "");
    state.set("angle", "front");
    clearHistory();
    touch();
  }

  public void editProject(Project p) {
    state.set("project", p.id);
    state.set("editing", p.id);
    state.set("vehicle", p.vehicleId);
    state.set("preset", p.presetId);
    state.set("originalPreset", p.presetId);
    state.set("angle", "front");
    clearHistory();
    touch();
  }

  private void clearHistory() {
    state.set("undo", new ArrayList<String>());
    state.set("redo", new ArrayList<String>());
  }

  private ArrayList<String> history(String key) {
    ArrayList<String> list = state.get(key);
    return list == null ? new ArrayList<>() : new ArrayList<>(list);
  }

  public void selectPreset(String id) {
    if (id.equals(value("preset", ""))) return;
    ArrayList<String> undo = history("undo");
    undo.add(value("preset", ""));
    state.set("undo", undo);
    state.set("redo", new ArrayList<String>());
    set("preset", id);
  }

  public void undo() {
    move("undo", "redo");
  }

  public void redo() {
    move("redo", "undo");
  }

  private void move(String from, String to) {
    ArrayList<String> a = history(from);
    if (a.isEmpty()) return;
    ArrayList<String> b = history(to);
    b.add(value("preset", ""));
    state.set("preset", a.remove(a.size() - 1));
    state.set(from, a);
    state.set(to, b);
    touch();
  }

  public boolean canUndo() {
    return !history("undo").isEmpty();
  }

  public boolean canRedo() {
    return !history("redo").isEmpty();
  }

  public boolean dirty() {
    return !value("preset", "").equals(value("originalPreset", ""));
  }

  public void discard() {
    state.set("preset", value("originalPreset", value("vehicle", "porsche") + "_racing_red"));
    clearHistory();
    touch();
  }

  public boolean favorite(String vehicle) {
    if (user() == null) return false;
    for (Favorite f : list(favorites)) if (f.vehicleId.equals(vehicle)) return true;
    return false;
  }

  public <T> Result<T> task(java.util.function.Consumer<T> done) {
    busy.setValue(true);
    error.setValue("");
    return new Result<T>() {
      public void success(T value) {
        busy.setValue(false);
        done.accept(value);
      }

      public void error(String message) {
        busy.setValue(false);
        error.setValue(message);
      }
    };
  }

  public void saveProject(String name, java.util.function.Consumer<Project> done) {
    if (user() == null || preset() == null) {
      error.setValue("Selecciona un vehículo y un estilo.");
      return;
    }
    if (name.trim().isEmpty()) {
      error.setValue("Escribe un nombre para el proyecto.");
      return;
    }
    Project p = new Project();
    Project old = project();
    p.id = value("editing", "");
    p.userId = user().id;
    p.vehicleId = preset().vehicleId;
    p.presetId = preset().id;
    p.name = name.trim();
    if (old != null && old.id.equals(p.id)) {
      p.status = old.status;
      p.progress = old.progress;
      p.createdAt = old.createdAt;
      p.estimatedDelivery = old.estimatedDelivery;
    }
    repositories
        .projects()
        .saveProject(
            p,
            task(
                saved -> {
                  state.set("project", saved.id);
                  state.set("editing", saved.id);
                  state.set("originalPreset", saved.presetId);
                  touch();
                  done.accept(saved);
                }));
  }

  public void createQuote(String customer, String notes, java.util.function.Consumer<Quote> done) {
    Project p = project();
    if (p == null) {
      error.setValue("Selecciona un proyecto guardado.");
      return;
    }
    if (customer.trim().isEmpty()) {
      error.setValue("Escribe el nombre del cliente.");
      return;
    }
    CustomizationPreset style = preset(p.presetId);
    if (style == null) return;
    Quote q = new Quote();
    q.projectId = p.id;
    q.customerName = customer.trim();
    q.notes = notes.trim();
    q.itemsJson = new Gson().toJson(QuoteCalculator.items(style.priceCents));
    q.totalCents = QuoteCalculator.total(QuoteCalculator.items(style.priceCents));
    repositories
        .quotes()
        .saveQuote(
            q,
            task(
                saved -> {
                  state.set("quote", saved.id);
                  touch();
                  done.accept(saved);
                }));
  }
}
