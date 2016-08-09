package com.forcelain.awesomebrowser.presentation.common;

public interface TabManagerActivity {
    TabNotifierManager getTabManager();

    void onPageLoaded(String tabId, String url, String title);
}
