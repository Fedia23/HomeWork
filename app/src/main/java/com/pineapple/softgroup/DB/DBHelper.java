package com.pineapple.softgroup.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pineapple.softgroup.interfaces.IDBHelper;
import com.pineapple.softgroup.DB.model.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper implements IDBHelper {

    public static final String TAG = DBHelper.class.getSimpleName();
    public static final int DATEBASE_VERSION = 5;
    public static final String DATEBASE_NAME = "ContactDB";
    public static final String TABLE_CONTACT = "User";

    public static final String KEY_ID = "ID";
    public static final String KEY_NAME = "Name";
    public static final String KEY_LOGIN = "Login";
    public static final String KEY_PASS = "Pass";
    public int countUser;

    public DBHelper(Context context) {
        super(context, DATEBASE_NAME, null, DATEBASE_VERSION);
    }

    public static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_CONTACT + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_LOGIN + " TEXT, "
            + KEY_PASS + " TEXT)";

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
    public void addContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(KEY_NAME, user.getName());
        value.put(KEY_LOGIN, user.getLogin());
        value.put(KEY_PASS, user.getPass());

        Long id = db.insert(TABLE_CONTACT, null, value);
        db.close();

        Log.d(TAG, "user inserted" + id);
        countUser++;
    }

    @Override
    public User getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACT, new String[] { KEY_ID, KEY_NAME, KEY_LOGIN, KEY_PASS }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        User user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return user;
    }

    @Override
    public List<User> getAllContacts() {
        List<User> userList = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setID(Integer.parseInt(cursor.getString(0)));
                user.setName(cursor.getString(1));
                user.setLogin(cursor.getString(2));
                user.setPass(cursor.getString(3));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        return userList;
    }

    @Override
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }

    @Override
    public int updateContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, user.getName());
        values.put(KEY_LOGIN, user.getLogin());
        values.put(KEY_PASS, user.getPass());

        return db.update(TABLE_CONTACT, values, KEY_ID + " = ?",
                new String[] { String.valueOf(user.getID()) });
    }

    @Override
    public void deleteContact(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, KEY_ID + " = ?", new String[] { String.valueOf(user.getID()) });
        db.close();
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACT, null, null);
        db.close();
    }
}


