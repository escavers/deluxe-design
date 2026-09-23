package com.example.deluxedesignv42.data;

public class DemoAuthRepository implements AuthRepository {
    private static DemoAuthRepository instance;
    private boolean isSessionActive = false;

    private DemoAuthRepository() {}

    public static synchronized DemoAuthRepository getInstance() {
        if (instance == null) {
            instance = new DemoAuthRepository();
        }
        return instance;
    }

    @Override
    public boolean loginDemo(String email, String password) {
        // Acceso libre para pruebas (Fase de desarrollo)
        isSessionActive = true;
        return true;
    }

    @Override
    public boolean isDemoSessionActive() {
        return isSessionActive;
    }

    @Override
    public void logoutDemo() {
        isSessionActive = false;
    }
}