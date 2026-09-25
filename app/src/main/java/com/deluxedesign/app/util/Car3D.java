package com.deluxedesign.app.util;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.webkit.WebViewAssetLoader;
import com.deluxedesign.app.BuildConfig;
import java.util.ArrayList;
import java.util.List;

/**
 * Embeds the local GLB viewer (viewer.html + model-viewer.min.js) inside a WebView using a
 * WebViewAssetLoader. The loader must delegate shouldInterceptRequest, otherwise the WebView
 * resolves the virtual host externally and fails with ERR_NAME_NOT_RESOLVED.
 */
public final class Car3D {
  private final WebView webView;
  private final List<String> pending = new ArrayList<>();

  public Car3D(WebView webView, Context context, String modelFile) {
    this.webView = webView;
    WebViewAssetLoader loader =
        new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
            .build();
    webView.setBackgroundColor(Color.rgb(12, 14, 20));
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setAllowFileAccess(false);
    settings.setAllowContentAccess(false);
    settings.setMediaPlaybackRequiresUserGesture(false);
    webView.setFocusable(false);
    webView.setFocusableInTouchMode(false);
    if (BuildConfig.DEBUG) {
      WebView.setWebContentsDebuggingEnabled(true);
    }
    webView.setWebChromeClient(
        new WebChromeClient() {
          @Override
          public boolean onConsoleMessage(ConsoleMessage msg) {
            Log.d("Deluxe3D", "[console] " + msg.message() + " (" + msg.lineNumber() + ")");
            return true;
          }
        });
    webView.setWebViewClient(
        new WebViewClient() {
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
          }

          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
          }

          @Override
          public WebResourceResponse shouldInterceptRequest(
              WebView view, WebResourceRequest request) {
            return loader.shouldInterceptRequest(request.getUrl());
          }

          @Override
          public void onReceivedError(
              WebView view,
              android.webkit.WebResourceRequest request,
              android.webkit.WebResourceError error) {
            Log.e("Deluxe3D", "[error] " + request.getUrl() + " -> " + error.getDescription());
          }

          @Override
          public void onPageFinished(WebView view, String url) {
            Log.d("Deluxe3D", "[page] " + url);
            for (String command : pending) view.evaluateJavascript(command, null);
            pending.clear();
            dumpState(view, 0);
            view.postDelayed(() -> dumpState(view, 1), 2500);
          }
        });
    String model = modelFile == null ? "vehicle_porsche.glb" : modelFile;
    webView.loadUrl(
        "https://appassets.androidplatform.net/assets/3d/viewer.html?model="
            + Uri.encode(model));
  }

  public void js(String command) {
    if (webView.getProgress() >= 100) webView.evaluateJavascript(command, null);
    else pending.add(command);
  }

  private void dumpState(WebView view, int round) {
    String script =
        "(function(){"
            + "var mv=document.getElementById('mv');"
            + "return JSON.stringify({"
            + "custom:(typeof customElements!=='undefined')&&customElements.get('model-viewer')?'ok':'missing',"
            + "els:document.querySelectorAll('model-viewer').length,"
            + "src:mv?(mv.getAttribute('src')||''):'',"
            + "loaded:!!mv&&!!mv.loaded,"
            + "visible:!!mv&&!!mv.modelIsVisible,"
            + "title:document.title"
            + "});})()";
    view.evaluateJavascript(
        script,
        value ->
            Log.d(
                "Deluxe3D",
                "[state" + (round == 0 ? "0" : "1") + "] " + String.valueOf(value)));
  }

  public void destroy() {
    ViewGroup parent = (ViewGroup) webView.getParent();
    if (parent != null) parent.removeView(webView);
    webView.removeAllViews();
    webView.destroy();
  }
}