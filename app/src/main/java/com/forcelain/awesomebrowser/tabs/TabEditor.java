package com.forcelain.awesomebrowser.tabs;

/**
 * Convenience class to store and retrieve data in/from TabModels
 * You can use it, or extend it, or use custom mechanism as you wish
 */
public class TabEditor {

    public static final String ARG_URL = "ARG_URL";
    public static final String ARG_TITLE = "ARG_TITLE";

    public void setUrl(TabModel tabModel, String url) {
        tabModel.getData().putString(ARG_URL, url);
    }

    public String getUrl(TabModel tabModel) {
        return tabModel.getData().getString(ARG_URL);
    }

    public void setTitle(TabModel tabModel, String title) {
        tabModel.getData().putString(ARG_TITLE, title);
    }

    public String getTitle(TabModel tabModel) {
        return tabModel.getData().getString(ARG_TITLE);
    }
}
