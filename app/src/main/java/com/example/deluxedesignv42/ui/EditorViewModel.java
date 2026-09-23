package com.example.deluxedesignv42.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.deluxedesignv42.R;
import com.example.deluxedesignv42.data.DemoRepository;
import com.example.deluxedesignv42.data.VehicleVariantRepository;
import com.example.deluxedesignv42.model.DesignConfiguration;
import com.example.deluxedesignv42.model.Vehicle;

import java.util.Stack;

public class EditorViewModel extends ViewModel {
    private final MutableLiveData<DesignConfiguration> currentConfig = new MutableLiveData<>();
    private final MutableLiveData<Integer> previewImageResId = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();

    private final Stack<Memento> undoStack = new Stack<>();
    private final Stack<Memento> redoStack = new Stack<>();
    
    private String vehicleId;
    private String currentAngle = "Frente";
    private String currentPaintColor = "Rojo";
    private String currentFinishType = "Sólido";

    private static class Memento {
        final String angle;
        final String color;
        final String finish;

        Memento(String angle, String color, String finish) {
            this.angle = angle;
            this.color = color;
            this.finish = finish;
        }
    }

    public void initialize(String vehicleId) {
        if (this.vehicleId != null) return; // Ya inicializado
        this.vehicleId = vehicleId;
        
        Vehicle vehicle = null;
        for (Vehicle v : DemoRepository.getInstance().getCatalog()) {
            if (v.getId().equals(vehicleId)) {
                vehicle = v;
                break;
            }
        }

        if (vehicle != null) {
            DesignConfiguration config = new DesignConfiguration(vehicle);
            config.setAngle(currentAngle);
            config.setPaintColor(currentPaintColor);
            config.setFinishType(currentFinishType);
            currentConfig.setValue(config);
            updatePreview();
        }
    }

    public LiveData<DesignConfiguration> getCurrentConfig() { return currentConfig; }
    public LiveData<Integer> getPreviewImageResId() { return previewImageResId; }
    public LiveData<String> getStatusMessage() { return statusMessage; }

    public String getCurrentAngle() { return currentAngle; }
    public String getCurrentPaintColor() { return currentPaintColor; }
    public String getCurrentFinishType() { return currentFinishType; }

    public void updateAngle(String angle) {
        saveStateToUndo();
        this.currentAngle = angle;
        applyChanges();
    }

    public void updatePaint(String color, String finish) {
        saveStateToUndo();
        this.currentPaintColor = color;
        this.currentFinishType = finish;
        applyChanges();
    }

    private void saveStateToUndo() {
        undoStack.push(new Memento(currentAngle, currentPaintColor, currentFinishType));
        redoStack.clear(); // Nuevo cambio borra historial de rehacer
    }

    private void applyChanges() {
        DesignConfiguration config = currentConfig.getValue();
        if (config != null) {
            config.setAngle(currentAngle);
            config.setPaintColor(currentPaintColor);
            config.setFinishType(currentFinishType);
            currentConfig.setValue(config);
            updatePreview();
        }
    }

    private void updatePreview() {
        int resId = VehicleVariantRepository.getInstance().getVariantImage(vehicleId, currentAngle, currentPaintColor, currentFinishType);
        if (resId != -1) {
            previewImageResId.setValue(resId);
            statusMessage.setValue("Configuración: " + currentPaintColor + " (" + currentFinishType + ") en ángulo " + currentAngle);
        } else {
            previewImageResId.setValue(R.drawable.img_placeholder);
            statusMessage.setValue("Vista previa no disponible para esta combinación (DEMO)");
        }
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public void undo() {
        if (canUndo()) {
            redoStack.push(new Memento(currentAngle, currentPaintColor, currentFinishType));
            Memento memento = undoStack.pop();
            this.currentAngle = memento.angle;
            this.currentPaintColor = memento.color;
            this.currentFinishType = memento.finish;
            applyChanges();
        }
    }

    public void redo() {
        if (canRedo()) {
            undoStack.push(new Memento(currentAngle, currentPaintColor, currentFinishType));
            Memento memento = redoStack.pop();
            this.currentAngle = memento.angle;
            this.currentPaintColor = memento.color;
            this.currentFinishType = memento.finish;
            applyChanges();
        }
    }
}