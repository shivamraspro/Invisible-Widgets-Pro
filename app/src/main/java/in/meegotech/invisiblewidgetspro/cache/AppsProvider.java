package in.meegotech.invisiblewidgetspro.cache;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by shivam on 21/03/17.
 */

public class AppsProvider extends ContentProvider {

    private static final int APPS = 100;
    private static final int APPS_WITH_ID = 101;

    private AppsDbHelper mDbHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://in.meegotech.invisiblewidgetspro/apps_iwp
        uriMatcher.addURI(AppsContract.CONTENT_AUTHORITY,
                AppsContract.TABLE_APPS,
                APPS);

        // content://in.meegotech.invisiblewidgetspro/apps_iwp/{id}
        uriMatcher.addURI(AppsContract.CONTENT_AUTHORITY,
                AppsContract.TABLE_APPS + "/#", APPS_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AppsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case APPS :
                return AppsContract.CONTENT_TYPE;
            case APPS_WITH_ID :
                return AppsContract.CONTENT_ITEM_TYPE;
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String
            selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case APPS :
                cursor = mDbHelper.getReadableDatabase().query(
                        AppsContract.TABLE_APPS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case APPS_WITH_ID :
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", AppsContract.AppColumns._ID);
                selectionArgs = new String[]{String.valueOf(id)};

                cursor = mDbHelper.getReadableDatabase().query(
                        AppsContract.TABLE_APPS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;

            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;    }

    //will not be needed in this version
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //Implement new task insert
        //Expected Uri: content://com.google.developer.taskmaker/tasks

        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case APPS :
                long id = mDbHelper.getWritableDatabase().insert(
                        AppsContract.TABLE_APPS,
                        null,
                        contentValues
                );
                if (id > 0)
                    returnUri = AppsContract.buildAppUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (sUriMatcher.match(uri)) {
            case APPS:
                //Rows aren't counted with null selection
                selection = (selection == null) ? "1" : selection;
                break;
            case APPS_WITH_ID:
                long id = ContentUris.parseId(uri);
                selection = String.format("%s = ?", AppsContract.AppColumns._ID);
                selectionArgs = new String[]{String.valueOf(id)};
                break;
            default:
                throw new IllegalArgumentException("Illegal delete URI");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = db.delete(AppsContract.TABLE_APPS, selection, selectionArgs);

        if (count > 0) {
            //Notify observers of the change
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    //will not be needed in this version
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;

        long id = ContentUris.parseId(uri);
        selection = String.format("%s = ?", AppsContract.AppColumns._ID);
        selectionArgs = new String[]{String.valueOf(id)};

        switch (sUriMatcher.match(uri)) {
            case APPS_WITH_ID :
                count = mDbHelper.getWritableDatabase().update(
                        AppsContract.TABLE_APPS,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;

            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(count != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case APPS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long id = db.insert(AppsContract.TABLE_APPS, null, value);
                        if (id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, contentValues);
        }
    }
}
