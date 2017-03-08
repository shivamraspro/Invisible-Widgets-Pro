package com.shivam.invisiblewidgetspro.extras;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shivam.invisiblewidgetspro.R;

import java.util.ArrayList;

/**
 * Created by shivam on 11/02/17.
 */

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ApplicationInfo> applicationInfos;

    public AppsAdapter(Context context, ArrayList<ApplicationInfo> apps) {
        mContext = context;
        applicationInfos = apps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View c = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_app_list_item, parent, false);
        ViewHolder vh = new ViewHolder(c);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.pkgName.setVisibility(View.GONE);
            holder.icon.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R
                    .mipmap.app_launcher, null));
            holder.name.setText(mContext.getString(R.string.no_launcher_app));
            holder.name.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color
                    .cyan_700, null));
        } else {
            holder.icon.setImageDrawable(applicationInfos.get(position).loadIcon(mContext.getPackageManager()));
            holder.name.setText(applicationInfos.get(position).loadLabel(mContext.getPackageManager()));
            holder.name.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color
                    .grey_900, null));
            holder.pkgName.setVisibility(View.VISIBLE);
            holder.pkgName.setText(applicationInfos.get(position).packageName);
        }
    }
    @Override
    public int getItemCount() {
        return applicationInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView pkgName;
        LinearLayout listItem;

        ViewHolder(View view) {
            super(view);
            listItem = (LinearLayout) view.findViewById(R.id.list_item_container);
            icon = (ImageView) listItem.findViewById(R.id.app_icon);
            name = (TextView) listItem.findViewById(R.id.app_name);
            pkgName = (TextView) listItem.findViewById(R.id.app_pkg_name);
        }
    }
}
