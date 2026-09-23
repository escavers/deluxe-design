package com.example.deluxedesignv42;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.deluxedesignv42.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuración de vista mediante View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener NavHostFragment y NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            
            // Vincular BottomNavigationView con el NavController
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            
            // Listener para controlar la visibilidad de la barra inferior según el destino
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.homeFragment || id == R.id.catalogFragment || 
                    id == R.id.projectsFragment || id == R.id.quotesFragment || 
                    id == R.id.profileFragment) {
                    binding.bottomNavigation.setVisibility(View.VISIBLE);
                } else {
                    // Ocultar barra inferior en otras pantallas (ej. login, registro, detalles)
                    binding.bottomNavigation.setVisibility(View.GONE);
                }
            });
        }
    }
}