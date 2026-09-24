package com.deluxedesign.app.repository;

public interface Result<T> {
  void success(T value);

  void error(String message);
}
