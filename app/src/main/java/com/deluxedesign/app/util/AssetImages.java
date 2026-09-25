package com.deluxedesign.app.util;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;

/**
 * Resolves committed full images. Missing assets are explicitly marked, never disguised as finished
 * photography.
 */
public final class AssetImages {
  public static void show(ImageView view, String resource) {
    int id =
        view.getResources().getIdentifier(resource, "drawable", view.getContext().getPackageName());
    if (id != 0) view.setImageResource(id);
    else view.setImageDrawable(new PendingImage(resource));
    view.setContentDescription(resource.replace('_', ' ') + (id == 0 ? " · imagen pendiente" : ""));
  }

  public static boolean available(android.content.Context context, String resource) {
    return context.getResources().getIdentifier(resource, "drawable", context.getPackageName())
        != 0;
  }

  private static class PendingImage extends Drawable {
    private final String name;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    PendingImage(String name) {
      this.name = name;
    }

    @Override
    public void draw(@NonNull Canvas original) {
      Canvas c = original;
      int save = c.save();
      c.translate(getBounds().left, getBounds().top);
      c.scale(getBounds().width() / 640f, getBounds().height() / 400f);
      paint.setShader(
          new LinearGradient(0, 0, 640, 400, 0xFF23111B, 0xFF081322, Shader.TileMode.CLAMP));
      c.drawRoundRect(0, 0, 640, 400, 24, 24, paint);
      paint.setShader(null);
      int color =
          name.contains("street_blue")
              ? 0xFF178AE8
              : name.contains("urban_dark") ? 0xFF545966 : 0xFFED3146;
      paint.setColor(color);
      c.drawRoundRect(95, 176, 545, 288, 36, 36, paint);
      Path roof = new Path();
      roof.moveTo(190, 180);
      roof.lineTo(245, 113);
      roof.lineTo(397, 113);
      roof.lineTo(461, 180);
      roof.close();
      c.drawPath(roof, paint);
      paint.setColor(0xFF101925);
      c.drawRoundRect(244, 127, 390, 178, 12, 12, paint);
      paint.setColor(0xFF020406);
      c.drawCircle(195, 281, 41, paint);
      c.drawCircle(453, 281, 41, paint);
      paint.setColor(0xFFB7BBC8);
      c.drawCircle(195, 281, 21, paint);
      c.drawCircle(453, 281, 21, paint);
      paint.setColor(Color.WHITE);
      paint.setTextAlign(Paint.Align.CENTER);
      paint.setTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
      paint.setTextSize(22);
      c.drawText("RECURSO VISUAL PENDIENTE", 320, 53, paint);
      paint.setTextSize(17);
      paint.setTypeface(Typeface.DEFAULT);
      c.drawText(name.replace("vehicle_", "").replace('_', ' '), 320, 360, paint);
      c.restoreToCount(save);
    }

    @Override
    public void setAlpha(int alpha) {
      paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(android.graphics.ColorFilter filter) {
      paint.setColorFilter(filter);
    }

    @Override
    public int getOpacity() {
      return PixelFormat.OPAQUE;
    }
  }
}
