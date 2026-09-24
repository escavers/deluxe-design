package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;

public interface RepositoryProvider {
  AuthRepository auth();

  VehicleRepository vehicleRepository();

  ProjectRepository projects();

  QuoteRepository quotes();

  NotificationRepository notifications();

  BranchRepository branchRepository();

  LiveData<Boolean> ready();

  LiveData<String> issue();

  boolean cloud();
}
