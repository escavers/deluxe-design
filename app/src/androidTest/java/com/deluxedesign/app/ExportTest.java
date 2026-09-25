package com.deluxedesign.app;

import static org.junit.Assert.*;

import android.content.Context;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.deluxedesign.app.domain.model.Quote;
import com.deluxedesign.app.util.*;
import com.google.gson.Gson;
import java.io.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExportTest {
  @Test
  public void pdfPaginationMediaStoreAndPng() throws Exception {
    Context c = ApplicationProvider.getApplicationContext();
    Quote quote = new Quote();
    quote.id = "COT-TEST";
    quote.customerName = "Cliente de prueba";
    quote.createdAt = System.currentTimeMillis();
    quote.totalCents = 2390000;
    quote.itemsJson = new Gson().toJson(QuoteCalculator.items(quote.totalCents, null));
    quote.notes = "Porsche GT3 RS - Street Blue. Entrega y detalles por confirmar con el taller.";
    File pdf = ExportFiles.pdf(c, quote);
    assertTrue(pdf.length() > 1000);
    try (ParcelFileDescriptor descriptor =
            ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer renderer = new PdfRenderer(descriptor)) {
      assertEquals(1, renderer.getPageCount());
      try (PdfRenderer.Page page = renderer.openPage(0)) {
        Bitmap image = Bitmap.createBitmap(1190, 1684, Bitmap.Config.ARGB_8888);
        image.eraseColor(Color.WHITE);
        page.render(image, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        try (OutputStream out =
            new FileOutputStream(new File(c.getExternalFilesDir(null), "quote-preview.png"))) {
          image.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        image.recycle();
      }
    }
    Uri savedPdf = ExportFiles.savePdf(c, pdf);
    try (InputStream in = c.getContentResolver().openInputStream(savedPdf)) {
      assertNotNull(in);
      assertEquals('%', in.read());
    } finally {
      c.getContentResolver().delete(savedPdf, null, null);
    }
    StringBuilder notes = new StringBuilder();
    for (int i = 0; i < 100; i++)
      notes
          .append("Detalle especial número ")
          .append(i)
          .append(": conservar esta observación completa.\n");
    quote.notes = notes.toString();
    File longPdf = ExportFiles.pdf(c, quote);
    try (ParcelFileDescriptor descriptor =
            ParcelFileDescriptor.open(longPdf, ParcelFileDescriptor.MODE_READ_ONLY);
        PdfRenderer renderer = new PdfRenderer(descriptor)) {
      assertTrue("Long notes must paginate", renderer.getPageCount() > 1);
    }
    AtomicReference<Uri> png = new AtomicReference<>();
    AtomicReference<Exception> failure = new AtomicReference<>();
    InstrumentationRegistry.getInstrumentation()
        .runOnMainSync(
            () -> {
              try {
                ImageView view = new ImageView(c);
                AssetImages.show(view, "ci_porsche_front34");
                png.set(ExportFiles.saveImage(c, view, "DeluxeTest"));
              } catch (Exception e) {
                failure.set(e);
              }
            });
    assertNull(failure.get());
    try (InputStream in = c.getContentResolver().openInputStream(png.get())) {
      Bitmap image = BitmapFactory.decodeStream(in);
      assertNotNull(image);
      assertEquals(1200, image.getWidth());
      image.recycle();
    } finally {
      c.getContentResolver().delete(png.get(), null, null);
    }
  }
}
