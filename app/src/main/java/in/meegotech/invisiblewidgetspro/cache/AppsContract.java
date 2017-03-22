package in.meegotech.invisiblewidgetspro.cache;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shivam on 21/03/17.
 */

public class AppsContract {

    //Database schema information
    public static final String TABLE_APPS = "apps_iwp";

    public static final class AppColumns implements BaseColumns {
        public static final String APP_NAME = "app_name";

        public static final String PACKAGE_NAME = "package_name";
    }

    //Unique authority string for the content provider
    public static final String CONTENT_AUTHORITY = "in.meegotech.invisiblewidgetspro";

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_APPS;
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_APPS;

    //Base content Uri for accessing the provider
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(CONTENT_AUTHORITY)
            .appendPath(TABLE_APPS)
            .build();

    public static Uri buildAppUri(long id) {
        return ContentUris.withAppendedId(CONTENT_URI, id);
    }

    /* Helpers to retrieve column values */
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString( cursor.getColumnIndex(columnName) );
    }
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt( cursor.getColumnIndex(columnName) );
    }
}
