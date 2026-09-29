package com.deluxedesign.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Compone la vista del vehículo en dos capas desde assets: el fondo de estudio (fondo-estudio-
 * maestro.png) y el recorte del vehículo en el color elegido (vehicles/{slug}/{slug}-{color}.png).
 * Las imágenes se decodifican con escala reducida y se cachean en memoria.
 */
public final class VehicleImages {
  private VehicleImages() {}

  public static final String DEFAULT_COLOR = "paint_rojo";

  private static final Map<String, String> SLUGS = new LinkedHashMap<>();

  static {
    SLUGS.put("porsche", "porsche-911-gt3-rs");
    SLUGS.put("bmw", "bmw-m4-competition");
    SLUGS.put("mustang", "ford-mustang-gt");
    SLUGS.put("ferrari", "ferrari-296-gtb");
    SLUGS.put("huracan", "lamborghini-huracan");
    SLUGS.put("mclaren", "mclaren-750s");
    SLUGS.put("r8", "audi-r8-v10");
    SLUGS.put("corvette", "chevrolet-corvette-c8");
    SLUGS.put("gtr", "nissan-gtr-r35-transparent");
  }

  private static final Map<String, String> COLOR_FILES = new LinkedHashMap<>();

  static {
    COLOR_FILES.put("paint_rojo", "rojo");
    COLOR_FILES.put("paint_azul", "azul");
    COLOR_FILES.put("paint_blanco", "blanco");
    COLOR_FILES.put("paint_negro", "negro");
    COLOR_FILES.put("paint_plomo", "plomo");
  }

  private static final LruCache<String, Bitmap> CACHE =
      new LruCache<String, Bitmap>(24 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
          return value.getByteCount();
        }
      };

  public static String slug(String vehicleId) {
    String s = SLUGS.get(vehicleId);
    return s == null ? "porsche-911-gt3-rs" : s;
  }

  public static String colorFile(String colorId) {
    String s = COLOR_FILES.get(colorId);
    return s == null ? COLOR_FILES.get(DEFAULT_COLOR) : s;
  }

  private static Bitmap decode(Context context, String asset) {
    Bitmap bmp = CACHE.get(asset);
    if (bmp != null) return bmp;
    try {
      BitmapFactory.Options bounds = new BitmapFactory.Options();
      bounds.inJustDecodeBounds = true;
      BitmapFactory.decodeStream(context.getAssets().open(asset), null, bounds);
      int sample = 1;
      while (bounds.outWidth / sample > 1200) sample *= 2;
      BitmapFactory.Options opts = new BitmapFactory.Options();
      opts.inSampleSize = sample;
      bmp = BitmapFactory.decodeStream(context.getAssets().open(asset), null, opts);
      if (bmp != null) CACHE.put(asset, bmp);
      return bmp;
    } catch (java.io.IOException e) {
      return null;
    }
  }

  private static Bitmap fondo(Context context) {
    return decode(context, "vehicles/fondo-estudio-maestro.png");
  }

  /** Capa de fondo estático (área de estudio). */
  public static Bitmap background(Context context) {
    return fondo(context);
  }

  private static String cutoutPath(String vehicleId, String colorId) {
    String slug = slug(vehicleId);
    return "vehicles/" + slug + "/" + slug + "-" + colorFile(colorId) + ".png";
  }

  /** Recorte transparente del vehículo en el color dado. */
  public static Bitmap cutout(Context context, String vehicleId, String colorId) {
    return decode(context, cutoutPath(vehicleId, colorId));
  }

  /** Recorte transparente del vehículo en el color por defecto. */
  public static Bitmap cutout(Context context, String vehicleId) {
    return cutout(context, vehicleId, DEFAULT_COLOR);
  }

  public static Drawable drawable(Context context, String vehicleId, String colorId) {
    return new LayerDrawable(context, vehicleId, colorId);
  }

  public static void show(ImageView view, String vehicleId, @Nullable String colorId) {
    view.setImageDrawable(
        drawable(view.getContext(), vehicleId, colorId == null ? DEFAULT_COLOR : colorId));
  }

  /** Cambia el color suavemente (fade in) sobre la vista actual. */
  public static void fade(ImageView view, String vehicleId, String colorId) {
    view.setImageDrawable(drawable(view.getContext(), vehicleId, colorId));
    view.setAlpha(0f);
    view.animate().alpha(1f).setDuration(320).start();
  }

  private static final class LayerDrawable extends Drawable {
    private final Bitmap back;
    private final Bitmap front;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Matrix matrix = new Matrix();

    LayerDrawable(Context context, String vehicleId, String colorId) {
      back = fondo(context);
      front = cutout(context, vehicleId, colorId);
    }

    @Override
    public void draw(Canvas canvas) {
      int W = getBounds().width();
      int H = getBounds().height();
      if (back == null || front == null || W == 0 || H == 0) return;
      float s = Math.max(W / (float) back.getWidth(), H / (float) back.getHeight());
      float dx = (W - back.getWidth() * s) / 2f;
      float dy = (H - back.getHeight() * s) / 2f;
      matrix.setScale(s, s);
      matrix.postTranslate(dx, dy);
      canvas.drawBitmap(back, matrix, paint);
      canvas.drawBitmap(front, matrix, paint);
    }

    @Override
    public void setAlpha(int alpha) {
      paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
      paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
      return PixelFormat.OPAQUE;
    }
  }
}