package com.deluxedesign.app.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.deluxedesign.app.DeluxeApplication;
import com.deluxedesign.app.data.OptionsCatalog;
import com.deluxedesign.app.data.local.SeedData;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.repository.*;
import com.deluxedesign.app.util.QuoteCalculator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.*;

/** Shared workflow state survives recreation; repositories own durable data. */
public class AppViewModel extends AndroidViewModel {
  public final RepositoryProvider repositories;
  private final SavedStateHandle state;
  public final LiveData<User> session;
  public final LiveData<List<Vehicle>> vehicles;
  public final LiveData<List<CustomizationPreset>> presets;
  public final LiveData<List<CustomizationOption>> options;
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
    options = repositories.vehicleRepository().options();
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
    return configuration();
  }

  public CustomizationPreset preset(String id) {
    for (CustomizationPreset p : list(presets)) if (p.id.equals(id)) return p;
    return null;
  }

  public Project project() {
    for (Project p : list(projects)) if (p.id.equals(value("project", ""))) return p;
    return null;
  }

  public Project project(String id) {
    for (Project p : list(projects)) if (p.id.equals(id)) return p;
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
    state.set("angle", "front");
    state.set("editing", "");
    Map<String, String> defaults = OptionsCatalog.templateOptions(OptionsCatalog.TEMPLATE_RACING);
    for (String category : OptionsCatalog.CATEGORIES) state.set("c_" + category, defaults.get(category));
    state.set("origSnapshot", snapshot());
    clearHistory();
    touch();
  }

  public void editProject(Project p) {
    state.set("project", p.id);
    state.set("editing", p.id);
    state.set("vehicle", p.vehicleId);
    state.set("angle", "front");
    List<String> ids = parseOptions(p.optionsJson);
    if (ids.isEmpty() && OptionsCatalog.isTemplate(p.presetId))
      ids = new ArrayList<>(OptionsCatalog.templateOptions(p.presetId).values());
    for (int i = 0; i < OptionsCatalog.CATEGORIES.length && i < ids.size(); i++)
      if (OptionsCatalog.option(ids.get(i)) != null)
        state.set("c_" + OptionsCatalog.CATEGORIES[i], ids.get(i));
    state.set("origSnapshot", snapshot());
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

  public Map<String, String> selections() {
    Map<String, String> map = new LinkedHashMap<>();
    Map<String, String> fallback = OptionsCatalog.templateOptions(OptionsCatalog.TEMPLATE_RACING);
    for (String category : OptionsCatalog.CATEGORIES) {
      String id = value("c_" + category, "");
      if (OptionsCatalog.option(id) == null) id = fallback.get(category);
      map.put(category, id);
    }
    return map;
  }

  public List<String> optionIds() {
    return new ArrayList<>(selections().values());
  }

  private String snapshot() {
    return new Gson().toJson(optionIds());
  }

  private void applySnapshot(String snapshot) {
    try {
      List<String> ids =
          new Gson()
              .fromJson(snapshot, new TypeToken<List<String>>() {}.getType());
      if (ids == null) return;
      for (int i = 0; i < OptionsCatalog.CATEGORIES.length && i < ids.size(); i++)
        if (OptionsCatalog.option(ids.get(i)) != null)
          state.set("c_" + OptionsCatalog.CATEGORIES[i], ids.get(i));
    } catch (Exception ignored) {
    }
    touch();
  }

  public CustomizationOption option(String category) {
    return OptionsCatalog.option(selections().get(category));
  }

  public List<CustomizationOption> configurationOptions() {
    List<CustomizationOption> list = new ArrayList<>();
    for (String category : OptionsCatalog.CATEGORIES) list.add(option(category));
    return list;
  }

  public String templateOfCurrent() {
    return OptionsCatalog.templateFor(selections());
  }

  public long basePrice() {
    return basePrice(value("vehicle", "porsche"));
  }

  public long basePrice(String vehicleId) {
    Vehicle v = vehicle(vehicleId);
    if (v != null && v.basePriceCents != null) return v.basePriceCents;
    Vehicle s = SeedData.vehicle(vehicleId);
    return s == null || s.basePriceCents == null ? 0 : s.basePriceCents;
  }

  public long configurationPrice() {
    long total = basePrice();
    for (CustomizationOption o : configurationOptions())
      if (o.priceDeltaCents != null) total += o.priceDeltaCents;
    return total;
  }

  public String templateName() {
    String template = templateOfCurrent();
    return OptionsCatalog.isTemplate(template) ? OptionsCatalog.templateName(template) : "Personalizado";
  }

  public CustomizationPreset configuration() {
    return buildConfiguration(value("vehicle", "porsche"), optionIds());
  }

  public CustomizationPreset configurationFor(Project p) {
    if (p == null) return null;
    return buildConfiguration(p.vehicleId, parseOptions(p.optionsJson));
  }

  private CustomizationPreset buildConfiguration(String vehicleId, List<String> ids) {
    long base = basePrice(vehicleId);
    CustomizationPreset p = new CustomizationPreset();
    Map<String, String> map = new LinkedHashMap<>();
    for (int i = 0; i < OptionsCatalog.CATEGORIES.length && i < ids.size(); i++)
      map.put(OptionsCatalog.CATEGORIES[i], ids.get(i));
    for (String category : OptionsCatalog.CATEGORIES) {
      String id = map.get(category);
      if (OptionsCatalog.option(id) == null)
        id = OptionsCatalog.templateOptions(OptionsCatalog.TEMPLATE_RACING).get(category);
      map.put(category, id);
    }
    String template = OptionsCatalog.templateFor(map);
    p.id = vehicleId + "_" + template;
    p.vehicleId = vehicleId;
    p.name = OptionsCatalog.isTemplate(template) ? OptionsCatalog.templateName(template) : "Personalizado";
    p.paint = OptionsCatalog.option(map.get("paint")).label;
    p.finish = OptionsCatalog.option(map.get("finish")).label;
    p.vinyl = OptionsCatalog.option(map.get("vinyl")).label;
    p.wheels = OptionsCatalog.option(map.get("wheels")).label;
    p.bodyKit = OptionsCatalog.option(map.get("bodykit")).label;
    p.lights = OptionsCatalog.option(map.get("lights")).label;
    p.accessories = OptionsCatalog.option(map.get("accessories")).label;
    p.interior = OptionsCatalog.option(map.get("interior")).label;
    p.priceCents = base;
    for (String category : OptionsCatalog.CATEGORIES) {
      CustomizationOption o = OptionsCatalog.option(map.get(category));
      if (o.priceDeltaCents != null) p.priceCents += o.priceDeltaCents;
    }
    List<CustomizationOption> opts = new ArrayList<>();
    for (String category : OptionsCatalog.CATEGORIES) {
      CustomizationOption o = OptionsCatalog.option(map.get(category));
      if (o != null) opts.add(o);
    }
    p.options = opts;
    p.front = "ci_" + vehicleId + "_front34";
    p.side = "ci_" + vehicleId + "_side";
    p.rear = "ci_" + vehicleId + "_rear";
    return p;
  }

  public void selectOption(String category, String optionId) {
    if (optionId.equals(selections().get(category))) return;
    ArrayList<String> undo = history("undo");
    undo.add(snapshot());
    state.set("undo", undo);
    state.set("redo", new ArrayList<String>());
    state.set("c_" + category, optionId);
    touch();
  }

  public void selectTemplate(String templateId) {
    ArrayList<String> undo = history("undo");
    undo.add(snapshot());
    state.set("undo", undo);
    state.set("redo", new ArrayList<String>());
    Map<String, String> optionSet = OptionsCatalog.templateOptions(templateId);
    for (String category : OptionsCatalog.CATEGORIES)
      state.set("c_" + category, optionSet.get(category));
    touch();
  }

  public boolean canUndo() {
    return !history("undo").isEmpty();
  }

  public boolean canRedo() {
    return !history("redo").isEmpty();
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
    b.add(snapshot());
    state.set(to, b);
    applySnapshot(a.remove(a.size() - 1));
    state.set(from, a);
  }

  public boolean dirty() {
    String original = value("origSnapshot", snapshot());
    return !original.equals(snapshot());
  }

  public void discard() {
    applySnapshot(value("origSnapshot", snapshot()));
    clearHistory();
  }

  private List<String> parseOptions(String json) {
    if (json == null || json.isEmpty()) return Collections.emptyList();
    try {
      List<String> ids =
          new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
      return ids == null ? Collections.emptyList() : ids;
    } catch (Exception e) {
      return Collections.emptyList();
    }
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
    CustomizationPreset config = configuration();
    User current = user();
    if (current == null || config == null) {
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
    p.userId = current.id;
    p.vehicleId = config.vehicleId;
    p.presetId = config.id;
    p.name = name.trim();
    p.status = "Borrador";
    p.progress = 0;
    p.optionsJson = new Gson().toJson(optionIds());
    p.priceCents = configurationPrice();
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
                  state.set("origSnapshot", snapshot());
                  clearHistory();
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
    CustomizationPreset config = configurationFor(p);
    List<CustomizationOption> optionList = config == null ? null : config.options;
    List<QuoteItem> items = QuoteCalculator.items(basePrice(p.vehicleId), optionList);
    Quote q = new Quote();
    q.userId = user().id;
    q.projectId = p.id;
    q.customerName = customer.trim();
    q.notes = notes.trim();
    q.status = "Pendiente";
    q.itemsJson = new Gson().toJson(items);
    q.totalCents = QuoteCalculator.total(items);
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