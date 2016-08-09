package com.forcelain.awesomebrowser.presentation.common;

import com.forcelain.awesomebrowser.BaseRobolectricTest;
import com.forcelain.awesomebrowser.tabs.BaseTabManager;
import com.forcelain.awesomebrowser.tabs.TabModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TabNotifierManagerTest extends BaseRobolectricTest {

    @Test
    public void commonTest() throws Exception {
        final AtomicInteger addCounter = new AtomicInteger();
        final AtomicInteger removeCounter = new AtomicInteger();
        final AtomicInteger changeCounter = new AtomicInteger();

        TabNotifierManager manager = new TabNotifierManager(new BaseTabManager());
        TabNotifierManager.TabListener tabListener = new TabNotifierManager.TabListener() {
            @Override
            public void onTabAdded(TabModel tabModel) {
                addCounter.incrementAndGet();
            }

            @Override
            public void onTabRemoved(TabModel tabModel, int position) {
                removeCounter.incrementAndGet();
            }

            @Override
            public void onTabSetChanged() {
                changeCounter.incrementAndGet();
            }
        };
        manager.addTabListener(tabListener);

        manager.addTab(0, new TabModel("1"));
        manager.addTab(new TabModel("2"));
        manager.addTab(new TabModel("2"));

        manager.removeTab(0);
        manager.removeTab(new TabModel("2"));
        manager.removeTab(new TabModel("no-id"));

        manager.setTabs(new ArrayList<TabModel>(0));

        assertEquals(addCounter.get(), 2);
        assertEquals(removeCounter.get(), 2);
        assertEquals(changeCounter.get(), 1);

        manager.removeTabListener(tabListener);

        manager.addTab(new TabModel("3"));
        manager.removeTab(0);
        manager.setTabs(new ArrayList<TabModel>(0));

        assertEquals(addCounter.get(), 2);
        assertEquals(removeCounter.get(), 2);
        assertEquals(changeCounter.get(), 1);

    }

}