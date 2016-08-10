package com.forcelain.awesomebrowser.presentation.common;

import android.webkit.WebView;

public interface WebActivity {
    TabNotifierManager getTabManager();
    void onPageLoaded(String tabId, String url, String title);
    WebView getWebView(String tabId);
    void offerWebView(WebView webView, String tabId);
}
