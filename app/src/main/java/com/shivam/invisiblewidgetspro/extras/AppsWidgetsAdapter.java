package com.shivam.invisiblewidgetspro.extras;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.ui.MainActivity;

import java.util.ArrayList;

/**
 * Created by shivam on 20/02/17.
 */

public class AppsWidgetsAdapter extends RecyclerView.Adapter<AppsWidgetsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<MainActivity.Dataset> appWidgetData;

    public AppsWidgetsAdapter(Context context, ArrayList<MainActivity.Dataset> appWidgetData) {
        mContext = context;
        this.appWidgetData = appWidgetData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View c = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_widgets_list_item, parent, false);
        ViewHolder vh = new ViewHolder(c);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.icon.setImageDrawable(appWidgetData.get(i).getApplicationInfo()
                .loadIcon(mContext.getPackageManager()));
        holder.name.setText(appWidgetData.get(i).getApplicationInfo()
                .loadLabel(mContext.getPackageManager()));
        holder.id.setText("#" + appWidgetData.get(i).getWidgetId());
    }

    @Override
    public int getItemCount() {
        return appWidgetData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView id;
        LinearLayout listItem;

        ViewHolder(View view) {
            super(view);
            listItem = (LinearLayout) view.findViewById(R.id.app_widget_container);
            id = (TextView) listItem.findViewById(R.id.app_widget_id);
            icon = (ImageView) listItem.findViewById(R.id.app_widget_icon);
            name = (TextView) listItem.findViewById(R.id.app_widget_name);
        }
    }
}
