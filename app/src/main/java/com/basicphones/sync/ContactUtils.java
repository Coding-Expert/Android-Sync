package com.basicphones.sync;

import android.app.Activity;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactUtils {
    public static int addContactInPhone(ContactInfo contact, Activity activity) {        ///// add new contact to phone
        ArrayList< ContentProviderOperation > ops = new ArrayList <ContentProviderOperation> ();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        /** ------------------------------- name ------------------------------------ */
//        if (contact.getName() != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                            contact.getLastName())
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                            contact.getFirstName())
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.PREFIX,
                            contact.getNamePrefix())
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.SUFFIX,
                            contact.getNameSuffix())
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME,
                            contact.getMiddleName())
                    .build());
//        }
        /** --------------------- phone ---------------------------------- */
        if(contact.getPhones().size() > 0) {
            for(int i = 0; i < contact.getPhones().size(); i++) {
                ops.add(ContentProviderOperation.
                        newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i).getNumber())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                contact.getPhones().get(i).getType())
                        .build());
            }
        }
        /** ------------------- email ------------------------- */
        if(contact.getEmails().size() > 0){
            for(int i = 0; i < contact.getEmails().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i).getEmail())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, contact.getEmails().get(i).getType())
                        .build());
            }
        }
        /** ----------------- organization -------------------- */
        if (!contact.getCompany().getOrganization().equals("") && !contact.getCompany().getTitle().equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, contact.getCompany().getOrganization())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contact.getCompany().getTitle())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }
        /** ------------------------------address ----------------------- */
        if(contact.getAddressList().size() > 0){
            for(int i = 0; i < contact.getAddressList().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, contact.getAddressList().get(i).getAddressName())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, contact.getAddressList().get(i).getAddressType())
                        .build());
            }
        }
        /** --------------------------------- IMAddress ------------------------------- */
        if(contact.getImAddressesList().size() > 0){
            for(int i = 0; i < contact.getImAddressesList().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Im.DATA, contact.getImAddressesList().get(i).getImaddressName())
                        .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, contact.getImAddressesList().get(i).getImaddressType())
                        .build());
            }
        }
        /** ------------------------------------ event ------------------------------------ */
        if(contact.getEvents().size() > 0){
            for(int i = 0; i < contact.getEvents().size(); i++) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, contact.getEvents().get(i).getEventDate())
                        .withValue(ContactsContract.CommonDataKinds.Event.TYPE, contact.getEvents().get(i).getEventType())
                        .build());
            }
        }
        int contactID = 0;
        try {
            ContentProviderResult[] res = activity.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Uri myContactUri = res[0].uri;
            int lastSlash = myContactUri.toString().lastIndexOf("/");
            int length = myContactUri.toString().length();
            contactID = Integer.parseInt((String) myContactUri.toString().subSequence(lastSlash+1, length));
//            contactID = Integer.parseInt(res[0].uri.getLastPathSegment());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity.getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return contactID;
    }

