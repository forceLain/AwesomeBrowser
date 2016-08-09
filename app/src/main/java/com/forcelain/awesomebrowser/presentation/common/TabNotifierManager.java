package com.forcelain.awesomebrowser.presentation.common;

import com.forcelain.awesomebrowser.tabs.TabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TabNotifierManager implements TabManager {

    private final TabManager tabManager;
    private Set<TabListener> tabListeners = new HashSet<>();

    public TabNotifierManager(TabManager tabManager) {
        this.tabManager = tabManager;
    }

    public void addTabListener(TabListener tabAddListener) {
        tabListeners.add(tabAddListener);
    }

    public void removeTabListener(TabListener tabAddedListener) {
        tabListeners.remove(tabAddedListener);
    }

    @Override
    public boolean addTab(TabModel tabModel) {
        boolean changed = tabManager.addTab(tabModel);
        if (changed) {
            notifyAdded(tabModel);
        }
        return changed;
    }

    @Override
    public boolean addTab(int position, TabModel tabModel) {
        boolean changed = tabManager.addTab(position, tabModel);
        if (changed) {
            notifyAdded(tabModel);
        }
        return changed;
    }

    @Override
    public boolean removeTab(TabModel tabModel) {
        int pos = tabManager.getAllTabs().indexOf(tabModel);
        boolean changed = tabManager.removeTab(tabModel);
        if (changed) {
            notifyRemoved(tabModel, pos);
        }
        return changed;
    }

    @Override
    public TabModel removeTab(int position) {
        TabModel tabModel = tabManager.removeTab(position);
        notifyRemoved(tabModel, position);
        return tabModel;
    }

    @Override
    public TabModel getTab(int position) {
        return tabManager.getTab(position);
    }

    @Override
    public TabModel getTab(String id) {
        return tabManager.getTab(id);
    }

    @Override
    public List<TabModel> getAllTabs() {
        return tabManager.getAllTabs();
    }

    @Override
    public int count() {
        return tabManager.count();
    }

    @Override
    public void setTabs(List<TabModel> tabModels) {
        tabManager.setTabs(tabModels);
        notifyTabSetChanged();

    }

    private void notifyAdded(TabModel tabModel) {
        for (TabListener tabAddedListener : new ArrayList<>(tabListeners)) {
            tabAddedListener.onTabAdded(tabModel);
        }
    }

    private void notifyRemoved(TabModel tabModel, int pos) {
        for (TabListener tabRemoveListener : new ArrayList<>(tabListeners)) {
            tabRemoveListener.onTabRemoved(tabModel, pos);
        }
    }

    private void notifyTabSetChanged() {
        for (TabListener tabRemoveListener : new ArrayList<>(tabListeners)) {
            tabRemoveListener.onTabSetChanged();
        }
    }

    public interface TabListener {
        void onTabAdded(TabModel tabModel);
        void onTabRemoved(TabModel tabModel, int position);
        void onTabSetChanged();
    }
}
