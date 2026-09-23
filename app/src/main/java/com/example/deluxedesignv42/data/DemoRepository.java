package com.example.deluxedesignv42.data;

import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.model.Vehicle;
import com.example.deluxedesignv42.model.VehicleSpecification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoRepository {
    private static DemoRepository instance;
    private List<Vehicle> catalog;

    private DemoRepository() {
        catalog = new ArrayList<>();
        initDemoData();
    }

    public static synchronized DemoRepository getInstance() {
        if (instance == null) {
            instance = new DemoRepository();
        }
        return instance;
    }

    private void initDemoData() {
        VehicleSpecification sportsSpecs = new VehicleSpecification("V8 Bi-Turbo (DEMO)", "PDK 7 marchas", "525 hp", "Trasera (RWD)", "1450 kg");
        VehicleSpecification sedanSpecs = new VehicleSpecification("I6 Turbo (DEMO)", "Auto 8 marchas", "375 hp", "Integral (xDrive)", "1800 kg");
        VehicleSpecification truckSpecs = new VehicleSpecification("V6 Hybrid (DEMO)", "Auto 10 marchas", "437 hp", "4x4", "2400 kg");

        catalog.add(new Vehicle("v1", "Ford", "Ford Mustang GT 2024", 2024, "Deportivo",
                "El icono americano reinventado.",
                Arrays.asList(R.drawable.img_mustang_gt), sportsSpecs));

        catalog.add(new Vehicle("v2", "Porsche", "Porsche GT3 RS", 2023, "Deportivo",
                "Nacido en el circuito para la calle.",
                Arrays.asList(R.drawable.img_porsche_gt3rs), sportsSpecs));

        catalog.add(new Vehicle("v3", "Ferrari", "Ferrari La Ferrari", 2018, "Deportivo",
                "Excelencia híbrida italiana.",
                Arrays.asList(R.drawable.img_placeholder), sportsSpecs));

        catalog.add(new Vehicle("v4", "BMW", "BMW M8 Competition", 2024, "Deportivo",
                "Poder ejecutivo de alto rendimiento.",
                Arrays.asList(R.drawable.img_bmw_m4), sportsSpecs));

        catalog.add(new Vehicle("v5", "BMW", "BMW M4 Competition", 2024, "Deportivo",
                "Agilidad y agresividad M.",
                Arrays.asList(R.drawable.img_bmw_m4), sportsSpecs));

        catalog.add(new Vehicle("v6", "Toyota", "Toyota Tacoma", 2024, "PickUp",
                "Dominio total fuera del camino.",
                Arrays.asList(R.drawable.img_placeholder), truckSpecs));

        catalog.add(new Vehicle("v7", "Audi", "Audi R8", 2023, "Deportivo",
                "El legendario motor V10 atmosférico.",
                Arrays.asList(R.drawable.img_placeholder), sportsSpecs));

        catalog.add(new Vehicle("v8", "Volkswagen", "Golf GTI", 2024, "Hatchback",
                "El hot hatch original.",
                Arrays.asList(R.drawable.img_placeholder), sedanSpecs));
    }

    public List<Vehicle> getCatalog() {
        return new ArrayList<>(catalog);
    }
}