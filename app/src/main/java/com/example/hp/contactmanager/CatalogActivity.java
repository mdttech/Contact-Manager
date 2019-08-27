package com.example.hp.contactmanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.example.hp.contactmanager.data.ContactContract;
import com.getbase.floatingactionbutton.FloatingActionButton;


/**
 * Displays list of Contacts that were entered and stored in the app.
 */

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACT_LOADER = 0;
    ContactCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);


        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView contactListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        contactListView.setEmptyView(emptyView);

        mCursorAdapter = new ContactCursorAdapter(this, null);

        contactListView.setAdapter(mCursorAdapter);

        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri mCurrentContactUri = ContentUris.withAppendedId(ContactContract.ContactEntry.CONTENT_URI, id);
                intent.setData(mCurrentContactUri);
                startActivity(intent);

            }
        });

        getLoaderManager().initLoader(CONTACT_LOADER, null, this);

    }

    /**
     * Helper method to insert hardcoded Contact data into the database. For debugging purposes only.
     */
    private void insertContact() {

        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_NAME, "Tahseen");
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_ADDRESS, "Kulti");
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL_ID, "t@gmail.com");
        values.put(ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, "222222222");

        // Insert a new row for Tahseen o into the provider using the ContentResolver.
        // Use the {@link ContactEntry#CONTENT_URI} to indicate that we want to insert
        // into the Contact database table.
        // Receive the new content URI that will allow us to access Tahseen's data in the future.
        Uri newUri = getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all Product in the database.
     */
    private void deleteAllContact() {
        int rowsDeleted = getContentResolver().delete(ContactContract.ContactEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from Contact database");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertContact();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] project = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.COLUMN_CONTACT_NAME,
                ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER};
        return new CursorLoader(this,
                ContactContract.ContactEntry.CONTENT_URI,
                project,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }
}

