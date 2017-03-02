package com.pineapple.softgroup.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pineapple.softgroup.interfaces.IDBhelperContact;
import com.pineapple.softgroup.DB.model.Contacts;

import java.util.ArrayList;
import java.util.List;

public class DBHelperContact extends SQLiteOpenHelper implements IDBhelperContact {
    public static final String TAG = DBHelperContact.class.getSimpleName();
    public static final int DATEBASE_VERSION = 9;
    public static final String DATEBASE_NAME_CONTACT = "FruitBD";
    public static final String TABLE_CONTACT_CONTACTS = "ContactFruit";

    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "Name";
    public static final String KEY_NUMBER = "Number";

    public DBHelperContact(Context context) {
        super(context, DATEBASE_NAME_CONTACT, null, DATEBASE_VERSION);
    }

    public static final String CREATE_TABLE_CONTACT = "CREATE TABLE " + TABLE_CONTACT_CONTACTS + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_NUMBER + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT_CONTACTS);
        db.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void addContact(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(KEY_NAME, contacts.getName());
        value.put(KEY_NUMBER, contacts.getNumber());

        Long id = db.insert(TABLE_CONTACT_CONTACTS, null, value);
        db.close();

        Log.d(TAG, "user inserted" + id);
    }

    @Override
    public Contacts getContact(int id) {
        return null;
    }

    @Override
    public List<Contacts> getAllContacts() {
        List<Contacts> contactsList = new ArrayList<Contacts>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contacts contacts = new Contacts();
                contacts.setId(Integer.parseInt(cursor.getString(0)));
                contacts.setName(cursor.getString(1));
                contacts.setNumber(cursor.getString(2));

                contactsList.add(contacts);
            } while (cursor.moveToNext());
        }
        return contactsList;
    }

    @Override
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    @Override
    public int updateContatct(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contacts.getName());
        values.put(KEY_NUMBER, contacts.getNumber());

        return db.update(TABLE_CONTACT_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contacts.getId()) });
    }

    @Override
    public void deleteConatct(Contacts contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT_CONTACTS, KEY_ID + " = ?", new String[] { String.valueOf(contacts.getId()) });
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT_CONTACTS, null, null);
        db.close();
    }
}
