package com.example.deluxedesignv42.data;

import com.example.deluxedesignv42.R;
import java.util.HashMap;
import java.util.Map;

public class VehicleVariantRepository {
    private static VehicleVariantRepository instance;
    private final Map<String, Integer> variantImages;

    private VehicleVariantRepository() {
        variantImages = new HashMap<>();
        initVariantData();
    }

    public static synchronized VehicleVariantRepository getInstance() {
        if (instance == null) {
            instance = new VehicleVariantRepository();
        }
        return instance;
    }

    private void initVariantData() {
        // Variante base del Porsche GT3 RS (v1)
        variantImages.put("v1_Frente_Rojo_Sólido", R.drawable.img_porsche_gt3rs);
        // Otras variantes completas disponibles de ejemplo DEMO con imágenes existentes genéricas
        variantImages.put("v1_Frente_Azul_Mate", R.drawable.img_bmw_m4);
        variantImages.put("v1_Frente_Amarillo_Metálico", R.drawable.img_mustang_gt);
    }

    /**
     * Devuelve el recurso drawable de la combinación o -1 si no está disponible.
     */
    public int getVariantImage(String vehicleId, String angle, String color, String finish) {
        String key = vehicleId + "_" + angle + "_" + color + "_" + finish;
        if (variantImages.containsKey(key)) {
            return variantImages.get(key);
        }
        return -1; // No disponible
    }
}