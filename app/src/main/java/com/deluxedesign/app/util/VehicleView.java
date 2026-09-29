package com.deluxedesign.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Vista de dos capas para el vehículo: el fondo del estudio siempre visible y estático, y el
 * recorte del vehículo que entra en crossfade al cambiar de color (solo el auto transiciona).
 * La altura se deriva del aspecto 1690:931 para que coincida con el marco del fondo.
 */
public final class VehicleView extends View {
  private Bitmap back;
  private Bitmap front;
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
  private final Matrix matrix = new Matrix();
  private float frontAlpha = 1f;
  private android.animation.ValueAnimator animator;
  private String vehicleId = "";
  private String colorId = "";

  public VehicleView(Context context) {
    super(context);
  }

  public VehicleView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /** Muestra el vehículo con el color dado; si el color cambia, solo el auto entra en fade. */
  public void setColor(String newVehicleId, String newColorId) {
    if (newVehicleId == null) newVehicleId = VehicleImages.DEFAULT_COLOR;
    if (newColorId == null) newColorId = VehicleImages.DEFAULT_COLOR;
    boolean colorChanged = !vehicleId.equals(newVehicleId) || !colorId.equals(newColorId);
    vehicleId = newVehicleId;
    colorId = newColorId;
    back = VehicleImages.background(getContext());
    front = VehicleImages.cutout(getContext(), vehicleId, colorId);
    if (colorChanged) crossfade(); else frontAlpha = 1f;
    requestLayout();
    invalidate();
  }

  private void crossfade() {
    if (animator != null) animator.cancel();
    frontAlpha = 0f;
    animator = android.animation.ValueAnimator.ofFloat(0f, 1f);
    animator.setDuration(320);
    animator.setInterpolator(new DecelerateInterpolator());
    animator.addUpdateListener(
        a -> {
          frontAlpha = (Float) a.getAnimatedValue();
          invalidate();
        });
    animator.start();
  }

  @Override
  protected void onMeasure(int widthSpec, int heightSpec) {
    int width = resolveSize(getSuggestedMinimumWidth(), widthSpec);
    float ratio = 931f / 1690f;
    if (back != null) ratio = back.getHeight() / (float) back.getWidth();
    int height = Math.round(width * ratio);
    if (height == 0) height = Math.round(width * 931f / 1690f);
    setMeasuredDimension(width, height);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    int W = getWidth();
    int H = getHeight();
    if (back == null || front == null || W == 0 || H == 0) return;
    float s = Math.min(W / (float) back.getWidth(), H / (float) back.getHeight());
    float dx = (W - back.getWidth() * s) / 2f;
    float dy = (H - back.getHeight() * s) / 2f;
    matrix.setScale(s, s);
    matrix.postTranslate(dx, dy);
    paint.setAlpha(255);
    canvas.drawBitmap(back, matrix, paint);
    paint.setAlpha((int) (frontAlpha * 255f));
    canvas.drawBitmap(front, matrix, paint);
    paint.setAlpha(255);
  }

  @Override
  protected void onDetachedFromWindow() {
    if (animator != null) animator.cancel();
    super.onDetachedFromWindow();
  }
}