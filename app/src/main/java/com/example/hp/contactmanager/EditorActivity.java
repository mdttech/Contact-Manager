package com.example.hp.contactmanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.content.Loader;
import android.net.Uri;
import android.content.CursorLoader;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.app.LoaderManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hp.contactmanager.data.ContactContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /**
     * Identifier for the contact data loader
     */
    private static final int EXISTING_CONTACT_LOADER = 0;
    /**
     * Content URI for the existing contact (null if it's a new contact)
     */
    private Uri mCurrentContactUri;

    // Find all relevant views that we will need to read user input from
    /**
     * EditText field to enter the contact name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the contact price
     */
    private EditText mAddressEditText;


    /**
     * EditText field to enter the contacts email id
     */
    private EditText mEmailIdEditText;

    /**
     * EditText field to enter the contact phone number
     */
    private EditText mPhoneNumberEditText;


    private Button mCallButton;



    /**
     * Boolean flag that keeps track of whether the contact has been edited (true) or not (false)
     */
    private boolean mContactHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mContactHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mContactHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mNameEditText = (EditText) findViewById(R.id.edit_contact_name);
        mAddressEditText = (EditText) findViewById(R.id.edit_contact_address);
        mEmailIdEditText = (EditText) findViewById(R.id.edit_contact_email_id);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_contact_phone_number);
        mCallButton = (Button) findViewById(R.id.call_button);


        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        if (mCurrentContactUri == null) {
            setTitle("Add A Contact");

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a Contact that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Contact");

            // Initialize a loader to read the Contact data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CONTACT_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_contact_name);
        mAddressEditText = (EditText) findViewById(R.id.edit_contact_address);
        mEmailIdEditText = (EditText) findViewById(R.id.edit_contact_email_id);
        mPhoneNumberEditText = (EditText) findViewById(R.id.edit_contact_phone_number);
        mCallButton = (Button) findViewById(R.id.call_button);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mAddressEditText.setOnTouchListener(mTouchListener);
        mEmailIdEditText.setOnTouchListener(mTouchListener);
        mPhoneNumberEditText.setOnTouchListener(mTouchListener);

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });
    }


    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + mPhoneNumberEditText.getText().toString()));// only PHONE apps should handle this
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }
    /**
     * Get user input from editor and save new Contact into database.
     */
    private void saveContact() {

        boolean validationError = false;

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String addressString = mAddressEditText.getText().toString().trim();
        String emailIdString = mEmailIdEditText.getText().toString().trim();
        String phoneNumberString = mPhoneNumberEditText.getText().toString().trim();

        // Check if this is supposed to be a new Contact
        // and check if all the fields in the editor are blank
        if (mCurrentContactUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(addressString)
                && TextUtils.isEmpty(emailIdString) && TextUtils.isEmpty(phoneNumberString)) {
            // Since no fields were modified, we can return early without creating a new Contact.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and Contact attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // Check if  name Contact field is empty and if yes, display a toast message to the user
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Contact name is required", Toast.LENGTH_SHORT).show();
            validationError = true;
        }
        // If Contact name is filled, save it to the database
        else {
            values.put(ContactContract.ContactEntry.COLUMN_CONTACT_NAME, nameString);
        }

        if (TextUtils.isEmpty(addressString)) {
            Toast.makeText(this, "Contact address is required", Toast.LENGTH_SHORT).show();
            validationError = true;
        } else {
            values.put(ContactContract.ContactEntry.COLUMN_CONTACT_ADDRESS, addressString);
        }

        if (TextUtils.isEmpty(emailIdString)) {
            Toast.makeText(this, "Contact Email id is required", Toast.LENGTH_SHORT).show();
            validationError = true;
        } else {
            values.put(ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL_ID, emailIdString);
        }

        if (TextUtils.isEmpty(phoneNumberString)) {
            Toast.makeText(this, "Contact phone number is required", Toast.LENGTH_SHORT).show();
            validationError = true;
        } else {
            values.put(ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER, phoneNumberString);
        }

        if(validationError){
            return;
        }

        if (mCurrentContactUri == null) {
            // This is a NEW Contact, so insert a new Contact into the provider,
            // returning the content URI for the new Contact.
            Uri newUri = getContentResolver().insert(ContactContract.ContactEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error With Saving The Contact",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Contact Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING Contact, so update the Contact with content URI: mCurrentContactUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentContactUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentContactUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error With Saving The Contact",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Contact Saved",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new Contact, hide the "Delete" menu item.
        if (mCurrentContactUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save Contact to database
                saveContact();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                // If the Contact hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mContactHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the Contact hasn't changed, continue with handling back button press
        if (!mContactHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the Contact table
        String[] projection = {
                ContactContract.ContactEntry._ID,
                ContactContract.ContactEntry.COLUMN_CONTACT_NAME,
                ContactContract.ContactEntry.COLUMN_CONTACT_ADDRESS,
                ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL_ID,
                ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentContactUri,         // Query the content URI for the current Contact
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort call
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of Contact attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_NAME);
            int addressColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_ADDRESS);
            int emailIdColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_EMAIL_ID);
            int phoneNumberColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER);
            // Extract out the value from the Cursor for the given column index
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String emailId = cursor.getString(emailIdColumnIndex);
            String phoneNumber = cursor.getString(phoneNumberColumnIndex);
            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mAddressEditText.setText(address);
            mEmailIdEditText.setText(emailId);
            mPhoneNumberEditText.setText(phoneNumber);
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mAddressEditText.setText("");
        mEmailIdEditText.setText("");
        mPhoneNumberEditText.setText("");
    }


    /**
     * Prompt the user to confirm that they want to delete this Contact.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Contact.
                deleteContact();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the Contact
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the Contact in the database.
     */
    private void deleteContact() {
        // Only perform the delete if this is an existing Contact
        if (mCurrentContactUri != null) {
            // Call the ContentResolver to delete the Contact at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentContactUri
            // content URI already identifies the Contact that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentContactUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_contact_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_contact_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the Contact.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}