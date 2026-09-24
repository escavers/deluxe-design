package com.deluxedesign.app.ui.common;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import com.deluxedesign.app.R;

/** Displays the original supplied emblem in a circular viewport without modifying the file. */
public class BrandLogoView extends AppCompatImageView {
  private final Bitmap logo;
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

  public BrandLogoView(Context context, AttributeSet attributes) {
    super(context, attributes);
    logo = BitmapFactory.decodeResource(getResources(), R.drawable.brand_logo);
    setContentDescription("Logo DELUXE proporcionado por el usuario");
  }

  @Override
  protected void onDraw(Canvas canvas) {
    float size = Math.min(getWidth(), getHeight());
    float x = (getWidth() - size) / 2f, y = (getHeight() - size) / 2f;
    Path clip = new Path();
    clip.addCircle(x + size / 2, y + size / 2, size / 2, Path.Direction.CW);
    int save = canvas.save();
    canvas.clipPath(clip);
    Rect source =
        new Rect(
            Math.round(logo.getWidth() * .094f),
            Math.round(logo.getHeight() * .046f),
            Math.round(logo.getWidth() * .911f),
            Math.round(logo.getHeight() * .949f));
    canvas.drawBitmap(logo, source, new RectF(x, y, x + size, y + size), paint);
    canvas.restoreToCount(save);
  }
}
