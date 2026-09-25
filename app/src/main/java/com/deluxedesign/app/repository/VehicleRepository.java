package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;
import java.util.List;

public interface VehicleRepository {
  LiveData<List<Vehicle>> vehicles();

  LiveData<List<CustomizationPreset>> presets();

  LiveData<List<Favorite>> favorites(String userId);

  LiveData<List<CustomizationOption>> options();

  void toggleFavorite(String userId, String vehicleId, Result<Void> result);
}