//    private void addInsertOp() {
//
//        if (!mIsNewContact) {
//            mValues.put(Phone.RAW_CONTACT_ID, mRawContactId);
//        }
//        ContentProviderOperation.Builder builder = newInsertCpo(
//                Data.CONTENT_URI, mIsSyncOperation, mIsYieldAllowed);
//        builder.withValues(mValues);
//        if (mIsNewContact) {
//            builder.withValueBackReference(Data.RAW_CONTACT_ID, mBackReference);
//        }
//        mIsYieldAllowed = false;
//        mBatchOperation.add(builder.build());
//    }

    public static boolean updateContact(ContactInfo contact, Activity activity)          ///// update content of selected contact
    {

        boolean success = true;
        try
        {
            ContentResolver contentResolver  = activity.getContentResolver();
            String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
            String[] nameParams = new String[]{ contact.getId(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
            String[] numberParams = new String[]{ contact.getId(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
            String[] photoParams = new String[]{ contact.getId(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//            if(!contact.getName().equals(""))
//            {
//                ops.add(android.content.ContentProviderOperation.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI)
//                        .withSelection(where,nameParams)
//                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName())
//                        .build());
//            }
            String id= contact.getId();
            List<String> old_phone = getPhoneList(id, activity);
            Uri url = ContactsContract.Data.CONTENT_URI;
            if(old_phone.size() > 0){
                for(int i = 0; i < old_phone.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + id + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + old_phone.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getPhones().size() > 0) {
                for(int i = 0; i < contact.getPhones().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getId(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getPhones().get(i)).
                                    withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }
            }
            List<String> old_email = getEmailList(contact.getId(), activity);
            if(old_email.size() > 0)
            {
                for(int i = 0; i < old_email.size(); i++) {
                    String where1 = ContactsContract.Data.CONTACT_ID + " = '" + contact.getId() + "' AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.CommonDataKinds.Email.ADDRESS + " = '" + old_email.get(i) + "'";
                    activity.getApplicationContext().getContentResolver().delete(url, where1, null);
                }
            }
            if(contact.getEmails().size() > 0){
                for(int i = 0; i < contact.getEmails().size(); i++) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID, getRawContactId(contact.getId(), activity))
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmails().get(i))
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                            .build());
                }
            }

            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            success = false;

        }
        return success;
    }


    public static List<String> getPhoneList(String contactId, Activity activity) {  ///////// get phone list from phone using contact id
        List<String> phones = new ArrayList<>();
        try{
            ContentResolver cr = activity.getContentResolver();
            String[] projection = new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER};
            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
//            int hash_number = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//            if (hash_number > 0) {
            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{contactId}, null);
            if(cp.getCount() > 0) {

                while (cp.moveToNext()) {
                    String number = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = cp.getInt(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    switch (type) {
                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                            // do something with the Home number here...
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                            // do something with the Mobile number here...
                            Log.d("ContactsH", number);
                            //                                    this.callByNumber(number);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                            // do something with the Work number here...
                            break;
                    }
                    phones.add(number);
                }
//                    mContact.setPhones(phones);
                cp.close();
            }
//            }
        }
        catch(Exception e) {
            Log.d("Error in Contacts Read:", "" + e.getMessage());
        }
        return phones;
    }
    ////////// get raw_contact_id of contact using contact id
    public static String getRawContactId(String contactId, Activity activity)
    {
        String res = "";
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.RawContacts._ID};
        String selection = ContactsContract.RawContacts.CONTACT_ID + " = ?";
        String[] selectionArgs = new String[]{ contactId };
        Cursor c = activity.getContentResolver().query(uri, projection, selection, selectionArgs, null);

        if(c != null && c.moveToFirst())
        {
            res = c.getString(c.getColumnIndex(ContactsContract.RawContacts._ID));
            c.close();
        }

        return res;
    }
    public static List<String> getEmailList(String id, Activity activity) {         ///// get email list from phone using contact id
        List<String> email_list = new ArrayList<>();
        Cursor emailCur = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
        if(emailCur.getCount() > 0) {
            List<String> emails = new ArrayList<>();
            while (emailCur.moveToNext()) {
                String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                email_list.add(email);
            }
            emailCur.close();
        }
        return email_list;
    }

    public static Bitmap getPhoto(long contactId, Activity activity) {          //// get contact photo from phone using contact id
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = activity.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return null;

    }

    public static Long getID(String number, Activity activity){
        Uri uri =  Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor c =  activity.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup._ID}, null, null, null);
        while(c.moveToNext()){
            return c.getLong(c.getColumnIndex(ContactsContract.PhoneLookup._ID));
        }
        return null;
    }

    public static void deleteContact(Activity activity, String contactId) {             /// delete contact from phone using contact id
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,contactId);
        int deleted = activity.getContentResolver().delete(uri,null,null);  /// if operation is success, return deleted > 0
    }

}
