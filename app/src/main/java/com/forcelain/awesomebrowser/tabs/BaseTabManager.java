package com.forcelain.awesomebrowser.tabs;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BaseTabManager implements TabManager {

    private final List<TabModel> tabs = new ArrayList<>();

    @Override
    public boolean addTab(@NonNull TabModel tabModel) {
        return addTabInternal(-1, tabModel);
    }

    @Override
    public boolean addTab(int position, TabModel tabModel) {
        return addTabInternal(position, tabModel);
    }

    private boolean addTabInternal(int position, TabModel tabModel) {
        boolean contains = tabs.contains(tabModel);
        //We don't have tons of TabModel-s, so using a Set instead of List
        //only for checking duplicates is a bad idea
        if (!contains) {
            if (position >= 0) {
                tabs.add(position, tabModel);
            } else {
                tabs.add(tabModel);
            }
            return true;

        }
        return false;
    }

    @Override
    public int count() {
        return tabs.size();
    }

    @Override
    public void setTabs(List<TabModel> tabModels) {
        tabs.clear();
        if (tabModels != null) {
            HashSet<TabModel> tabSet = new HashSet<>(tabModels);
            tabs.addAll(tabSet);
        }
    }

    @Override
    public TabModel getTab(int position) {
        return tabs.get(position);
    }

    @Override
    public TabModel getTab(String id) {
        for (TabModel tab : tabs) {
            if (TextUtils.equals(id, tab.getId())) {
                return tab;
            }
        }
        return null;
    }

    @Override
    public TabModel removeTab(int currentItem) {
        return tabs.remove(currentItem);
    }

    @Override
    public boolean removeTab(TabModel tabModel) {
        return tabs.remove(tabModel);
    }

    @Override
    public List<TabModel> getAllTabs() {
        return new ArrayList<>(tabs);
    }
}
