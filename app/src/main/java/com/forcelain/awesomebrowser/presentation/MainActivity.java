package com.forcelain.awesomebrowser.presentation;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.presentation.common.WebActivity;
import com.forcelain.awesomebrowser.presentation.common.WebViewCache;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabIdGenerator;
import com.forcelain.awesomebrowser.tabs.TabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements WebActivity, TabNotifierManager.TabListener {

    private static final int WEB_VIEW_COUNT = 10;
    private final WebViewCache webViewPool = new WebViewCache(WEB_VIEW_COUNT);
    private TabNotifierManager tabManager;
    private EditText urlView;
    private View closeTabView;
    private TabIdGenerator tabIdGenerator;
    private TabEditor tabEditor = new TabEditor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_page);
        initViews();

        tabManager = ((AwesomeBrowserApp) getApplication()).getTabManager();
        tabManager.addTabListener(this);

        tabIdGenerator = ((AwesomeBrowserApp) getApplication()).getTabIdGenerator();

        closeTabView.setEnabled(tabManager.count() > 1);

        TabModel tabModel = getDefault();
        getTabManager().addTab(tabModel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabManager.removeTabListener(this);
    }

    private void initViews(){
        urlView = (EditText) findViewById(R.id.url_edit_text);
        urlView.setOnEditorActionListener(new SoftKBListener());
        findViewById(R.id.add_tab_action).setOnClickListener(new AddTabAction());
        findViewById(R.id.go_url_action).setOnClickListener(new GoToUrlAction());
        closeTabView = findViewById(R.id.close_tab_action);
        closeTabView.setOnClickListener(new CloseTabAction());
    }

    @Override
    public TabNotifierManager getTabManager() {
        return tabManager;
    }

    private void updateCloseView(TabManager tabManager) {
        closeTabView.setEnabled(tabManager.count() > 1);
    }

    private void onPageSelected(TabModel tabModel) {

        if (tabModel != null) {
            replacePage(tabModel.getId());
        }

        updatePageData(tabModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_page_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_tab:
                showChooseTabDialog();
                return true;
            case R.id.clear_tabs:
                TabModel tabModel = getDefault();
                List<TabModel> list = new LinkedList<>();
                list.add(tabModel);
                tabManager.setTabs(list);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TabModel getDefault() {
        TabModel tabModel = new TabModel(tabIdGenerator.generateId());
        tabEditor.setUrl(tabModel, "http://ya.ru");
        return tabModel;
    }

    private void showChooseTabDialog() {
        String newTabTitle = getString(R.string.new_tab);
        final List<TabModel> tabModels = getTabManager().getAllTabs();
        CharSequence[] items = new CharSequence[tabModels.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = tabEditor.getTitle(tabModels.get(i));
            if (items[i] == null) {
                items[i] = newTabTitle;
            }
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TabModel tabModel = getTabManager().getTab(i);
                onPageSelected(tabModel);
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_tab)
                .setItems(items, listener)
                .show();
    }

    private void replacePage(String pageId) {

        WebPageFragment webPageFragment = WebPageFragment.create(pageId);
        FragmentManager fm = getSupportFragmentManager();
        fm
                .beginTransaction()
                .replace(R.id.content, webPageFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commitNow();
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
    }

    private int bound(int pos, int min, int max) {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }

    @Override
    public void onPageLoaded(String tabId, String url, String title) {
        TabModel tab = getTabManager().getTab(tabId);
        if (tab != null) {
            tabEditor.setUrl(tab, url);
            tabEditor.setTitle(tab, title);

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
            if (fragment instanceof WebPageFragment) {
                String currentTabId = ((WebPageFragment) fragment).getTabId();
                if (TextUtils.equals(currentTabId, tabId)) {
                    updatePageData(getTabManager().getTab(tabId));
                }
            }
        }
    }

    @Override
    public WebView getWebView(String tabId) {
        WebView webView = webViewPool.get(tabId);
        if (webView == null) {
            webView = new WebView(this);
        }
        return webView;
    }

    @Override
    public void offerWebView(WebView webView, String tabId) {
        TabModel tab = tabManager.getTab(tabId);
        if (tab != null) {
            webViewPool.put(tabId, webView);
        } else {
            webViewPool.remove(tabId);
        }
    }

    private void updatePageData(TabModel tab) {
        if (tab != null) {
            String url = tabEditor.getUrl(tab);
            urlView.setText(url);
            String title = tabEditor.getTitle(tab);
            if (TextUtils.isEmpty(title)) {
                title = getString(R.string.new_tab);
            }
            setAppBarTitle(title);
        } else {
            urlView.setText(null);
            setAppBarTitle(getString(R.string.app_name));
        }
    }

    private void setAppBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof WebPageFragment && ((WebPageFragment) currentFragment).handleBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabAdded(TabModel tabModel) {
        updateCloseView(getTabManager());
        updatePageData(tabModel);
        replacePage(tabModel.getId());
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof WebPageFragment) {
            ((WebPageFragment) currentFragment).goUrl(tabEditor.getUrl(tabModel));
        }
    }

    @Override
    public void onTabRemoved(TabModel tabModel, int position) {
        webViewPool.remove(tabModel.getId());
        TabNotifierManager tabManager = getTabManager();
        updateCloseView(tabManager);
        int nextTabPosition = bound(position, 0, tabManager.count() - 1);
        TabModel nextTab = tabManager.getTab(nextTabPosition);
        onPageSelected(nextTab);
    }

    @Override
    public void onTabSetChanged() {
        webViewPool.evictAll();
        TabNotifierManager tabManager = getTabManager();
        Fragment currentFragment = getCurrentFragment();
        String currentTabId = null;
        if (currentFragment instanceof WebPageFragment) {
            currentTabId = ((WebPageFragment) currentFragment).getTabId();
        }
        TabModel currentTab = getTabManager().getTab(currentTabId);
        if (currentTab != null) {
            onPageSelected(currentTab);
        } else if (tabManager.count() > 0) {
            onPageSelected(tabManager.getTab(0));
        } else {
            onPageSelected(null);
        }
    }

    private class AddTabAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TabModel tabModel = new TabModel(tabIdGenerator.generateId());
            getTabManager().addTab(tabModel);
        }
    }

    private class GoToUrlAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            View focusedView = getCurrentFocus();
            if (focusedView != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
            }

            Fragment fragment = getCurrentFragment();
            if (fragment instanceof WebPageFragment) {
                ((WebPageFragment) fragment).goUrl(urlView.getText().toString());
            }
        }
    }

    private class CloseTabAction implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof WebPageFragment) {
                TabManager tabManager = getTabManager();
                String tabId = ((WebPageFragment) currentFragment).getTabId();
                TabModel tab = tabManager.getTab(tabId);
                tabManager.removeTab(tab);
            }
        }
    }

    private class SoftKBListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            new GoToUrlAction().onClick(textView);
            return true;
        }
    }
}
