package com.forcelain.awesomebrowser.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * This class keeps information of a "Tab"
 * The only required field is "id"
 * By default, TabModels are equals if their ids are equals
 * Any other data such url, title, color or whatever can be stored in the Bundle of the TabModel
 */
public class TabModel {

    private final String id;
    private final Bundle data = new Bundle();

    public TabModel(@NonNull String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Bundle getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TabModel tabModel = (TabModel) o;

        return id.equals(tabModel.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
