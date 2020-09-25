package com.basicphones.sync;

import android.app.Person;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.database.SyncDbSchema;
import com.basicphones.sync.database.SyncDbSchema.TokenTable;
import com.basicphones.sync.database.SyncDbSchema.CheckSumTable;
import com.basicphones.sync.database.SyncDbSchema.PersonTable;
import com.basicphones.sync.database.SyncDbSchema.PhoneTable;
import com.basicphones.sync.database.SyncDbSchema.EmailTable;
import com.basicphones.sync.database.SyncDbSchema.CompanyTable;
import com.basicphones.sync.database.SyncDbSchema.AddressTable;
import com.basicphones.sync.database.SyncDbSchema.IMAddressTable;
import com.basicphones.sync.database.SyncDbSchema.EventTable;
import com.basicphones.sync.ContactModel.Phone;

public class SyncCursorWrapper extends CursorWrapper {

    public SyncCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getTokenAuthentication(){
        return getString(getColumnIndex(TokenTable.Cols.Authentication));
    }
    public String getAccountNumber(){
        return getString(getColumnIndex(TokenTable.Cols.AccountNumber));
    }
    public String getCheckSum(){
        return getString(getColumnIndex(CheckSumTable.Cols.CheckSum));
    }
    public String getSyncDate(){
        return getString(getColumnIndex(CheckSumTable.Cols.UpdateSyncDate));
    }

    public String getContactPersonID() {
        return getString(getColumnIndex(PersonTable.Cols.ID));
    }
    public String getContactPersonUID() {
        return getString(getColumnIndex(PersonTable.Cols.UID));
    }
    public String getContactPersonNamePrefix() {
        return getString(getColumnIndex(PersonTable.Cols.NAMEPREFIX));
    }
    public String getContactPersonFirstName(){
        return getString(getColumnIndex(PersonTable.Cols.FIRSTNAME));
    }
    public String getContactPersonMiddleName(){
        return getString(getColumnIndex(PersonTable.Cols.MIDDLENAME));
    }
    public String getContactPersonLastName(){
        return getString(getColumnIndex(PersonTable.Cols.LASTNAME));
    }
    public String getContactPersonNameSuffix(){
        return getString(getColumnIndex(PersonTable.Cols.NAMESUFFIX));
    }



    public String getContactPersonTrash() {
        return getString(getColumnIndex(PersonTable.Cols.TRASH));
    }
    public String getContactPersonSync(){
        return getString(getColumnIndex(PersonTable.Cols.SYNC));
    }
    public String getContactPersonRemove() {
        return getString(getColumnIndex(PersonTable.Cols.REMOVE));
    }
    public Phone getContactPhonePhone() {

        String number = getString(getColumnIndex(PhoneTable.Cols.PHONE));
        String type = getString(getColumnIndex(PhoneTable.Cols.TYPE));
        Phone contact_phone = new Phone(number, Integer.parseInt(type));
        return contact_phone;
    }
    public Email getContactEmailEmail() {
        String email = getString(getColumnIndex(EmailTable.Cols.EMAIL));
        String type = getString(getColumnIndex(EmailTable.Cols.TYPE));
        Email contact_email = new Email(email, Integer.parseInt(type));
        return contact_email;
    }
    public Company getContactCompany(){
        String organization = getString(getColumnIndex(CompanyTable.Cols.ORGANIZATION));
        String title = getString(getColumnIndex(CompanyTable.Cols.TITLE));
        Company contact_company = new Company(organization, title);
        return contact_company;
    }
    public Address getContactAddress(){
        String addressName = getString(getColumnIndex(AddressTable.Cols.ADDRESSNAME));
        String addressType = getString(getColumnIndex(AddressTable.Cols.ADDRESSTYPE));
        Address contact_address = new Address(addressName, Integer.parseInt(addressType));
        return contact_address;
    }
    public IMAddress getContactIMAddress() {
        String imaddressName = getString(getColumnIndex(IMAddressTable.Cols.IMADDRESSNAME));
        String imaddressType = getString(getColumnIndex(IMAddressTable.Cols.IMADDRESSTYPE));
        IMAddress contact_imaddress = new IMAddress(imaddressName, Integer.parseInt(imaddressType));
        return contact_imaddress;
    }
    public Event getContactEvent(){
        String date = getString(getColumnIndex(EventTable.Cols.EVENTDATE));
        String type = getString(getColumnIndex(EventTable.Cols.EVENTTYPE));
        Event event = new Event(date, Integer.parseInt(type));
        return event;
    }
}
