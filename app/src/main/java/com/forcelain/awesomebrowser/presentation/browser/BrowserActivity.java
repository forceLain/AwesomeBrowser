package com.forcelain.awesomebrowser.presentation.browser;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.AwesomeBrowserApp;
import com.forcelain.awesomebrowser.presentation.WebPageFragment;
import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.presentation.common.WebActivity;
import com.forcelain.awesomebrowser.presentation.common.WebViewCache;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabIdGenerator;
import com.forcelain.awesomebrowser.tabs.TabModel;

import java.util.LinkedList;
import java.util.List;

public class BrowserActivity extends AppCompatActivity implements WebActivity {

    private static final int WEB_VIEW_COUNT = 10;
    private final WebViewCache webViewPool = new WebViewCache(WEB_VIEW_COUNT);
    private TabNotifierManager tabManager;
    private TabEditor tabEditor = new TabEditor();
    private TabManagerListener tabManagerListener;
    private BrowserView browserView;
    private TabIdGenerator tabIdGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        tabManager = ((AwesomeBrowserApp) getApplication()).getTabManager();
        tabManagerListener = new TabManagerListener();
        tabManager.addTabListener(tabManagerListener);
        tabIdGenerator = ((AwesomeBrowserApp) getApplication()).getTabIdGenerator();

        if (getResources().getBoolean(R.bool.isTablet)) {
            browserView = new TabletBrowserView(this, tabManager);
        } else {
            browserView = new PhoneBrowserView(this, tabManager);
        }

        browserView.setOnOpenUrlListener(new BrowserView.OnOpenUrlListener() {
            @Override
            public void onOpenUrl(String url) {
                hideSoftKeyboard();
                Fragment currentFragment = getCurrentFragment();
                if (currentFragment instanceof WebPageFragment) {
                    openUrlInCurrentTab(url);
                } else {
                    TabModel tab = createDefaultTab();
                    tabEditor.setUrl(tab, url);
                    tabManager.addTab(tab);
                }

                View current = getCurrentFocus();
                if (current != null) current.clearFocus();
            }
        });
        browserView.setOnAddTabListener(new BrowserView.OnAddTabListener() {
            @Override
            public void onAddNewTab() {
                TabModel tabModel = new TabModel(tabIdGenerator.generateId());
                tabEditor.setTitle(tabModel, getString(R.string.new_tab));
                tabManager.addTab(tabModel);
            }
        });
        browserView.setOnCloseTabListener(new BrowserView.OnCloseTabListener() {
            @Override
            public void onTabClosed(String tabId) {
                if (tabId == null) {
                    Fragment currentFragment = getCurrentFragment();
                    if (currentFragment instanceof WebPageFragment) {
                        tabId = ((WebPageFragment) currentFragment).getTabId();
                    }
                }
                TabModel tabModel = tabManager.getTab(tabId);
                tabManager.removeTab(tabModel);
            }
        });
        browserView.setOnChoseTabListener(new BrowserView.OnChoseTabListener() {
            @Override
            public void onTabChosen(String tabId) {
                TabModel tabModel = tabManager.getTab(tabId);
                openPage(tabModel);
            }
        });
        browserView.setOnClearListener(new BrowserView.OnClearListener() {
            @Override
            public void onTabCleared() {
                List<TabModel> cleanList = new LinkedList<>();
                cleanList.add(createDefaultTab());
                tabManager.setTabs(cleanList);
            }
        });

        if (savedInstanceState == null) {
            tabManager.addTab(createDefaultTab());
        } else {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof WebPageFragment) {
                String tabId = ((WebPageFragment) currentFragment).getTabId();
                TabModel tab = tabManager.getTab(tabId);
                if (tab != null) {
                    openUrlInCurrentTab(tabEditor.getUrl(tab));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabManager.removeTabListener(tabManagerListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return browserView.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = browserView.onOptionsItemSelected(item);
        return handled || super.onOptionsItemSelected(item);
    }

    private void hideSoftKeyboard() {
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

    }

    private TabModel createDefaultTab() {
        TabModel tabModel = new TabModel(tabIdGenerator.generateId());
        tabEditor.setUrl(tabModel, "http://ya.ru");
        tabEditor.setTitle(tabModel, getString(R.string.new_tab));
        return tabModel;
    }

    void replaceFragment(@Nullable TabModel tab) {

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (tab != null) {
            String pageId = tab.getId();

            WebPageFragment webPageFragment = WebPageFragment.create(pageId);
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content, webPageFragment)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commitNow();
        } else {
            Fragment fragment = supportFragmentManager.findFragmentById(R.id.content);
            if (fragment != null) {
                supportFragmentManager
                        .beginTransaction()
                        .remove(fragment)
                        .commitNow();
            }
        }
    }

    Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.content);
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
    public TabNotifierManager getTabManager() {
        return tabManager;
    }

    @Override
    public void onPageLoaded(String tabId, String url, String title) {
        TabModel tab = tabManager.getTab(tabId);
        tabEditor.setUrl(tab, url);
        tabEditor.setTitle(tab, title);
        onTabChanged(tab);
    }

    private void onTabChanged(TabModel tab) {
        browserView.onTabChanged(tab);
        if (isCurrentTab(tab)){
            onCurrentTabChanged(tab);
        }
    }

    private boolean isCurrentTab(TabModel tab) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof WebPageFragment) {
            String tabId = ((WebPageFragment) currentFragment).getTabId();
            if (tabId.equals(tab.getId())) {
                return true;
            }
        }
        return false;
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

    private void openUrlInCurrentTab(String url) {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment instanceof WebPageFragment) {
            ((WebPageFragment) currentFragment).goUrl(url);
        }
    }

    private void openPage(@Nullable TabModel tabModel) {
        replaceFragment(tabModel);
        onCurrentTabChanged(tabModel);
    }

    private void onCurrentTabChanged(@Nullable  TabModel tabModel) {
        String title = tabModel != null ? tabEditor.getTitle(tabModel) : getString(R.string.app_name);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
        browserView.onCurrentTabChanged(tabModel);
    }

    private class TabManagerListener implements TabNotifierManager.TabListener {
        @Override
        public void onTabAdded(TabModel tabModel) {
            openPage(tabModel);
            openUrlInCurrentTab(tabEditor.getUrl(tabModel));
            browserView.onTabAdded(tabModel);
        }

        @Override
        public void onTabRemoved(TabModel tabModel, int position) {
            webViewPool.remove(tabModel.getId());
            browserView.onTabRemoved(tabModel);
            TabModel tabModelToOpen = null;
            if (tabManager.count() > 0) {
                int posToOpen = position;
                if (posToOpen >= tabManager.count()) {
                    posToOpen = tabManager.count() - 1;
                }
                tabModelToOpen = tabManager.getTab(posToOpen);
            }
            openPage(tabModelToOpen);
        }

        @Override
        public void onTabSetChanged() {
            webViewPool.evictAll();
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof WebPageFragment) {
                String tabId = ((WebPageFragment) currentFragment).getTabId();
                TabModel tab = tabManager.getTab(tabId);
                if (tab == null) {
                    tab = tabManager.getTab(0);
                }
                openPage(tab);
            }
        }

    }
}
