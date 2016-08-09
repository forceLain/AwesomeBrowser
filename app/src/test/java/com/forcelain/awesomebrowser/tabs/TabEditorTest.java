package com.forcelain.awesomebrowser.tabs;

import com.forcelain.awesomebrowser.BaseRobolectricTest;

import org.junit.Test;

import static org.junit.Assert.*;

public class TabEditorTest extends BaseRobolectricTest {

    @Test
    public void testSetUrl() throws Exception {
        TabModel tabModel = new TabModel("id");
        TabEditor tabEditor = new TabEditor();
        tabEditor.setUrl(tabModel, "url");
        String url = tabModel.getData().getString(TabEditor.ARG_URL);
        assertEquals(url, "url");
    }

    @Test
    public void testGetUrl() throws Exception {
        TabModel tabModel = new TabModel("id");
        tabModel.getData().putString(TabEditor.ARG_URL, "url");
        TabEditor tabEditor = new TabEditor();
        String url = tabEditor.getUrl(tabModel);
        assertEquals(url, "url");
    }

    @Test
    public void testSetTitle() throws Exception {
        TabModel tabModel = new TabModel("id");
        TabEditor tabEditor = new TabEditor();
        tabEditor.setTitle(tabModel, "title");
        String title = tabModel.getData().getString(TabEditor.ARG_TITLE);
        assertEquals(title, "title");
    }

    @Test
    public void testGetTitle() throws Exception {
        TabModel tabModel = new TabModel("id");
        tabModel.getData().putString(TabEditor.ARG_TITLE, "title");
        TabEditor tabEditor = new TabEditor();
        String title = tabEditor.getTitle(tabModel);
        assertEquals(title, "title");
    }
}