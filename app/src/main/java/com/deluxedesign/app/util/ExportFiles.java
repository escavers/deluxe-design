package com.deluxedesign.app.util;

import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.core.content.FileProvider;
import com.deluxedesign.app.domain.model.*;
import com.google.gson.Gson;
import java.io.*;

public final class ExportFiles {
  private ExportFiles() {}

  public static void shareText(Context c, String text) {
    Intent i =
        new Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, "DELUXE DESIGN\n" + text);
    c.startActivity(Intent.createChooser(i, "Compartir proyecto"));
  }

  public static File pdf(Context c, Quote quote) throws IOException {
    File dir = new File(c.getCacheDir(), "exports");
    if (!dir.exists() && !dir.mkdirs())
      throw new IOException("No se pudo crear la carpeta de exportación.");
    File file = new File(dir, quote.id.replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf");
    PdfDocument pdf = new PdfDocument();
    try {
      PdfLayout layout = new PdfLayout(pdf, quote.id);
      layout.text("Cliente: " + quote.customerName);
      layout.text("Fecha: " + Formatters.date(quote.createdAt) + "   Estado: " + quote.status);
      layout.y += 24;
      QuoteItem[] items = new Gson().fromJson(quote.itemsJson, QuoteItem[].class);
      for (QuoteItem item : items) {
        layout.text(item.label + "  -  " + Formatters.money(item.amountCents));
        layout.y += 12;
      }
      layout.y += 16;
      layout.paint.setTypeface(Typeface.DEFAULT_BOLD);
      layout.paint.setTextSize(20);
      layout.paint.setColor(Color.rgb(200, 20, 50));
      layout.text("TOTAL: " + Formatters.money(quote.totalCents));
      layout.y += 25;
      layout.bodyStyle();
      layout.text("Notas: " + quote.notes);
      layout.finish();
      try (OutputStream stream = new FileOutputStream(file)) {
        pdf.writeTo(stream);
      }
    } finally {
      pdf.close();
    }
    return file;
  }

  /** Paginates long notes instead of silently truncating a customer's request. */
  private static final class PdfLayout {
    final PdfDocument document;
    final String quoteId;
    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    PdfDocument.Page page;
    Canvas canvas;
    int y, number;

    PdfLayout(PdfDocument document, String quoteId) {
      this.document = document;
      this.quoteId = quoteId;
      start();
    }

    void bodyStyle() {
      paint.setTypeface(Typeface.DEFAULT);
      paint.setTextSize(12);
      paint.setColor(Color.DKGRAY);
    }

    void start() {
      page = document.startPage(new PdfDocument.PageInfo.Builder(595, 842, ++number).create());
      canvas = page.getCanvas();
      paint.setColor(Color.rgb(12, 15, 25));
      canvas.drawRect(0, 0, 595, 135, paint);
      paint.setColor(Color.WHITE);
      paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setTextSize(28);
      canvas.drawText("DELUXE DESIGN", 40, 65, paint);
      paint.setTextSize(14);
      canvas.drawText("COTIZACIÓN · " + quoteId, 40, 103, paint);
      bodyStyle();
      y = 177;
    }

    void text(String value) {
      for (String paragraph : value.split("\n", -1)) {
        String remaining = paragraph;
        do {
          int count = paint.breakText(remaining, true, 510, null);
          if (count < remaining.length() && count > 0) {
            int space = remaining.lastIndexOf(' ', count - 1);
            if (space > 0) count = space;
          }
          if (count == 0 && !remaining.isEmpty()) count = 1;
          int height = (int) paint.getTextSize() + 7;
          if (y + height > 750) {
            Paint saved = new Paint(paint);
            finish();
            start();
            paint.set(saved);
          }
          canvas.drawText(remaining.substring(0, count), 40, y, paint);
          y += height;
          remaining = remaining.substring(count).trim();
        } while (!remaining.isEmpty());
      }
    }

    void finish() {
      bodyStyle();
      paint.setTextSize(10);
      canvas.drawText("Demostración académica · valores referenciales en USD.", 40, 785, paint);
      canvas.drawText("Tu vehículo, tu estilo, sin límites.  ·  Página " + number, 40, 805, paint);
      document.finishPage(page);
    }
  }

  public static void sharePdf(Context c, File file) {
    Uri uri = FileProvider.getUriForFile(c, c.getPackageName() + ".files", file);
    Intent i =
        new Intent(Intent.ACTION_SEND)
            .setType("application/pdf")
            .putExtra(Intent.EXTRA_STREAM, uri)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    i.setClipData(ClipData.newRawUri("Cotización", uri));
    c.startActivity(Intent.createChooser(i, "Compartir cotización"));
  }

  public static Uri savePdf(Context c, File file) throws IOException {
    ContentValues values = new ContentValues();
    values.put(MediaStore.Downloads.DISPLAY_NAME, file.getName());
    values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
    values.put(
        MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/DeluxeDesign");
    values.put(MediaStore.Downloads.IS_PENDING, 1);
    Uri uri = c.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
    if (uri == null) throw new IOException("Almacenamiento no disponible.");
    try (InputStream in = new FileInputStream(file);
        OutputStream out = c.getContentResolver().openOutputStream(uri)) {
      if (out == null) throw new IOException("No se pudo abrir el destino.");
      byte[] buffer = new byte[8192];
      int n;
      while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
    } catch (Exception e) {
      c.getContentResolver().delete(uri, null, null);
      throw new IOException(e);
    }
    values.clear();
    values.put(MediaStore.Downloads.IS_PENDING, 0);
    c.getContentResolver().update(uri, values, null, null);
    return uri;
  }

  public static Uri saveImage(Context c, ImageView image, String name) throws IOException {
    if (image.getDrawable() == null) throw new IOException("No hay imagen para exportar.");
    Bitmap bitmap = Bitmap.createBitmap(1200, 800, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    android.graphics.Rect old = new android.graphics.Rect(image.getDrawable().getBounds());
    image.getDrawable().setBounds(0, 0, 1200, 800);
    image.getDrawable().draw(canvas);
    image.getDrawable().setBounds(old);
    return saveBitmap(c, bitmap, name);
  }

  public static Uri saveImage(Context c, View view, String name) throws IOException {
    int width = Math.max(360, view.getWidth());
    int height = Math.max(240, view.getHeight());
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    view.draw(new Canvas(bitmap));
    return saveBitmap(c, bitmap, name);
  }

  private static Uri saveBitmap(Context c, Bitmap bitmap, String name) throws IOException {
    ContentValues values = new ContentValues();
    values.put(MediaStore.Images.Media.DISPLAY_NAME, name + ".png");
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
    values.put(
        MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DeluxeDesign");
    values.put(MediaStore.Images.Media.IS_PENDING, 1);
    Uri uri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    if (uri == null) {
      bitmap.recycle();
      throw new IOException("Almacenamiento no disponible.");
    }
    try (OutputStream stream = c.getContentResolver().openOutputStream(uri)) {
      if (stream == null || !bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
        throw new IOException("Error al escribir la imagen.");
    } catch (Exception e) {
      c.getContentResolver().delete(uri, null, null);
      throw new IOException(e);
    } finally {
      bitmap.recycle();
    }
    values.clear();
    values.put(MediaStore.Images.Media.IS_PENDING, 0);
    c.getContentResolver().update(uri, values, null, null);
    return uri;
  }
}
