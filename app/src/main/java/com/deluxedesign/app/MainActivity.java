package com.deluxedesign.app;

import android.os.Bundle;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.*;
import androidx.navigation.fragment.NavHostFragment;
import com.deluxedesign.app.databinding.ActivityMainBinding;
import com.deluxedesign.app.ui.AppViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {
  private ActivityMainBinding binding;
  private AppViewModel vm;
  private NavController nav;
  private boolean syncing;
  private android.net.ConnectivityManager connectivity;
  private final android.net.ConnectivityManager.NetworkCallback networkCallback =
      new android.net.ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(android.net.Network network) {
          updateNetwork();
        }

        @Override
        public void onLost(android.net.Network network) {
          updateNetwork();
        }
      };

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    vm = new ViewModelProvider(this).get(AppViewModel.class);
    nav =
        ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHost))
            .getNavController();
    nav.addOnDestinationChangedListener(
        (controller, d, args) -> {
          boolean hideNavigation = isAuth(d.getId()) || d.getId() == R.id.change_password;
          binding.bottomNav.setVisibility(hideNavigation ? View.GONE : View.VISIBLE);
          syncing = true;
          binding.bottomNav.getMenu().findItem(section(d.getId())).setChecked(true);
          syncing = false;
          bounceNavIcon(section(d.getId()));
          vm.error.setValue("");
        });
    binding.bottomNav.setOnItemSelectedListener(
        item -> {
          if (syncing) return true;
          binding.bottomNav.performHapticFeedback(
              android.view.HapticFeedbackConstants.KEYBOARD_TAP,
              android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
          if (nav.getCurrentDestination() != null
              && nav.getCurrentDestination().getId() != item.getItemId()) {
            leaveEditor(
                () ->
                    nav.navigate(
                        item.getItemId(),
                        null,
                        new NavOptions.Builder()
                            .setEnterAnim(R.anim.nav_fade_in)
                            .setExitAnim(R.anim.nav_fade_out)
                            .setPopEnterAnim(R.anim.nav_fade_in)
                            .setPopExitAnim(R.anim.nav_fade_out)
                            .setPopUpTo(R.id.home, false)
                            .setLaunchSingleTop(true)
                            .build()),
                true);
            return false;
          }
          return false;
        });
    getOnBackPressedDispatcher()
        .addCallback(
            this,
            new OnBackPressedCallback(true) {
              public void handleOnBackPressed() {
                leaveEditor(
                    () -> {
                      if (!nav.popBackStack()) finish();
                    },
                    false);
              }
            });
    vm.repositories
        .ready()
        .observe(
            this,
            ready -> {
              if (Boolean.TRUE.equals(ready)
                  && vm.user() == null
                  && nav.getCurrentDestination() != null
                  && !isAuth(nav.getCurrentDestination().getId())) {
                nav.navigate(
                    R.id.login,
                    null,
                    new NavOptions.Builder().setPopUpTo(R.id.main_graph, true).build());
                return;
              }
              if (Boolean.TRUE.equals(ready)
                  && state == null
                  && vm.user() != null
                  && nav.getCurrentDestination().getId() == R.id.welcome)
                nav.navigate(
                    R.id.home,
                    null,
                    new NavOptions.Builder().setPopUpTo(R.id.welcome, true).build());
            });
  }

  @Override
  protected void onStart() {
    super.onStart();
    connectivity = getSystemService(android.net.ConnectivityManager.class);
    connectivity.registerDefaultNetworkCallback(networkCallback);
    updateNetwork();
  }

  @Override
  protected void onStop() {
    connectivity.unregisterNetworkCallback(networkCallback);
    super.onStop();
  }

  private void updateNetwork() {
    runOnUiThread(
        () -> {
          boolean offline = connectivity.getActiveNetwork() == null;
          binding.networkStatus.setText(
              vm.repositories.cloud()
                  ? "Sin conexión · los cambios remotos requieren Internet"
                  : "Sin conexión · modo local disponible");
          binding.networkStatus.setVisibility(offline ? View.VISIBLE : View.GONE);
        });
  }

  private void bounceNavIcon(int itemId) {
    View item = binding.bottomNav.findViewById(itemId);
    if (item == null) return;
    android.widget.ImageView icon = item.findViewById(com.google.android.material.R.id.icon);
    if (icon == null) return;
    icon.animate()
        .scaleX(1.18f)
        .scaleY(1.18f)
        .setDuration(90)
        .withEndAction(
            () ->
                icon.animate().scaleX(1f).scaleY(1f).setDuration(170).start())
        .start();
  }

  private void leaveEditor(Runnable action, boolean changingTab) {
    int id = nav.getCurrentDestination() == null ? 0 : nav.getCurrentDestination().getId();
    if ((id == R.id.customizer || (changingTab && id == R.id.confirmation)) && vm.dirty()) {
      new MaterialAlertDialogBuilder(this)
          .setTitle("¿Salir sin guardar?")
          .setMessage("Se descartará el estilo que todavía no has guardado.")
          .setNegativeButton("Seguir editando", (d, w) -> {})
          .setPositiveButton(
              "Descartar",
              (d, w) -> {
                vm.discard();
                action.run();
              })
          .show();
    } else action.run();
  }

  private boolean isAuth(int id) {
    return id == R.id.welcome
        || id == R.id.login
        || id == R.id.register
        || id == R.id.recover
        || id == R.id.reset;
  }

  private int section(int id) {
    if (id == R.id.catalog
        || id == R.id.vehicle_detail
        || id == R.id.customizer
        || id == R.id.confirmation) return R.id.catalog;
    if (id == R.id.projects || id == R.id.project_detail || id == R.id.project_ready)
      return R.id.projects;
    if (id == R.id.quotes || id == R.id.quote_detail || id == R.id.new_quote) return R.id.quotes;
    if (id == R.id.profile
        || id == R.id.edit_profile
        || id == R.id.my_vehicles
        || id == R.id.change_password)
      return R.id.profile;
    return R.id.home;
  }
}
