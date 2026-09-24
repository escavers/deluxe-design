package com.deluxedesign.app.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public final class RemoteImage {
  private static final ExecutorService POOL = Executors.newFixedThreadPool(2);

  public static void load(ImageView view, String url) {
    if (url.equals(view.getTag())) return;
    view.setTag(url);
    POOL.execute(
        () -> {
          HttpURLConnection connection = null;
          try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            try (java.io.InputStream in = connection.getInputStream()) {
              BitmapFactory.Options options = new BitmapFactory.Options();
              options.inSampleSize = 2;
              Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
              view.post(
                  () -> {
                    if (url.equals(view.getTag()) && bitmap != null) view.setImageBitmap(bitmap);
                  });
            }
          } catch (Exception ignored) {
          } finally {
            if (connection != null) connection.disconnect();
          }
        });
  }
}
