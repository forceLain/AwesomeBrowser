package com.forcelain.awesomebrowser.tabs;

import java.util.List;

public interface TabManager {
    boolean addTab(TabModel tabModel);
    boolean addTab(int position, TabModel tabModel);
    boolean removeTab(TabModel tabModel);
    TabModel removeTab(int position);
    TabModel getTab(int position);
    TabModel getTab(String id);
    List<TabModel> getAllTabs();
    int count();
    void setTabs(List<TabModel> tabModels);
}
