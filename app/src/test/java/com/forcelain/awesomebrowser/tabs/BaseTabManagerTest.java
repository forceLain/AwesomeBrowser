package com.forcelain.awesomebrowser.tabs;

import com.forcelain.awesomebrowser.BaseRobolectricTest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BaseTabManagerTest extends BaseRobolectricTest {

    @Test
    public void testAddTab() throws Exception {
        BaseTabManager tabManager = new BaseTabManager();
        TabModel tabModel1 = new TabModel("id");
        boolean added = tabManager.addTab(tabModel1);
        List<TabModel> allTabs = tabManager.getAllTabs();
        assertEquals(allTabs.size(), 1);
        assertEquals(allTabs.get(0), tabModel1);
        assertTrue(added);

        TabModel tabModel2 = new TabModel("id2");
        added = tabManager.addTab(0, tabModel2);
        allTabs = tabManager.getAllTabs();
        assertEquals(allTabs.size(), 2);
        assertEquals(allTabs.get(0), tabModel2);
        assertEquals(allTabs.get(1), tabModel1);
        assertTrue(added);

        TabModel tabModel3 = new TabModel("id2");
        tabModel3.getData().putString("any_key", "any_value");
        added = tabManager.addTab(0, tabModel3);
        allTabs = tabManager.getAllTabs();
        assertFalse(added);
        assertEquals(allTabs.size(), 2);
        String value = allTabs.get(0).getData().getString("any_key");
        assertNull(value);
    }

    @Test
    public void testCount() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        assertEquals(manager.count(), 0);
        manager.addTab(new TabModel(""));
        assertEquals(manager.count(), 1);
    }

    @Test
    public void testSetTabs() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        List<TabModel> list = new ArrayList<>();
        list.add(new TabModel("1"));
        manager.setTabs(list);
        List<TabModel> allTabs = manager.getAllTabs();
        assertEquals(allTabs.size(), list.size());
        assertEquals(allTabs.get(0), list.get(0));
    }


    @Test
    public void testSetTabs2() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        List<TabModel> list = new ArrayList<>();
        list.add(new TabModel("1"));
        list.add(new TabModel("1"));
        list.add(new TabModel("2"));
        manager.setTabs(list);
        List<TabModel> allTabs = manager.getAllTabs();
        assertNotEquals(allTabs, list);
        assertEquals(allTabs.size(), 2);
    }

    @Test
    public void testGetTab() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        TabModel tabModel0 = new TabModel("0");
        manager.addTab(tabModel0);
        TabModel tabModel1 = new TabModel("1");
        manager.addTab(tabModel1);
        TabModel tabMode2 = new TabModel("2");
        manager.addTab(tabMode2);

        TabModel tab = manager.getTab(2);
        assertEquals(tab, tabMode2);

        tab = manager.getTab("1");
        assertEquals(tab, tabModel1);

        tab = manager.getTab("no-id");
        assertNull(tab);
    }

    @Test
    public void testRemoveTab() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        TabModel tab = new TabModel("tab");
        manager.addTab(tab);
        TabModel tab2 = new TabModel("tab2");
        manager.addTab(tab2);
        TabModel removedTab = manager.removeTab(0);
        assertEquals(removedTab, tab);
        assertEquals(manager.count(), 1);

        boolean removed = manager.removeTab(new TabModel("no-id"));
        assertFalse(removed);
        assertEquals(manager.count(), 1);

        removed = manager.removeTab(tab2);
        assertTrue(removed);
        assertEquals(manager.count(), 0);
    }

    @Test
    public void testGetAllTabs() throws Exception {
        BaseTabManager manager = new BaseTabManager();
        List<TabModel> allTabs = manager.getAllTabs();
        assertNotNull(allTabs);
        manager.addTab(new TabModel(""));
        allTabs = manager.getAllTabs();
        assertEquals(allTabs.size(), 1);
    }
}