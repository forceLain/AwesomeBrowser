package com.forcelain.awesomebrowser.presentation.common;

import android.util.LruCache;
import android.webkit.WebView;

public class WebViewCache extends LruCache<String, WebView> {
    public WebViewCache(int maxSize) {
        super(maxSize);
    }
}
