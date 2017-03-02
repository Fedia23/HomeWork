package com.pineapple.softgroup.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pineapple.softgroup.DB.model.LastLocation;
import com.pineapple.softgroup.DB.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class DBHelperLastLocation extends SQLiteOpenHelper {
    public static final int DATEBASE_VERSION = 1;
    public static final String DATEBASE_NAME = "LastLocation";
    public static final String TABLE_CONTACT = "Location";

    public static final String KEY_ID = "Id";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    private static final String TAG = "Location";

    public DBHelperLastLocation(Context context) {
        super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_CONTACT + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LATITUDE + " INTEGER, "
            + KEY_LONGITUDE + " INTEGER)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL(CREATE_TABLE);
    }

    public void addLockation(LastLocation lock) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_LATITUDE, lock.getLatitude());
        value.put(KEY_LONGITUDE, lock.getLongitude());

        Long id = db.insert(TABLE_CONTACT, null, value);
        db.close();

        Log.d(TAG, "inserted" + id);
    }

    public List<LastLocation> getAllLockation() {
        List<LastLocation> contactsList = new ArrayList<LastLocation>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                LastLocation lastLocation = new LastLocation();
                lastLocation.setID(Integer.parseInt(cursor.getString(0)));
                lastLocation.setLatitude(Double.parseDouble(cursor.getString(1)));
                lastLocation.setLongitude(Double.parseDouble(cursor.getString(2)));

                contactsList.add(lastLocation);
            } while (cursor.moveToNext());
        }
        return contactsList;
    }

    public int getLockCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, null, null);
        db.close();
    }

}
