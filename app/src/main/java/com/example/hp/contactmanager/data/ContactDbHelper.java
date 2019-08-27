package com.example.hp.contactmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactDbHelper extends SQLiteOpenHelper{

    /**
        * Name of the database file
     */

    private static final String DATABASE_NAME = "contact.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */

    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ContactDbHelper}.
     *
     * @param context of the app
     */

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    // Create a String that contains the SQL statement to create the CONTACTS table

        String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE " + ContactContract.ContactEntry.TABLE_NAME + " ("
        + ContactContract.ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + ContactContract.ContactEntry.COLUMN_CONTACT_NAME + " TEXT NOT NULL, "
        + ContactContract.ContactEntry.COLUMN_CONTACT_ADDRESS + " TEXT NOT NULL, "
        + ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL_ID + " TEXT NOT NULL, "
        + ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER + " TEXT NOT NULL); " ;

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CONTACTS_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.


    }

}







