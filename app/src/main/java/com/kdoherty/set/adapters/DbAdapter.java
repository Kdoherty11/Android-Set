package com.kdoherty.set.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kdoherty on 8/5/14.
 */
public class DbAdapter {

    private static final String TAG = "DbAdapter";

    // Field Names:
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String[] ALL_KEYS = new String[] { KEY_ROWID,
            KEY_USERNAME, KEY_PASSWORD };

    public static final int COL_PASSWORD = 2;

    // DataBase Info:
    public static final String DATABASE_NAME = "credentials";
    public static final String DATABASE_TABLE = "credentials";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL = "CREATE TABLE "
            + DATABASE_TABLE + " (" + KEY_ROWID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_USERNAME
            + " TEXT NOT NULL, " + KEY_PASSWORD + " TEXT NOT NULL" + ");";

    private final Context mContext;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DbAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(mContext);
    }

    // Open the database connection.
    public DbAdapter open() {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        mDbHelper.close();
    }

    // Add a new set of values to be inserted into the database.
    public long insertRow(String username, String password) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        // TODO: initialValues.put(KEY_HIGH_SCORE, 0);
        // Insert the data into the database.
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return mDb.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        Cursor c = mDb.query(true, DATABASE_TABLE, ALL_KEYS, null, null, null,
                null, null, null);
        if (c != null) {
            // c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = mDb.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null,
                null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public String getPassword(String userName) {
        String where = KEY_USERNAME + " = ?";
        Cursor c = mDb.query(DATABASE_TABLE, ALL_KEYS, where,
                new String[] { userName }, null, null, null, null);
        if (c.getCount() < 1) {
            c.close();
            return "NOT EXIST";
        }
        c.moveToFirst();
        String password = c.getString(COL_PASSWORD);
        c.close();
        return password;
    }

	/*
	 * public double getHighScore(String userName) { String where = KEY_USERNAME
	 * + " = ?"; Cursor c = db.query(DATABASE_TABLE, ALL_KEYS, where, new
	 * String[] { userName }, null, null, null, null); if (c.getCount() < 1) {
	 * c.close(); return -1; } c.moveToFirst(); double highScore =
	 * c.getInt(COL_HIGH_SCORE); c.close(); return highScore; }
	 */

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String username, String password,
                             double highScore) {
        String where = KEY_ROWID + "=" + rowId;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_USERNAME, username);
        newValues.put(KEY_PASSWORD, password);
        // TODO: newValues.put(KEY_HIGH_SCORE, highScore);
        // Insert it into the database.
        return mDb.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version "
                    + oldVersion + " to " + newVersion
                    + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
