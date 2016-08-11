package com.forcelain.awesomebrowser.presentation.tabs;

public class TabViewItem {

    private final String id;
    private String title;

    public TabViewItem(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }
}
