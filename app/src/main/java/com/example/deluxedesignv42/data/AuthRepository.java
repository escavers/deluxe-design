package com.example.deluxedesignv42.data;

public interface AuthRepository {
    boolean loginDemo(String email, String password);
    boolean isDemoSessionActive();
    void logoutDemo();
}