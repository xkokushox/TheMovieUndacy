package com.freakybyte.movies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.freakybyte.movies.util.DebugUtils;

/**
 * Created by Jose Torres on 29/11/2016.
 */

public class DBAdapter {

    private static final String TAG = DBAdapter.class.getSimpleName();

    private static DBAdapter singleton;
    private SQLiteDatabase mDb;

    private Context mContext;
    private MovieDbHelper mDbHelper;

    public static DBAdapter getInstance() {
        if (singleton == null)
            singleton = new DBAdapter();
        return singleton;
    }

    public static DBAdapter getInstance(Context context) {
        if (singleton == null)
            singleton = new DBAdapter(context);
        return singleton;
    }

    public DBAdapter() {
    }

    public DBAdapter(Context context) {
        this.mContext = context;
    }

    private DBAdapter open() throws SQLException {
        mDbHelper = new MovieDbHelper(mContext);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    private void close() {
        mDbHelper.close();
    }

    public void begginTransaction() {
        open();
        mDb.beginTransaction();
    }

    public void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();
        mDb.endTransaction();
        close();
    }

    /**
     * Insert a new record into the specified table
     *
     * @param table   - The name of the table.
     * @param cValues - The values to set.
     * @return
     */
    public long insert(String table, ContentValues cValues) {
        long isInserted = MovieDbHelper.FAIL_DB_MODIFY;

        try {
            open();
            isInserted = mDb.insert(table, null, cValues);
        } catch (Exception e) {
            DebugUtils.logDebug(TAG, "Error Inserted:: " + e.getLocalizedMessage());
        }
        close();

        return isInserted;
    }

    public long insert(String table, String columnHack, ContentValues cValues) {
        long isInserted = MovieDbHelper.FAIL_DB_MODIFY;

        try {
            open();
            isInserted = mDb.insert(table, columnHack, cValues);
        } catch (Exception e) {
            DebugUtils.logDebug(TAG, "Error Inserted:: " + e.getLocalizedMessage());
        }
        close();

        return isInserted;
    }

    public Cursor getData(String tables) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tables);
        Cursor cursor = queryBuilder.query(mDb, null, null, null, null, null,
                null);
        return cursor;
    }

    public Cursor getData(Uri uri) {
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    /**
     * Delete an entry from database
     *
     * @param table     - The name of the table.
     * @param condition - sql delete condition
     * @return Return 1 if was successful else returns -1.
     */
    public int delete(String table, String condition, String[] args) {
        try {
            open();
            mDb.delete(table, condition, args);
            close();
            return 1;
        } catch (Exception e) {
            close();
            DebugUtils.logError("DbAdapter.delete", e.toString());
            return -1;
        }
    }

    /**
     * Delete all records in the specified table.
     *
     * @param table - The table to delete records.
     * @return 1 if the delete was successfull, otherwise -1.
     */
    public int deleteAll(String table) {
        try {
            open();
            mDb.delete(table, null, null);
            close();
            return 1;
        } catch (Exception e) {
            close();
            DebugUtils.logError("DbAdapter.deleteAll", e.toString());
            return -1;
        }
    }

    /**
     * Update a record from the specified table
     *
     * @param table   - The name of the table.
     * @param cValues - The values to set.
     * @return
     */
    public int update(String table, ContentValues cValues, String condition, String[] args) {
        int rowsUpdated = 0;

        try {
            begginTransaction();
            rowsUpdated = mDb.update(table, cValues, condition, args);
        } catch (Exception e) {
            DebugUtils.logError("DbAdapter.update", e.toString());
        } finally {
            setTransactionSuccessful();
        }
        return rowsUpdated;
    }

}
