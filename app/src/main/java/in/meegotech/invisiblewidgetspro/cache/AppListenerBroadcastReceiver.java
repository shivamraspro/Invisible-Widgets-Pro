package in.meegotech.invisiblewidgetspro.cache;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shivam on 21/03/17.
 */

public class AppListenerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent cacheAppsIntent = new Intent(context, AppsService.class);
        context.startService(cacheAppsIntent);
    }
}
