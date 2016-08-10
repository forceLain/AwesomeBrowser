package com.forcelain.awesomebrowser.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.WebActivity;
import com.forcelain.awesomebrowser.tabs.TabEditor;

public class WebPageFragment extends Fragment {

    private static final String ARG_TAB_ID = "ARG_TAB_ID";
    private View progressView;
    private WebActivity webActivity;
    private TabEditor tabEditor = new TabEditor();
    private ViewGroup webViewContainer;
    private WebView webView;
    private String pendingUrl;

    public static WebPageFragment create(String id) {
        WebPageFragment fragment = new WebPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TAB_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        webActivity = (WebActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        webActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_web_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressView = view.findViewById(R.id.progress_view);
        progressView.setVisibility(View.GONE);
        webViewContainer = (ViewGroup) view.findViewById(R.id.web_view_container);
        webView = webActivity.getWebView(getTabId());

        webViewContainer.addView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new AwesomeClient());
        if (pendingUrl != null) {
            goUrl(pendingUrl);
            pendingUrl = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webViewContainer.removeAllViews();
        webActivity.offerWebView(webView, getTabId());
        webView = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        webView.onPause();
    }

    public boolean handleBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    public String getTabId() {
        return getArguments().getString(ARG_TAB_ID);
    }

    public void goUrl(String url) {
        if (webView != null && url != null) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            webView.loadUrl(url);
        } else {
            pendingUrl = url;
        }
    }

    private class AwesomeClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressView.setVisibility(View.GONE);
            if (webActivity != null) {
                webActivity.onPageLoaded(getTabId(), url, webView.getTitle());
            }
        }
    }
}
