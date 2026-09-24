package com.deluxedesign.app.repository;

import androidx.lifecycle.LiveData;
import com.deluxedesign.app.domain.model.*;

public interface AuthRepository {
  LiveData<User> session();

  void signIn(String email, String password, Result<User> result);

  void register(String name, String email, String phone, String password, Result<User> result);

  void signOut();

  void updateProfile(String name, String phone, String avatar, Result<User> result);

  void requestPasswordReset(String email, Result<String> result);

  void resetPassword(String email, String token, String password, Result<Void> result);

  void changePassword(String current, String password, Result<Void> result);
}
