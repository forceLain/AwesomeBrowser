package com.forcelain.awesomebrowser.presentation;

import android.app.Application;

import com.forcelain.awesomebrowser.presentation.common.TabNotifierManager;
import com.forcelain.awesomebrowser.tabs.BaseTabManager;
import com.forcelain.awesomebrowser.tabs.TabIdGenerator;
import com.forcelain.awesomebrowser.tabs.UUIDGenerator;

public class AwesomeBrowserApp extends Application {

    private TabNotifierManager tabNotifierManager = new TabNotifierManager(new BaseTabManager());
    private TabIdGenerator tabIdGenerator = new UUIDGenerator();

    public TabNotifierManager getTabManager() {
        return tabNotifierManager;
    }

    public TabIdGenerator getTabIdGenerator() {
        return tabIdGenerator;
    }
}
