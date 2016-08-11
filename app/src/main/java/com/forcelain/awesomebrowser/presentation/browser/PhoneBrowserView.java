package com.forcelain.awesomebrowser.presentation.browser;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;

import java.util.List;

public class PhoneBrowserView extends BrowserView {
    public PhoneBrowserView(Activity activity, TabNotifierManager tabManager) {
        super(activity, tabManager);
        activity.findViewById(R.id.close_tab_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnCloseTabListener listener = getOnCloseTabListener();
                listener.onTabClosed(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getActivity().getMenuInflater().inflate(R.menu.browser_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.choose_tab:
                showChoseTabDialog();
                return true;
            case R.id.clear_tabs:
                OnClearListener listener = getOnClearListener();
                if (listener != null) {
                    listener.onTabCleared();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChoseTabDialog() {
        TabEditor tabEditor = new TabEditor();
        TabManager tabManager = getTabManager();
        final List<TabModel> tabModels = tabManager.getAllTabs();
        CharSequence[] items = new CharSequence[tabModels.size()];
        for (int i = 0; i < items.length; i++) {
            items[i] = tabEditor.getTitle(tabModels.get(i));
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TabModel tabModel = getTabManager().getTab(i);
                OnChoseTabListener onChoseTabListener = getOnChoseTabListener();
                if (onChoseTabListener != null) {
                    onChoseTabListener.onTabChosen(tabModel.getId());
                }
            }
        };

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_tab)
                .setItems(items, listener)
                .show();

    }
}
