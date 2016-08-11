package com.forcelain.awesomebrowser.presentation.browser;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.presentation.tabs.TabViewItem;
import com.forcelain.awesomebrowser.presentation.tabs.TabsAdapter;
import com.forcelain.awesomebrowser.tabs.TabModel;

public class TabletBrowserView extends BrowserView {

    private final TabsAdapter adapter;

    public TabletBrowserView(Activity activity, TabNotifierManager tabManager) {
        super(activity, tabManager);
        RecyclerView tabsView = (RecyclerView) activity.findViewById(R.id.tabs_view);
        tabsView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        adapter = new TabsAdapter();
        adapter.setAdapterActionsListener(new TabsAdapter.AdapterActionsListener() {
            @Override
            public void onTabClosed(TabViewItem tabViewItem) {
                OnCloseTabListener listener = getOnCloseTabListener();
                if (listener != null) {
                    listener.onTabClosed(tabViewItem.getId());
                }
            }

            @Override
            public void onTabClicked(TabViewItem tabViewItem) {
                OnChoseTabListener listener = getOnChoseTabListener();
                if (listener != null) {
                    listener.onTabChosen(tabViewItem.getId());
                }
            }
        });
        adapter.setTabs(getTabManager().getAllTabs());
        tabsView.setAdapter(adapter);
    }

    @Override
    public void onTabAdded(TabModel tabModel) {
        super.onTabAdded(tabModel);
        adapter.addItem(tabModel);
    }

    @Override
    public void onTabRemoved(TabModel tabModel) {
        super.onTabRemoved(tabModel);
        adapter.removeItem(tabModel);
    }

    @Override
    public void onTabChanged(TabModel tab) {
        super.onTabChanged(tab);
        adapter.updateItem(tab);
    }
}
