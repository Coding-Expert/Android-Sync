package com.basicphones.sync.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.database.SyncDbSchema.TokenTable;
import com.basicphones.sync.database.SyncDbSchema.CheckSumTable;
import com.basicphones.sync.database.SyncDbSchema.PhoneTable;
import com.basicphones.sync.database.SyncDbSchema.EmailTable;
import com.basicphones.sync.database.SyncDbSchema.PersonTable;
import com.basicphones.sync.database.SyncDbSchema.CompanyTable;
import com.basicphones.sync.database.SyncDbSchema.AddressTable;
import com.basicphones.sync.database.SyncDbSchema.IMAddressTable;
import com.basicphones.sync.database.SyncDbSchema.EventTable;

public class SyncBaseHelper extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "syncBase.db";

    public SyncBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + TokenTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TokenTable.Cols.AccountNumber + ", " +
                TokenTable.Cols.Authentication +
                ")"
        );
        db.execSQL("create table " + CheckSumTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                CheckSumTable.Cols.CheckSum + ", " +
                CheckSumTable.Cols.UpdateSyncDate +
                ")"
        );
        db.execSQL("create table " + PersonTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                PersonTable.Cols.ID + ", " +
                PersonTable.Cols.UID + ", " +
                PersonTable.Cols.NAMEPREFIX + ", " +
                PersonTable.Cols.FIRSTNAME + ", " +
                PersonTable.Cols.MIDDLENAME + ", " +
                PersonTable.Cols.LASTNAME + ", " +
                PersonTable.Cols.NAMESUFFIX + ", " +
                PersonTable.Cols.FULLNAME + ", " +
                PersonTable.Cols.TRASH + ", " +
                PersonTable.Cols.SYNC + ", " +
                PersonTable.Cols.REMOVE +
                ")"
        );
        db.execSQL("create table " + PhoneTable.NAME + "(" +
                " _id integer primary key not null, " +
                PhoneTable.Cols.ID + ", " +
                PhoneTable.Cols.UID + ", " +
                PhoneTable.Cols.PHONE + ", " +
                PhoneTable.Cols.TYPE + ", " +
                "foreign key (" + PhoneTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                ")"
        );
        db.execSQL("create table " + EmailTable.NAME + "(" +
                " _id integer primary key not null, " +
                EmailTable.Cols.ID + ", " +
                EmailTable.Cols.UID + ", " +
                EmailTable.Cols.EMAIL + ", " +
                EmailTable.Cols.TYPE + ", " +
                "foreign key (" + EmailTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                ")"
        );
        db.execSQL("create table " + CompanyTable.NAME + "(" +
                " _id integer primary key not null, " +
                CompanyTable.Cols.ID + ", " +
                CompanyTable.Cols.UID + ", " +
                CompanyTable.Cols.ORGANIZATION + ", " +
                CompanyTable.Cols.TITLE + ", " +
                "foreign key (" + CompanyTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                ")"
        );
        db.execSQL("create table " + AddressTable.NAME + "(" +
                " _id integer primary key not null, " +
                AddressTable.Cols.ID + ", " +
                AddressTable.Cols.UID + ", " +
                AddressTable.Cols.ADDRESSNAME + ", " +
                AddressTable.Cols.ADDRESSTYPE + ", " +
                "foreign key (" + AddressTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                ")"
        );
        db.execSQL("create table " + IMAddressTable.NAME + "(" +
                " _id integer primary key not null, " +
                IMAddressTable.Cols.ID + ", " +
                IMAddressTable.Cols.UID + ", " +
                IMAddressTable.Cols.IMADDRESSNAME + ", " +
                IMAddressTable.Cols.IMADDRESSTYPE + ", " +
                "foreign key (" + IMAddressTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                ")"
        );
        db.execSQL("create table " + EventTable.NAME + "(" +
                " _id integer primary key not null, " +
                EventTable.Cols.ID + ", " +
                EventTable.Cols.UID + ", " +
                EventTable.Cols.EVENTDATE + ", " +
                EventTable.Cols.EVENTTYPE + ", " +
                "foreign key (" + EventTable.Cols.ID + ") " +
                "references " + PersonTable.NAME + "(" + PersonTable.Cols.ID + ") " +
                 ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
