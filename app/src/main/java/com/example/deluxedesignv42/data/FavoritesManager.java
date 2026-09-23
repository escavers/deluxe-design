package com.example.deluxedesignv42.data;

import java.util.HashSet;
import java.util.Set;

public class FavoritesManager {
    private static FavoritesManager instance;
    private Set<String> favoriteVehicleIds;

    private FavoritesManager() {
        favoriteVehicleIds = new HashSet<>();
    }

    public static synchronized FavoritesManager getInstance() {
        if (instance == null) {
            instance = new FavoritesManager();
        }
        return instance;
    }

    public boolean isFavorite(String id) {
        return favoriteVehicleIds.contains(id);
    }

    public void toggleFavorite(String id) {
        if (favoriteVehicleIds.contains(id)) {
            favoriteVehicleIds.remove(id);
        } else {
            favoriteVehicleIds.add(id);
        }
    }
}