package com.forcelain.awesomebrowser.presentation.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcelain.awesomebrowser.R;
import com.forcelain.awesomebrowser.tabs.TabEditor;
import com.forcelain.awesomebrowser.tabs.TabModel;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends RecyclerView.Adapter<TabViewHolder> {

    private AdapterActionsListener adapterActionsListener;
    private final List<TabViewItem> tabViewItems = new ArrayList<>();
    private final TabEditor tabEditor = new TabEditor();

    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tab_item, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TabViewHolder holder, int position) {
        TabViewItem tabViewItem = tabViewItems.get(position);
        holder.titleView.setText(tabViewItem.getTitle());
        holder.closeView.setOnClickListener(new TabCloseAction(tabViewItem));
        holder.itemView.setOnClickListener(new OpenTabAction(tabViewItem));
    }

    @Override
    public int getItemCount() {
        return tabViewItems.size();
    }

    public void setAdapterActionsListener(AdapterActionsListener adapterActionsListener) {
        this.adapterActionsListener = adapterActionsListener;
    }

    public void addItem(TabModel tabModel) {
        tabViewItems.add(map(tabModel));
        notifyDataSetChanged();
    }

    public void updateItem(TabModel tab) {
        TabViewItem item = findItem(tab);
        if (item != null) {
            update(item, tab);
            notifyDataSetChanged();
        }
    }

    public void removeItem(TabModel tabModel) {
        TabViewItem tabViewItem = findItem(tabModel);
        tabViewItems.remove(tabViewItem);
        notifyDataSetChanged();
    }

    public void setTabs(List<TabModel> tabModels) {
        tabViewItems.clear();
        for (TabModel tabModel : tabModels) {
            tabViewItems.add(map(tabModel));
        }
        notifyDataSetChanged();
    }

    private TabViewItem findItem(TabModel tab) {
        for (TabViewItem tabViewItem : tabViewItems) {
            if (tabViewItem.getId().equals(tab.getId())) {
                return tabViewItem;
            }
        }
        return null;
    }

    private TabViewItem map(TabModel tabModel) {
        TabViewItem tabViewItem = new TabViewItem(tabModel.getId());
        update(tabViewItem, tabModel);
        return tabViewItem;
    }

    private void update(TabViewItem tabViewItem, TabModel tabModel) {
        tabViewItem.setTitle(tabEditor.getTitle(tabModel));
    }

    public interface AdapterActionsListener {
        void onTabClosed(TabViewItem tabViewItem);
        void onTabClicked(TabViewItem tabViewItem);
    }

    private class TabCloseAction implements View.OnClickListener {
        private final TabViewItem tabViewItem;

        public TabCloseAction(TabViewItem tabViewItem) {
            this.tabViewItem = tabViewItem;
        }

        @Override
        public void onClick(View view) {
            if (adapterActionsListener != null) {
                adapterActionsListener.onTabClosed(tabViewItem);
            }
        }
    }

    private class OpenTabAction implements View.OnClickListener {
        private final TabViewItem tabViewItem;

        public OpenTabAction(TabViewItem tabViewItem) {
            this.tabViewItem = tabViewItem;
        }

        @Override
        public void onClick(View view) {
            if (adapterActionsListener != null) {
                adapterActionsListener.onTabClicked(tabViewItem);
            }
        }
    }
}
