package com.forcelain.awesomebrowser.presentation.browser;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;

public class BrowserView {

    private final EditText urlView;
    private final Activity activity;
    private final TabManager tabManager;
    private OnOpenUrlListener onOpenUrlListener;
    private OnAddTabListener onAddTabListener;
    private OnCloseTabListener onCloseTabListener;
    private OnChoseTabListener onChoseTabListener;
    private OnClearListener onClearListener;

    public BrowserView(Activity activity, TabManager tabManager) {
        this.activity = activity;
        this.tabManager = tabManager;
        urlView = (EditText) activity.findViewById(R.id.url_edit_text);
        urlView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (onOpenUrlListener != null) {
                    onOpenUrlListener.onOpenUrl(urlView.getText().toString());
                }
                return true;
            }
        });
        activity.findViewById(R.id.go_url_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onOpenUrlListener != null) {
                    onOpenUrlListener.onOpenUrl(urlView.getText().toString());
                }
            }
        });
        activity.findViewById(R.id.add_tab_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAddTabListener != null) {
                    onAddTabListener.onAddNewTab();
                }
            }
        });
    }

    public Activity getActivity() {
        return activity;
    }

    public TabManager getTabManager() {
        return tabManager;
    }

    public void onTabChanged(TabModel tab) {

    }

    public void onCurrentTabChanged(@Nullable TabModel tabModel) {
        TabEditor tabEditor = new TabEditor();
        String url = tabModel != null ? tabEditor.getUrl(tabModel) : null;
        urlView.setText(url);
    }

    public void onTabAdded(TabModel tabModel) {

    }

    public void onTabRemoved(TabModel tabModel) {

    }

    public OnOpenUrlListener getOnOpenUrlListener() {
        return onOpenUrlListener;
    }

    public void setOnOpenUrlListener(OnOpenUrlListener onOpenUrlListener) {
        this.onOpenUrlListener = onOpenUrlListener;
    }

    public OnAddTabListener getOnAddTabListener() {
        return onAddTabListener;
    }

    public void setOnAddTabListener(OnAddTabListener onAddTabListener) {
        this.onAddTabListener = onAddTabListener;
    }

    public OnCloseTabListener getOnCloseTabListener() {
        return onCloseTabListener;
    }

    public void setOnCloseTabListener(OnCloseTabListener onCloseTabListener) {
        this.onCloseTabListener = onCloseTabListener;
    }

    public OnChoseTabListener getOnChoseTabListener() {
        return onChoseTabListener;
    }

    public void setOnChoseTabListener(OnChoseTabListener onChoseTabListener) {
        this.onChoseTabListener = onChoseTabListener;
    }

    public OnClearListener getOnClearListener() {
        return onClearListener;
    }

    public void setOnClearListener(OnClearListener onClearListener) {
        this.onClearListener = onClearListener;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public interface OnOpenUrlListener {
        void onOpenUrl(String url);
    }

    public interface OnAddTabListener {
        void onAddNewTab();
    }

    public interface OnCloseTabListener {
        void onTabClosed(String tabId);
    }

    public interface OnChoseTabListener {
        void onTabChosen(String tabId);
    }

    public interface OnClearListener {
        void onTabCleared();
    }
}
