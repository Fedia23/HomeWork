package com.pineapple.softgroup.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pineapple.softgroup.DB.model.Contacts;
import com.pineapple.softgroup.DB.model.Marker;
import com.pineapple.softgroup.interfaces.IDBHelperMap;

import java.util.ArrayList;
import java.util.List;

public class DBHelperMap extends SQLiteOpenHelper implements IDBHelperMap{

    public static final int DATEBASE_VERSION = 2;
    public static final String DATEBASE_NAME = "MapDB";
    public static final String TABLE_CONTACT = "Map";

    public static final String KEY_ID = "Id";
    public static final String KEY_NAME = "Name";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_LATITUDE = "Latitude";
    public static final String KEY_LONGITUDE = "Longitude";
    private static final String TAG = "MapSQL";

    public DBHelperMap(Context context) {
        super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
    }

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_CONTACT + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_DESCRIPTION + " TEXT, "
            + KEY_LATITUDE + " INTEGER, "
            + KEY_LONGITUDE + " INTEGER)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void addMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, marker.getName());
        value.put(KEY_DESCRIPTION, marker.getDescription());
        value.put(KEY_LATITUDE, marker.getLatitude());
        value.put(KEY_LONGITUDE, marker.getLongitude());

        Long id = db.insert(TABLE_CONTACT, null, value);
        db.close();

        Log.d(TAG, "user inserted" + id);
    }

    public Marker getMarker(double lat, double lnt) {
        Marker marker = null;
        List<Marker> res = getAllMarkers();
       for (Marker m : res) {
            double e = m.getLatitude();
            double p = m.getLongitude();
            if (e == lat && p == lnt){
                String name = m.getName();
                String desc = m.getDescription();
                marker = new Marker(name, desc, lat, lnt);
            }
        }
        return marker;
    }

    @Override
    public List<Marker> getAllMarkers() {
        List<Marker> contactsList = new ArrayList<Marker>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Marker marker = new Marker();
                marker.setID(Integer.parseInt(cursor.getString(0)));
                marker.setName(cursor.getString(1));
                marker.setDescription(cursor.getString(2));
                marker.setLatitude(Double.parseDouble(cursor.getString(3)));
                marker.setLongitude(Double.parseDouble(cursor.getString(4)));

                contactsList.add(marker);
            } while (cursor.moveToNext());
        }
        return contactsList;
    }

    @Override
    public int getMarkersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    @Override
    public int updateMarker(Marker marker) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, marker.getName());
        values.put(KEY_DESCRIPTION, marker.getDescription());
        values.put(KEY_LATITUDE, marker.getLatitude());
        values.put(KEY_LONGITUDE, marker.getLongitude());

        return db.update(TABLE_CONTACT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(marker.getId()) });
    }

    @Override
    public void deleteMarker(Marker marker) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_ID + " = ?", new String[] { String.valueOf(marker.getId()) });
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, null, null);
        db.close();
    }
}
