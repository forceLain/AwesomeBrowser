package com.forcelain.awesomebrowser.tabs;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * An ordered collection of TabModel instances that
 * prohibits duplicates
 * This implementation does not expect a null-reference of the TabModel
 */
public class BaseTabManager implements TabManager {

    private final List<TabModel> tabs = new ArrayList<>();

    /**
     * Add tabModel at the end of the list
     * @param tabModel object to add
     * @return true if tabModel is added, false otherwise
     */
    @Override
    public boolean addTab(@NonNull TabModel tabModel) {
        return addTabInternal(-1, tabModel);
    }

    /**
     * Add tabModel at the position in the list
     * @param position index to insert at
     * @param tabModel object to insert
     * @return true if tabModel is added, false otherwise
     * @throws ArrayIndexOutOfBoundsException if the position is out of list's bounds
     */
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

    /**
     * @return count of tabs
     */
    @Override
    public int count() {
        return tabs.size();
    }

    /**
     * Replace any present list with the content of the passed list
     * All duplicates will be removed
     */
    @Override
    public void setTabs(List<TabModel> tabModels) {
        tabs.clear();
        if (tabModels != null) {
            HashSet<TabModel> tabSet = new HashSet<>(tabModels);
            tabs.addAll(tabSet);
        }
    }

    /**
     * Returns a TabModel at the given position
     * @throws ArrayIndexOutOfBoundsException if the position is out of list's bounds
     */
    @Override
    public TabModel getTab(int position) {
        return tabs.get(position);
    }

    /**
     * Return a TabModel with the given id if any
     * @param id an id of the TabModel
     * @return a TabModel with the given id or null if there is no such object
     */
    @Override
    public TabModel getTab(String id) {
        for (TabModel tab : tabs) {
            if (TextUtils.equals(id, tab.getId())) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Removes an object at the given position
     * @return removed object
     * @throws ArrayIndexOutOfBoundsException if the position is out of list's bounds
     */
    @Override
    public TabModel removeTab(int position) {
        return tabs.remove(position);
    }

    /**
     * Removes the given object from the list
     * @param tabModel object to remove
     * @return true if the object was in the list and has been removed, false otherwise
     */
    @Override
    public boolean removeTab(TabModel tabModel) {
        return tabs.remove(tabModel);
    }

    /**
     * Return a new collection with the content of the current internal list
     */
    @Override
    public List<TabModel> getAllTabs() {
        return new ArrayList<>(tabs);
    }
}
