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
import android.widget.EditText;
import android.widget.TextView;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.TabManagerActivity;
import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabIdGenerator;
import com.forcelain.awesomebrowser.tabs.TabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;
import com.forcelain.awesomebrowser.tabs.UUIDGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TabManagerActivity, TabNotifierManager.TabListener {

    private static final String STATE_PAGE_ID_LIST = "STATE_PAGE_ID_LIST";
    private final Map<String, Fragment.SavedState> pages = new HashMap<>();
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

        if (savedInstanceState == null) {
            TabModel tabModel = getDefault();
            getTabManager().addTab(tabModel);
        } else {
            ArrayList<String> pageIds = savedInstanceState.getStringArrayList(STATE_PAGE_ID_LIST);
            if (pageIds != null) {
                for (String pageId : pageIds) {
                    Fragment.SavedState state = savedInstanceState.getParcelable(pageId);
                    pages.put(pageId, state);
                }
            }
        }
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
            WebPageFragment webPageFragment = WebPageFragment.create(tabModel.getId());
            Fragment.SavedState savedState = pages.get(tabModel.getId());
            if (savedState != null) {
                webPageFragment.setInitialSavedState(savedState);
            }

            replacePage(webPageFragment);
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

    private void replacePage(WebPageFragment webPageFragment) {

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.content);
        if (fragment instanceof WebPageFragment) {
            String tabId = ((WebPageFragment) fragment).getTabId();
            Fragment.SavedState savedState = fm.saveFragmentInstanceState(fragment);
            if (pages.containsKey(tabId)) {
                pages.put(tabId, savedState);
            }
        }

        fm
                .beginTransaction()
                .replace(R.id.content, webPageFragment)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
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

        pages.put(tabModel.getId(), null);

        WebPageFragment webPageFragment = WebPageFragment.create(tabModel.getId());
        replacePage(webPageFragment);
    }

    @Override
    public void onTabRemoved(TabModel tabModel, int position) {
        TabNotifierManager tabManager = getTabManager();
        updateCloseView(tabManager);
        pages.remove(tabModel.getId());
        position = bound(position, 0, tabManager.count() - 1);
        TabModel nextTab = tabManager.getTab(position);
        onPageSelected(nextTab);
    }

    @Override
    public void onTabSetChanged() {
        TabNotifierManager tabManager = getTabManager();
        List<TabModel> tabs = tabManager.getAllTabs();
        Fragment currentFragment = getCurrentFragment();
        String currentTabId = null;
        if (currentFragment instanceof WebPageFragment) {
            currentTabId = ((WebPageFragment) currentFragment).getTabId();
        }
        Iterator<Map.Entry<String, Fragment.SavedState>> iterator = pages.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Fragment.SavedState> entry = iterator.next();
            String pageId = entry.getKey();
            TabModel tab = tabManager.getTab(pageId);
            if (tab == null) {
                iterator.remove();
            }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<String> pageIds = new ArrayList<>(pages.size());
        for (Map.Entry<String, Fragment.SavedState> entry : pages.entrySet()) {
            String pageId = entry.getKey();
            Fragment.SavedState pageState = entry.getValue();
            outState.putParcelable(pageId, pageState);
            pageIds.add(pageId);
        }
        outState.putStringArrayList(STATE_PAGE_ID_LIST, pageIds);
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
