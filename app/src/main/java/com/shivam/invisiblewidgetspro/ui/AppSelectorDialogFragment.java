package com.shivam.invisiblewidgetspro.ui;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.shivam.invisiblewidgetspro.R;
import com.shivam.invisiblewidgetspro.extras.AppsAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shivam on 11/02/17.
 * <p>
 * Some of the code is taken from
 * http://stacktips.com/tutorials/android/how-to-get-list-of-installed-apps-in-android
 */

public class AppSelectorDialogFragment extends DialogFragment {
    private ListView listView;
    private ProgressBar loadingSpinner;
    private AppsAdapter adapter;
    private Context mContext;
    private ArrayList<ApplicationInfo> applist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container);
        mContext = getActivity();

        //todo use recyclerview instead of list view
        listView = (ListView) view.findViewById(R.id.listview_installed_apps);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                ((AppSelectedListener) getActivity()).getSelectedAppPackage(applist.get(position).packageName);

                dismiss();
            }
        });

        new LoadApplications().execute();

        return view;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(mContext, null, mContext.getString(R.string
                    .loading_application));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(mContext.getPackageManager().getInstalledApplications
                    (PackageManager.GET_META_DATA));
            //todo sort applist by app name

            adapter = new AppsAdapter(mContext, applist);
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            listView.setAdapter(adapter);
            progress.dismiss();

            super.onPostExecute(result);
        }


        private ArrayList<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
            ArrayList<ApplicationInfo> applist = new ArrayList<>();
            for (ApplicationInfo info : list) {
                try {
                    //to load only those apps that can be launched
                    if (null != mContext.getPackageManager().getLaunchIntentForPackage(info.packageName)) {
                        applist.add(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return applist;
        }
    }

    public interface AppSelectedListener {
        public void getSelectedAppPackage(String packageName);
    }
}
