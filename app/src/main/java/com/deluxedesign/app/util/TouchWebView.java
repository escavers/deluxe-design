package com.deluxedesign.app.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * WebView que pide al padre no interceptar el gesto: los arrastres sobre el viewer 3D van al
 * modelos (orbitar/paneo/zoom) en lugar de hacer scroll de la página.
 */
public final class TouchWebView extends WebView {
  public TouchWebView(Context context) {
    super(context);
  }

  public TouchWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    getParent().requestDisallowInterceptTouchEvent(true);
    return super.onTouchEvent(event);
  }
}