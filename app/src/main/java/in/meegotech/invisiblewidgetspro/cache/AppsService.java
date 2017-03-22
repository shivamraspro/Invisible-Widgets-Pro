package in.meegotech.invisiblewidgetspro.cache;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by shivam on 21/03/17.
 */

public class AppsService extends IntentService {

    private static final String TAG = AppsService.class.getName();

    private ArrayList<ApplicationInfo> applist;

    private Context mContext;

    public AppsService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        mContext = getApplicationContext();

        //Delete All Apps
        getContentResolver().delete(AppsContract.CONTENT_URI, null, null);

        applist = checkForLaunchIntent(mContext.getPackageManager().getInstalledApplications
                (PackageManager.GET_META_DATA));

        Collections.sort(applist, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo applicationInfo, ApplicationInfo t1) {
                String name1 = applicationInfo.loadLabel(mContext.getPackageManager()).toString();
                String name2 = t1.loadLabel(mContext.getPackageManager()).toString();

                return name1.compareToIgnoreCase(name2);
            }
        });

        ApplicationInfo app;
        ContentValues[] contentValues = new ContentValues[applist.size()];
        //First entry is for blank/placeholder widget
        contentValues[0] = new ContentValues(3);
        contentValues[0].put(AppsContract.AppColumns._ID, 0);
        contentValues[0].put(AppsContract.AppColumns.APP_NAME, "");
        contentValues[0].put(AppsContract.AppColumns.PACKAGE_NAME, "");
        for(int i = 1; i < applist.size(); i++) {
            app = applist.get(i);
            contentValues[i] = new ContentValues(3);
            contentValues[i].put(AppsContract.AppColumns._ID, i);
            contentValues[i].put(AppsContract.AppColumns.APP_NAME, app.loadLabel(mContext
                    .getPackageManager()).toString());
            contentValues[i].put(AppsContract.AppColumns.PACKAGE_NAME, app.packageName);
        }

        getContentResolver().bulkInsert(AppsContract.CONTENT_URI, contentValues);
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
