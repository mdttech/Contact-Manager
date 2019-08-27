package com.example.hp.contactmanager;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.hp.contactmanager.data.ContactContract;


public class ContactCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ContactCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ContactCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }
    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    /**
     * This method binds the contact data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context.
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phone_number);


        // Find the columns of contact attributes that we're interested in
        int columnIdIndex = cursor.getColumnIndex(ContactContract.ContactEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_NAME);
        int phoneNumberColumnIndex = cursor.getColumnIndex(ContactContract.ContactEntry.COLUMN_CONTACT_PHONE_NUMBER);

        // Read the Contact attributes from the Cursor for the current contact
        String contactName = cursor.getString(nameColumnIndex);
        String contactPhoneNumber = cursor.getString(phoneNumberColumnIndex);
        final int productID = cursor.getInt(columnIdIndex);


        // Update the TextViews with the attributes for the current product
        nameTextView.setText(contactName);
        phoneNumberTextView.setText(contactPhoneNumber);




    }


}
