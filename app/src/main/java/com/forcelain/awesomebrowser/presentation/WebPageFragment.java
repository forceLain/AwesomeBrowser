package com.forcelain.awesomebrowser.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.TabManagerActivity;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabModel;

public class WebPageFragment extends Fragment {

    private static final String ARG_TAB_ID = "ARG_TAB_ID";
    private WebView webView;
    private View progressView;
    private TabManagerActivity tabManagerActivity;
    private TabEditor tabEditor = new TabEditor();

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
        tabManagerActivity = (TabManagerActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tabManagerActivity = null;
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
        webView = (WebView) view.findViewById(R.id.web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new AwesomeClient());
        if (savedInstanceState == null) {
            TabModel tab = tabManagerActivity.getTabManager().getTab(getTabId());
            if (tab != null) {
                webView.loadUrl(tabEditor.getUrl(tab));
            }
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        webView.destroy();
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
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
            if (tabManagerActivity != null) {
                tabManagerActivity.onPageLoaded(getTabId(), url, webView.getTitle());
            }
        }
    }
}
