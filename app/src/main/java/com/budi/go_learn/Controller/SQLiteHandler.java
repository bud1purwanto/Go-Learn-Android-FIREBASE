package com.budi.go_learn.Controller;

/**
 * Created by root on 11/9/17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "android_api";
    private static final String TABLE_USER = "user";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_PELAJARAN = "pelajaran";
    private static final String KEY_STATUS = "status";
    private static final String KEY_KET = "description";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_WORK = "work";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_PHONE + " TEXT,"
                + KEY_GENDER + " TEXT," + KEY_ADDRESS + " TEXT,"
                + KEY_UID + " TEXT," + KEY_CREATED_AT + " TEXT,"
                + KEY_PELAJARAN + " TEXT," + KEY_STATUS + " TEXT,"
                + KEY_KET + " TEXT," + KEY_ACTIVE + " TEXT," + KEY_WORK + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String phone, String gender, String address,
                        String uid, String created_at, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_GENDER, gender);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_STATUS, status);

        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void addP(String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void updateUser(String name, String phone, String address, String gender, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_GENDER, gender);
        values.put(KEY_ADDRESS, address);


        long id = db.update(TABLE_USER,  values, KEY_EMAIL + "='" + email+"'", null);
        db.close();

        Log.d(TAG, "New user updated into sqlite: " + id);
    }

    public void addPengajar(String name, String email, String phone, String gender, String address,
                        String uid, String created_at, String pelajaran, String status, String description,
                            String active, String work) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_EMAIL, email);
        values.put(KEY_PHONE, phone);
        values.put(KEY_GENDER, gender);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_UID, uid);
        values.put(KEY_CREATED_AT, created_at);
        values.put(KEY_PELAJARAN, pelajaran);
        values.put(KEY_STATUS, status);
        values.put(KEY_KET, description);
        values.put(KEY_ACTIVE, active);
        values.put(KEY_WORK, work);

        long id = db.insert(TABLE_USER, null, values);
        db.close();

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    public void updatePengajar(String name, String phone, String address, String gender,
                               String email, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_GENDER, gender);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_KET, description);

        long id = db.update(TABLE_USER,  values, KEY_EMAIL + "='" + email+"'", null);
        db.close();

        Log.d(TAG, "New user updated into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("phone", cursor.getString(3));
            user.put("gender", cursor.getString(4));
            user.put("address", cursor.getString(5));
            user.put("uid", cursor.getString(6));
            user.put("created_at", cursor.getString(7));
            user.put("pelajaran", cursor.getString(8));
            user.put("status", cursor.getString(9));
            user.put("description", cursor.getString(10));
        }
        cursor.close();
        db.close();

        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}