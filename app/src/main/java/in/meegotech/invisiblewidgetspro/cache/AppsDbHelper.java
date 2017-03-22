package in.meegotech.invisiblewidgetspro.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import in.meegotech.invisiblewidgetspro.cache.AppsContract.AppColumns;

/**
 * Created by shivam on 21/03/17.
 */

public class AppsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "apps_iwp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TABLE_TASKS = String.format("create table %s"
                    +" (%s integer primary key, %s text, %s text)",
            AppsContract.TABLE_APPS,
            AppColumns._ID,
            AppColumns.APP_NAME,
            AppColumns.PACKAGE_NAME
    );

    public AppsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_TASKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + AppsContract.TABLE_APPS);

        onCreate(db);
    }
}
