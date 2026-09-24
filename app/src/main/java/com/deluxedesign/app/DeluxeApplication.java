package com.deluxedesign.app;

import android.app.Application;
import com.deluxedesign.app.data.firebase.FirebaseRepositoryProvider;
import com.deluxedesign.app.data.local.DemoRepositoryProvider;
import com.deluxedesign.app.repository.RepositoryProvider;

public class DeluxeApplication extends Application {
  private RepositoryProvider repositories;

  @Override
  public void onCreate() {
    super.onCreate();
    repositories =
        BuildConfig.FIREBASE_ENABLED
            ? new FirebaseRepositoryProvider(this)
            : new DemoRepositoryProvider(this);
  }

  public RepositoryProvider repositories() {
    return repositories;
  }
}
