package com.basicphones.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.database.SyncBaseHelper;
import com.basicphones.sync.database.SyncDbSchema;
import com.basicphones.sync.database.SyncDbSchema.CheckSumTable;
import com.basicphones.sync.database.SyncDbSchema.TokenTable;
import com.basicphones.sync.database.SyncDbSchema.PersonTable;
import com.basicphones.sync.database.SyncDbSchema.EmailTable;
import com.basicphones.sync.database.SyncDbSchema.PhoneTable;
import com.basicphones.sync.ContactModel.Phone;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.database.SyncDbSchema.CompanyTable;
import com.basicphones.sync.database.SyncDbSchema.AddressTable;
import com.basicphones.sync.database.SyncDbSchema.IMAddressTable;
import com.basicphones.sync.database.SyncDbSchema.EventTable;
import com.basicphones.sync.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class SyncLab {

    private static SyncLab m_SyncLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static SyncLab get(Context context) {
        if (m_SyncLab == null) {
            m_SyncLab = new SyncLab(context);
        }
        return m_SyncLab;
    }

    private SyncLab(Context context)
    {
        mContext = context.getApplicationContext();
        mDatabase = new SyncBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addTokenInfo(Token token){
        ContentValues tokeninfoValues = getTokenInfoContentValues(token);
        mDatabase.insert(TokenTable.NAME, null, tokeninfoValues);
    }
    public void addCheckSum(SyncInfo syncInfo){
        ContentValues checksumValues = getCheckSumContentValues(syncInfo.getUpdate_checksum(), syncInfo.getUpdate_date());
        mDatabase.insert(CheckSumTable.NAME, null, checksumValues);
    }
    public void updateCheckSum(String sum, String date){
        ContentValues values = getCheckSumContentValues(sum, date);
        mDatabase.update(CheckSumTable.NAME, values, null, null);
    }
    public static ContentValues  getTokenInfoContentValues(Token token){
        ContentValues values = new ContentValues();
        values.put(TokenTable.Cols.AccountNumber, token.getAccount_Number());
        values.put(TokenTable.Cols.Authentication, token.getAuthentication());
        return values;
    }
    public static ContentValues getCheckSumContentValues(String sum, String date){
        ContentValues values = new ContentValues();
        values.put(CheckSumTable.Cols.CheckSum, sum);
        values.put(CheckSumTable.Cols.UpdateSyncDate, date);
        return values;
    }

    public Token getTokenInfo(){
        Token m_token = new Token();
        SyncCursorWrapper syncCursor = queryData(TokenTable.NAME,null, null);
        try{
            syncCursor.moveToFirst();
            while (!syncCursor.isAfterLast()) {
                String number = syncCursor.getAccountNumber();
                String authentication = syncCursor.getTokenAuthentication();
                m_token.setAccount_Number(number);
                m_token.setAuthentication(authentication);
                syncCursor.moveToNext();
            }
        }finally {
            syncCursor.close();
        }
        return m_token;
    }
    public SyncInfo getSyncInfo(){
        SyncInfo m_syncInfo = new SyncInfo();
        SyncCursorWrapper syncCursor = queryData(CheckSumTable.NAME, null, null);
        try{
            syncCursor.moveToFirst();
            while(!syncCursor.isAfterLast()){
                String checksum = syncCursor.getCheckSum();
                String syncdate = syncCursor.getSyncDate();
                m_syncInfo.setUpdate_checksum(checksum);
                m_syncInfo.setUpdate_date(syncdate);
                syncCursor.moveToNext();
            }
        }finally {

        }
        return m_syncInfo;
    }

    public void addContact(ContactInfo c)
    {
        ContentValues personValues = getPersonContentValues(c);
        List<ContentValues> phoneValues = getPhoneContentValues(c);
        List<ContentValues> emailValues = getEmailContentValues(c);
        ContentValues companyValues = getCompanyContentValues(c);
        List<ContentValues> addressValues = getAddressContentValues(c);
        List<ContentValues> imaddressValues = getIMAddressContentValues(c);
        List<ContentValues> eventValues = getEventContentValues(c);

        mDatabase.insert(PersonTable.NAME, null, personValues);
        for(int i=0; i!=phoneValues.size();i++) {
            mDatabase.insert(PhoneTable.NAME, null, phoneValues.get(i));
        }
        for(int i=0; i!=emailValues.size();i++) {
            mDatabase.insert(EmailTable.NAME, null, emailValues.get(i));
        }
        mDatabase.insert(CompanyTable.NAME, null, companyValues);
        for(int i = 0; i < addressValues.size(); i++){
            mDatabase.insert(AddressTable.NAME, null, addressValues.get(i));
        }
        for(int i = 0; i < imaddressValues.size(); i++){
            mDatabase.insert(IMAddressTable.NAME, null, imaddressValues.get(i));
        }
        for(int i = 0; i < eventValues.size(); i++){
            mDatabase.insert(EventTable.NAME, null, eventValues.get(i));
        }
    }
    public void updateContact(ContactInfo contact) {
        String idString = contact.getId();
        String uidString = contact.getUid();

        /** --------------------- name --------------------------------- */
        //update name
        ContentValues values = getPersonContentValues(contact);
        if(!idString.isEmpty() && !idString.equals("")){
            mDatabase.update(PersonTable.NAME, values,
                    PersonTable.Cols.ID + " = ?",
                    new String[] { idString });
        }
        else{
            mDatabase.update(PersonTable.NAME, values,
                    PersonTable.Cols.UID + " = ?",
                    new String[] { uidString });
        }

        /** ---------------------------- phone -------------------------- */
        Phone temp_phone;

        //update phones
        List<Phone> newPhones= new ArrayList<>();
        List<Phone> databasePhones = getPhones(idString, uidString);
        List<Phone> contactPhones = contact.getPhones();

//        check for blank and null phones
        for(int i=0; i!=contactPhones.size();i++) {
            temp_phone = contactPhones.get(i);
            if(temp_phone != null) {
                newPhones.add(temp_phone);
            }

        }

        //check if newPhones and databasePhones are different and update
        if(databasePhones != null && (databasePhones.size() == newPhones.size())){
            databasePhones.removeAll(newPhones);
            if(!databasePhones.isEmpty()) { //both lists not same.
                if(uidString == null || uidString.isEmpty() || uidString.equals("")) {
                    mDatabase.delete(PhoneTable.NAME,
                            PhoneTable.Cols.ID + " = ?",
                            new String[]{idString});
                }
                else{
                    mDatabase.delete(PhoneTable.NAME,
                            PhoneTable.Cols.UID + " = ?",
                            new String[]{uidString});
                }
                contact.setPhones(newPhones);
                List<ContentValues> phoneValues = getPhoneContentValues(contact);
                for(int i=0; i!=phoneValues.size();i++)
                    mDatabase.insert(PhoneTable.NAME, null, phoneValues.get(i));
            }
        }
        else if (databasePhones != null && (databasePhones.size() != newPhones.size())){
            if(!idString.isEmpty() && !idString.equals("")){
                mDatabase.delete(PhoneTable.NAME,
                        PhoneTable.Cols.ID + " = ?",
                        new String[] { idString });
            }
            else{
                mDatabase.delete(PhoneTable.NAME,
                        PhoneTable.Cols.UID + " = ?",
                        new String[] { uidString });
            }

            contact.setPhones(newPhones);
            List<ContentValues> phoneValues = getPhoneContentValues(contact);

            for(int i=0; i!=phoneValues.size();i++)
                mDatabase.insert(PhoneTable.NAME, null, phoneValues.get(i));
        }

        /** --------------------------------- email -------------------------- */
        //update emails
        Email temp_email;
        List<Email> newEmails= new ArrayList<>();
        List<Email> databaseEmails = getEmails(idString, uidString);
        List<Email> contactEmails = contact.getEmails();
//        Log.d("allonsy.contacts", "'Emails Size " + contactEmails.size() + "'");

//        check for blank and null emails
        for(int i=0; i!=contactEmails.size();i++) {
            temp_email = contactEmails.get(i);
            if(temp_email != null)
                newEmails.add(temp_email);
        }

        //check if newEmails and databaseEmails are different and update
        if(databaseEmails != null && (databaseEmails.size() == newEmails.size())){
            databaseEmails.removeAll(newEmails);
            if(!databaseEmails.isEmpty()) { //both lists not same.
                if(!idString.isEmpty() || !idString.equals("")){
                    mDatabase.delete(EmailTable.NAME,
                            EmailTable.Cols.ID + " = ?",
                            new String[] { idString });
                }
                else{
                    mDatabase.delete(EmailTable.NAME,
                            EmailTable.Cols.UID + " = ?",
                            new String[] { uidString });
                }

                contact.setEmails(newEmails);
                List<ContentValues> emailValues = getEmailContentValues(contact);
                for(int i=0; i!=emailValues.size();i++)
                    mDatabase.insert(EmailTable.NAME, null, emailValues.get(i));
            }
        }
        else if(databaseEmails != null && (databaseEmails.size() != newEmails.size())){
            if(!idString.isEmpty() || !idString.equals("")){
                mDatabase.delete(EmailTable.NAME,
                        EmailTable.Cols.ID + " = ?",
                        new String[] { idString });
            }
            else{
                mDatabase.delete(EmailTable.NAME,
                        EmailTable.Cols.UID + " = ?",
                        new String[] { uidString });
            }

            contact.setEmails(newEmails);
            List<ContentValues> emailValues = getEmailContentValues(contact);
            for(int i=0; i!=emailValues.size();i++)
                mDatabase.insert(EmailTable.NAME, null, emailValues.get(i));
        }
        /** -------------------------------company -------------------------- */

        ContentValues companyValues = getCompanyContentValues(contact);
        if(!idString.isEmpty() && !idString.equals("")){
            mDatabase.update(CompanyTable.NAME, companyValues,
                    CompanyTable.Cols.ID + " = ?",
                    new String[] { idString });
        }
        else{
            mDatabase.update(CompanyTable.NAME, companyValues,
                    CompanyTable.Cols.UID + " = ?",
                    new String[] { uidString });
        }
        /** ------------------------------address --------------------------- */

        Address temp_address;
        //update address
        List<Address> newAddresses= new ArrayList<>();
        List<Address> databaseAddresses = getAddresses(idString, uidString);
        List<Address> contactAddresses = contact.getAddressList();

//        check for blank and null address
        for(int i=0; i!=contactAddresses.size();i++) {
            temp_address = contactAddresses.get(i);
            if(temp_address != null) {
                newAddresses.add(temp_address);
            }

        }

        //check if newAddresses and databaseAddresses are different and update
        if(databaseAddresses != null && (databaseAddresses.size() == newAddresses.size())){
            databaseAddresses.removeAll(newAddresses);
            if(!databaseAddresses.isEmpty()) { //both lists not same.
                if(!idString.isEmpty() || !idString.equals("")) {
                    mDatabase.delete(AddressTable.NAME,
                            AddressTable.Cols.ID + " = ?",
                            new String[]{idString});
                }
                else{
                    mDatabase.delete(AddressTable.NAME,
                            AddressTable.Cols.UID + " = ?",
                            new String[]{uidString});
                }
                contact.setAddressList(newAddresses);
                List<ContentValues> addressValues = getAddressContentValues(contact);
                for(int i=0; i!=addressValues.size();i++)
                    mDatabase.insert(AddressTable.NAME, null, addressValues.get(i));
            }
        }
        else if (databaseAddresses != null && (databaseAddresses.size() != newAddresses.size())){
            if(!idString.isEmpty() && !idString.equals("")){
                mDatabase.delete(AddressTable.NAME,
                        AddressTable.Cols.ID + " = ?",
                        new String[] { idString });
            }
            else{
                mDatabase.delete(AddressTable.NAME,
                        AddressTable.Cols.UID + " = ?",
                        new String[] { uidString });
            }

            contact.setAddressList(newAddresses);
            List<ContentValues> addressValues = getAddressContentValues(contact);

            for(int i=0; i!=addressValues.size();i++)
                mDatabase.insert(AddressTable.NAME, null, addressValues.get(i));
        }
        /** ------------------------------ IMAddress --------------------------- */

        IMAddress temp_imaddress;
        //update imaddress
        List<IMAddress> newIMAddresses= new ArrayList<>();
        List<IMAddress> databaseIMAddresses = getIMAddresses(idString, uidString);
        List<IMAddress> contactIMAddresses = contact.getImAddressesList();

//        check for blank and null imaddress
        for(int i=0; i!=contactIMAddresses.size();i++) {
            temp_imaddress = contactIMAddresses.get(i);
            if(temp_imaddress != null) {
                newIMAddresses.add(temp_imaddress);
            }

        }

        //check if newIMAddresses and databaseIMAddresses are different and update
        if(databaseIMAddresses != null && (databaseIMAddresses.size() == newIMAddresses.size())){
            databaseIMAddresses.removeAll(newIMAddresses);
            if(!databaseIMAddresses.isEmpty()) { //both lists not same.
                if(!idString.isEmpty() || !idString.equals("")) {
                    mDatabase.delete(IMAddressTable.NAME,
                            IMAddressTable.Cols.ID + " = ?",
                            new String[]{idString});
                }
                else{
                    mDatabase.delete(IMAddressTable.NAME,
                            IMAddressTable.Cols.UID + " = ?",
                            new String[]{uidString});
                }
                contact.setImAddressesList(newIMAddresses);
                List<ContentValues> imaddressValues = getIMAddressContentValues(contact);
                for(int i=0; i!=imaddressValues.size();i++)
                    mDatabase.insert(IMAddressTable.NAME, null, imaddressValues.get(i));
            }
        }
        else if (databaseIMAddresses != null && (databaseIMAddresses.size() != newIMAddresses.size())){
            if(!idString.isEmpty() && !idString.equals("")){
                mDatabase.delete(IMAddressTable.NAME,
                        IMAddressTable.Cols.ID + " = ?",
                        new String[] { idString });
            }
            else{
                mDatabase.delete(IMAddressTable.NAME,
                        IMAddressTable.Cols.UID + " = ?",
                        new String[] { uidString });
            }

            contact.setImAddressesList(newIMAddresses);
            List<ContentValues> imaddressValues = getIMAddressContentValues(contact);

            for(int i=0; i!=imaddressValues.size();i++)
                mDatabase.insert(IMAddressTable.NAME, null, imaddressValues.get(i));
        }
        /** --------------------------- event ------------------------------- */
        Event temp_event;
        //update event
        List<Event> newEvents= new ArrayList<>();
        List<Event> databaseEvents = getEvents(idString, uidString);
        List<Event> contactEvents = contact.getEvents();

//        check for blank and null event
        for(int i=0; i!=contactEvents.size();i++) {
            temp_event = contactEvents.get(i);
            if(temp_event != null) {
                newEvents.add(temp_event);
            }

        }

        //check if newEvents and databaseEvents are different and update
        if(databaseEvents != null && (databaseEvents.size() == newEvents.size())){
            databaseEvents.removeAll(newEvents);
            if(!databaseEvents.isEmpty()) { //both lists not same.
                if(!idString.isEmpty() || !idString.equals("")) {
                    mDatabase.delete(EventTable.NAME,
                            EventTable.Cols.ID + " = ?",
                            new String[]{idString});
                }
                else{
                    mDatabase.delete(EventTable.NAME,
                            EventTable.Cols.UID + " = ?",
                            new String[]{uidString});
                }
                contact.setEvents(newEvents);
                List<ContentValues> eventValues = getEventContentValues(contact);
                for(int i=0; i!=eventValues.size();i++)
                    mDatabase.insert(EventTable.NAME, null, eventValues.get(i));
            }
        }
        else if (databaseEvents != null && (databaseEvents.size() != newEvents.size())){
            if(!idString.isEmpty() && !idString.equals("")){
                mDatabase.delete(EventTable.NAME,
                        EventTable.Cols.ID + " = ?",
                        new String[] { idString });
            }
            else{
                mDatabase.delete(EventTable.NAME,
                        EventTable.Cols.UID + " = ?",
                        new String[] { uidString });
            }

            contact.setEvents(newEvents);
            List<ContentValues> eventValues = getEventContentValues(contact);

            for(int i=0; i!=eventValues.size();i++)
                mDatabase.insert(EventTable.NAME, null, eventValues.get(i));
        }
    }
    public void deleteContact(ContactInfo contact) {
        String idString = contact.getId();
        String uidString = contact.getUid();

        //delete contact
        if(!idString.isEmpty() && !idString.equals("")) {
            mDatabase.delete(PersonTable.NAME,
                    PersonTable.Cols.ID + " = ?",
                    new String[]{idString});
            mDatabase.delete(PhoneTable.NAME,
                    PhoneTable.Cols.ID + " = ?",
                    new String[] { idString });
            mDatabase.delete(EmailTable.NAME,
                    EmailTable.Cols.ID + " = ?",
                    new String[] { idString });
            mDatabase.delete(CompanyTable.NAME,
                    CompanyTable.Cols.ID + " = ?",
                    new String[] { idString });
            mDatabase.delete(AddressTable.NAME,
                    AddressTable.Cols.ID + " = ?",
                    new String[] { idString });
            mDatabase.delete(IMAddressTable.NAME,
                    IMAddressTable.Cols.ID + " = ?",
                    new String[] { idString });
            mDatabase.delete(EventTable.NAME,
                    EventTable.Cols.ID + " = ?",
                    new String[] { idString });
        }
        else{
            mDatabase.delete(PersonTable.NAME,
                    PersonTable.Cols.UID + " = ?",
                    new String[]{uidString});
            mDatabase.delete(PhoneTable.NAME,
                    PhoneTable.Cols.UID + " = ?",
                    new String[] { uidString });
            mDatabase.delete(EmailTable.NAME,
                    EmailTable.Cols.UID + " = ?",
                    new String[] { uidString });
            mDatabase.delete(CompanyTable.NAME,
                    CompanyTable.Cols.UID + " = ?",
                    new String[] { uidString });
            mDatabase.delete(AddressTable.NAME,
                    AddressTable.Cols.UID + " = ?",
                    new String[] { uidString });
            mDatabase.delete(IMAddressTable.NAME,
                    IMAddressTable.Cols.UID + " = ?",
                    new String[] { uidString });
            mDatabase.delete(EventTable.NAME,
                    EventTable.Cols.UID + " = ?",
                    new String[] { uidString });
        }

    }
    public List<Phone> getPhones(String id, String uid)
    {
        List<Phone> phones = new ArrayList<>();
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")) {
            cursor = queryData(
                    PhoneTable.NAME,
                    PhoneTable.Cols.ID + " = ?",
                    new String[]{id}
            );
        }
        else{
            cursor = queryData(
                    PhoneTable.NAME,
                    PhoneTable.Cols.UID + " = ?",
                    new String[]{uid}
            );
        }
        try {
            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                phones.add(cursor.getContactPhonePhone());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return phones;
    }
    public List<Email> getEmails(String id, String uid)
    {
        List<Email> emails = new ArrayList<>();
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")){
            cursor = queryData(
                    EmailTable.NAME,
                    EmailTable.Cols.ID + " = ?",
                    new String[] { id });
        }
        else{
            cursor = queryData(
                    EmailTable.NAME,
                    EmailTable.Cols.UID + " = ?",
                    new String[] { uid });
        }
        try {
            if (cursor.getCount() == 0) {
                return new ArrayList<>();
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getContactEmailEmail());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return emails;
    }
    public Company getCompany(String id, String uid){
        Company contact_company = null;
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")){
            cursor = queryData(
                    CompanyTable.NAME,
                    CompanyTable.Cols.ID + " = ?",
                    new String[] { id });
        }
        else{
            cursor = queryData(
                    CompanyTable.NAME,
                    CompanyTable.Cols.UID + " = ?",
                    new String[] { uid });
        }
        try {
            if (cursor.getCount() == 0) {
                return contact_company;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                contact_company = cursor.getContactCompany();
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return contact_company;
    }
    public List<Address> getAddresses(String id, String uid) {
        List<Address> addresses = new ArrayList<>();
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")){
            cursor = queryData(
                    AddressTable.NAME,
                    AddressTable.Cols.ID + " = ?",
                    new String[] { id });
        }
        else{
            cursor = queryData(
                    AddressTable.NAME,
                    AddressTable.Cols.UID + " = ?",
                    new String[] { uid });
        }
        try {
            if (cursor.getCount() == 0) {
                return addresses;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                addresses.add(cursor.getContactAddress());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return addresses;
    }
    public List<IMAddress> getIMAddresses(String id, String uid) {
        List<IMAddress> imaddresses = new ArrayList<>();
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")){
            cursor = queryData(
                    IMAddressTable.NAME,
                    IMAddressTable.Cols.ID + " = ?",
                    new String[] { id });
        }
        else{
            cursor = queryData(
                    IMAddressTable.NAME,
                    IMAddressTable.Cols.UID + " = ?",
                    new String[] { uid });
        }
        try {
            if (cursor.getCount() == 0) {
                return imaddresses;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                imaddresses.add(cursor.getContactIMAddress());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return imaddresses;
    }
    public List<Event> getEvents(String id, String uid) {
        List<Event> events = new ArrayList<>();
        SyncCursorWrapper cursor = null;
        if(!id.isEmpty() && !id.equals("")){
            cursor = queryData(
                    EventTable.NAME,
                    EventTable.Cols.ID + " = ?",
                    new String[] { id });
        }
        else{
            cursor = queryData(
                    EventTable.NAME,
                    EventTable.Cols.UID + " = ?",
                    new String[] { uid });
        }
        try {
            if (cursor.getCount() == 0) {
                return events;
            }
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                events.add(cursor.getContactEvent());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return events;
    }
    public List<ContactInfo> getContacts()
    {
        List<ContactInfo> contacts = new ArrayList<>();
        SyncCursorWrapper personCursor = queryData(PersonTable.NAME,null, null);
        try {
            personCursor.moveToFirst();
            while (!personCursor.isAfterLast()) {
                String id = personCursor.getContactPersonID();
                String uid = personCursor.getContactPersonUID();
                String nameprefix = personCursor.getContactPersonNamePrefix();
                String firstname = personCursor.getContactPersonFirstName();
                String middlename = personCursor.getContactPersonMiddleName();
                String lastname = personCursor.getContactPersonLastName();
                String namesuffix = personCursor.getContactPersonNameSuffix();
                String fullname = firstname + " " + lastname;
                String trash = personCursor.getContactPersonTrash();
                String sync = personCursor.getContactPersonSync();
                String remove = personCursor.getContactPersonRemove();
                List<Phone> phones = getPhones(id, uid);
                List<Email> emails = getEmails(id, uid);
                Company company = getCompany(id, uid);
                List<Address> addresses = getAddresses(id, uid);
                List<IMAddress> imAddresses = getIMAddresses(id, uid);
                List<Event> events = getEvents(id, uid);

                ContactInfo contact = new ContactInfo(id);
                contact.setUid(uid);
                contact.setNamePrefix(nameprefix);
                contact.setFirstName(firstname);
                contact.setMiddleName(middlename);
                contact.setLastName(lastname);
                contact.setNameSuffix(namesuffix);
                contact.setFullName(fullname);
                contact.setPhones(phones);
                contact.setEmails(emails);
                contact.setmTrash(trash);
                contact.setmSync(sync);
                contact.setmRemove(remove);
                contact.setCompany(company);
                contact.setAddressList(addresses);
                contact.setImAddressesList(imAddresses);
                contact.setEvents(events);

                contacts.add(contact);
                personCursor.moveToNext();
            }
        } finally {
            personCursor.close();
        }
        return contacts;
    }
//    public List<ContactInfo> searchContactsByName(String search)
//    {
//        List<ContactInfo> contacts = new ArrayList<>();
//        SyncCursorWrapper personCursor = queryData(PersonTable.NAME, PersonTable.Cols.NAMEPREFIX + " LIKE ?",
//                new String[] { "%" + search + "%" });
//        try {
//            personCursor.moveToFirst();
//            while (!personCursor.isAfterLast()) {
//                String id = personCursor.getContactPersonID();
//                String uid= personCursor.getContactPersonUID();
//                String name = personCursor.getContactPersonName();
//                List<String> phones = getPhones(id, uid);
//                List<String> emails = getEmails(id, uid);
//                String trash = personCursor.getContactPersonTrash();
//                String sync = personCursor.getContactPersonSync();
//                String remove = personCursor.getContactPersonRemove();
//
//                ContactInfo contact = new ContactInfo(id);
//                contact.setUid(uid);
////                contact.setName(name);
////                contact.setPhones(phones);
////                contact.setEmails(emails);
//                contact.setmTrash(trash);
//                contact.setmSync(sync);
//                contact.setmRemove(remove);
//
//                contacts.add(contact);
//                personCursor.moveToNext();
//            }
//        } finally {
//            personCursor.close();
//        }
//        return contacts;
//    }
    private static ContentValues getPersonContentValues(ContactInfo contact) {
        ContentValues values = new ContentValues();
        values.put(PersonTable.Cols.ID, contact.getId());
        values.put(PersonTable.Cols.UID, contact.getUid());
        values.put(PersonTable.Cols.NAMEPREFIX, contact.getNamePrefix());
        values.put(PersonTable.Cols.FIRSTNAME, contact.getFirstName());
        values.put(PersonTable.Cols.MIDDLENAME, contact.getMiddleName());
        values.put(PersonTable.Cols.LASTNAME, contact.getLastName());
        values.put(PersonTable.Cols.NAMESUFFIX, contact.getNameSuffix());
        values.put(PersonTable.Cols.FULLNAME, contact.getFirstName() + " " + contact.getLastName());
        values.put(PersonTable.Cols.TRASH, contact.getmTrash());
        values.put(PersonTable.Cols.SYNC, contact.getmSync());
        values.put(PersonTable.Cols.REMOVE, contact.getmRemove());
        return values;
    }
    private static List<ContentValues> getPhoneContentValues(ContactInfo contact) {

        List<ContentValues>  values =  new ArrayList<>();
        List<Phone> phones = contact.getPhones();

        for(int i=0; i!=phones.size();i++) {
            ContentValues value = new ContentValues();
            value.put(PhoneTable.Cols.ID, contact.getId());
            value.put(PhoneTable.Cols.UID, contact.getUid());
            value.put(PhoneTable.Cols.PHONE, phones.get(i).getNumber());
            value.put(PhoneTable.Cols.TYPE, phones.get(i).getType());
            values.add(value);
        }

        return values;
    }

    private static List<ContentValues> getEmailContentValues(ContactInfo contact) {

        List<ContentValues>  values =  new ArrayList<>();
        List<Email> emails = contact.getEmails();

        for(int i=0; i!=emails.size();i++) {
            ContentValues value = new ContentValues();
            value.put(EmailTable.Cols.ID, contact.getId());
            value.put(EmailTable.Cols.UID, contact.getUid());
            value.put(EmailTable.Cols.EMAIL, emails.get(i).getEmail());
            value.put(EmailTable.Cols.TYPE, emails.get(i).getType());
            values.add(value);
        }

        return values;
    }
    private static ContentValues getCompanyContentValues(ContactInfo contact){
        ContentValues values = new ContentValues();
        values.put(CompanyTable.Cols.ID, contact.getId());
        values.put(CompanyTable.Cols.UID, contact.getUid());
        values.put(CompanyTable.Cols.ORGANIZATION, contact.getCompany().getOrganization());
        values.put(CompanyTable.Cols.TITLE, contact.getCompany().getTitle());
        return values;
    }
    private static List<ContentValues> getAddressContentValues(ContactInfo contact){
        List<ContentValues> values = new ArrayList<>();
        List<Address> addresses = contact.getAddressList();
        for(int i = 0; i != addresses.size(); i++){
            ContentValues value = new ContentValues();
            value.put(AddressTable.Cols.ID, contact.getId());
            value.put(AddressTable.Cols.UID, contact.getUid());
            value.put(AddressTable.Cols.ADDRESSNAME, addresses.get(i).getAddressName());
            value.put(AddressTable.Cols.ADDRESSTYPE, addresses.get(i).getAddressType());
            values.add(value);
        }
        return values;
    }
    private static List<ContentValues> getIMAddressContentValues(ContactInfo contact){
        List<ContentValues> values = new ArrayList<>();
        List<IMAddress> imAddresses = contact.getImAddressesList();
        for(int i = 0; i < imAddresses.size(); i++){
            ContentValues value = new ContentValues();
            value.put(IMAddressTable.Cols.ID, contact.getId());
            value.put(IMAddressTable.Cols.UID, contact.getUid());
            value.put(IMAddressTable.Cols.IMADDRESSNAME, imAddresses.get(i).getImaddressName());
            value.put(IMAddressTable.Cols.IMADDRESSTYPE, imAddresses.get(i).getImaddressType());
            values.add(value);
        }
        return values;
    }
    private static List<ContentValues> getEventContentValues(ContactInfo contact) {
        List<ContentValues> values = new ArrayList<>();
        List<Event> events = contact.getEvents();
        for(int i = 0; i < events.size(); i++){
            ContentValues value = new ContentValues();
            value.put(EventTable.Cols.ID, contact.getId());
            value.put(EventTable.Cols.UID, contact.getUid());
            value.put(EventTable.Cols.EVENTDATE, events.get(i).getEventDate());
            value.put(EventTable.Cols.EVENTTYPE, events.get(i).getEventType());
            values.add(value);
        }
        return values;
    }

    private SyncCursorWrapper queryData(String table, String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                table,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new SyncCursorWrapper(cursor);
    }
}
