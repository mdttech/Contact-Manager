package com.example.hp.contactmanager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public class ContactContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */

    public static final String CONTENT_AUTHORITY = "com.example.hp.contactmanager";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.contacts/contacts/ is a valid path for
     * looking at contact data. content://com.example.android.contact/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */


    public static final String PATH_CONTACTS = "contacts";


    private ContactContract() {

    }

    public static abstract class ContactEntry implements BaseColumns {

        /**
         * The content URI to access the contacts data in the provider
         */

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CONTACTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of contacts.
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single contacts.
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String TABLE_NAME = "contacts";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_CONTACT_NAME = "name";
        public static final String COLUMN_CONTACT_ADDRESS = "address";
        public static final String COLUMN_CONTACT_EMAIL_ID = "email_id";
        public static final String COLUMN_CONTACT_PHONE_NUMBER = "phone_number";

    }

}
