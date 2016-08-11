package com.forcelain.awesomebrowser.presentation.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.forcelain.awesomebrowser.R;

public class TabViewHolder extends RecyclerView.ViewHolder {
    TextView titleView;
    View closeView;

    public TabViewHolder(View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.tab_title);
        closeView = itemView.findViewById(R.id.tab_close);
    }
}
