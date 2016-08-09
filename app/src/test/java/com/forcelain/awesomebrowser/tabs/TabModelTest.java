package com.forcelain.awesomebrowser.tabs;

import android.os.Bundle;

import com.forcelain.awesomebrowser.BaseRobolectricTest;
import com.forcelain.awesomebrowser.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

public class TabModelTest extends BaseRobolectricTest {

    @Test
    public void testGetId() throws Exception {
        TabModel tabModel = new TabModel("i-am-id");
        String id = tabModel.getId();
        assertEquals(id, "i-am-id");

    }

    @Test
    public void testGetData() throws Exception {
        TabModel tabModel = new TabModel("id");
        Bundle data = tabModel.getData();
        assertNotNull(data);
    }

    @Test
    public void testEquals() throws Exception {
        TabModel tabModelLeft = new TabModel("id");
        tabModelLeft.getData().putString("any_key", "any_string");
        TabModel tabModelRight = new TabModel("id");
        TabModel tabModelOther = new TabModel("id2");

        assertEquals(tabModelLeft, tabModelRight);
        assertEquals(tabModelLeft, tabModelLeft);
        assertNotEquals(tabModelLeft, tabModelOther);
    }

    @Test
    public void testHashCode() throws Exception {
        TabModel tabModelLeft = new TabModel("id");
        tabModelLeft.getData().putString("any_key", "any_string");
        TabModel tabModelRight = new TabModel("id");
        assertEquals(tabModelLeft.hashCode(), tabModelRight.hashCode());
    }
}