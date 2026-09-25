package com.deluxedesign.app.ui.profile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/** Offline map preview shown while the OpenStreetMap tile view loads or has no connection. */
public class BranchMapPreviewView extends View {
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  public BranchMapPreviewView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    paint.setStrokeCap(Paint.Cap.ROUND);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    float w = getWidth(), h = getHeight();
    canvas.drawColor(Color.rgb(28, 32, 38));

    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(18f);
    paint.setColor(Color.rgb(42, 47, 55));
    for (int i = -1; i < 6; i++) {
      float x = i * w / 4f;
      canvas.drawLine(x, 0, x + w * .28f, h, paint);
    }
    for (int i = 1; i < 5; i++) {
      float y = i * h / 5f;
      canvas.drawLine(0, y, w, y - h * .14f, paint);
    }

    paint.setStrokeWidth(4f);
    paint.setColor(Color.rgb(104, 111, 122));
    Path mainRoad = new Path();
    mainRoad.moveTo(-20, h * .76f);
    mainRoad.cubicTo(w * .22f, h * .52f, w * .55f, h * .62f, w + 20, h * .22f);
    canvas.drawPath(mainRoad, paint);

    paint.setStrokeWidth(7f);
    paint.setColor(Color.rgb(47, 91, 111));
    Path river = new Path();
    river.moveTo(w * .72f, -10);
    river.cubicTo(w * .60f, h * .24f, w * .88f, h * .58f, w * .74f, h + 10);
    canvas.drawPath(river, paint);

    drawPin(canvas, w * .39f, h * .47f);
    drawPin(canvas, w * .66f, h * .66f);

    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.WHITE);
    paint.setTextSize(22f);
    paint.setFakeBoldText(true);
    canvas.drawText("La Paz", 18f, 30f, paint);
    paint.setFakeBoldText(false);
    paint.setColor(Color.rgb(184, 190, 199));
    paint.setTextSize(15f);
    canvas.drawText("Deluxe Central", w * .39f + 16f, h * .47f - 5f, paint);
  }

  private void drawPin(Canvas canvas, float x, float y) {
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.rgb(255, 49, 58));
    canvas.drawCircle(x, y, 11f, paint);
    paint.setColor(Color.WHITE);
    canvas.drawCircle(x, y, 4f, paint);
  }
}
