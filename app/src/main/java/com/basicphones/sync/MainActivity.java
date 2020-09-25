package com.basicphones.sync;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.ContactModel.Phone;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import jagerfield.mobilecontactslibrary.Contact.Contact;
import jagerfield.mobilecontactslibrary.ElementContainers.AddressContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.EmailContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.EventContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.IMContainer;
import jagerfield.mobilecontactslibrary.ElementContainers.NumberContainer;
import jagerfield.mobilecontactslibrary.ImportContactsAsync;

public class MainActivity extends AppCompatActivity {

    private Button buttonSync;
    private Button buttonTrash;
    private TextView txtAbout;
    private TextView txtExit;
    private Switch enableAutoSync;
    public int server_checksum = 0;
    public List<ContactInfo> server_contactList = new ArrayList<>();
    public List<ContactInfo> local_contactList = new ArrayList<>();
    public List<String> local_contact_UIDList = new ArrayList<>();
    public List<String> local_contact_IDList = new ArrayList<>();
    public List<String> server_contact_UIDList = new ArrayList<>();
    public List<String> server_contact_IDList = new ArrayList<>();
    public List<ContactInfo> upload_contactList = new ArrayList<>();
    public Token tokenInfo;
    public ProgressDialog syncDialog;
    public List<ContactInfo> response_uploadContactList = new ArrayList<>();
    public List<ContactInfo> delete_contactList = new ArrayList<>();
    public List<ContactInfo> trash_contactList = new ArrayList<>();
    private TextView syncStatusView;
    public List<ContactInfo> realContactList = new ArrayList<>();
    public boolean localdb_flag = false;    // check that local db blanked

    private static final int READ_CONTACT_REQUEST_CODE = 100;
    private static final int WRITE_CONTACT_REQUEST_CODE = 101;

    public int mInterval = 24 * 60 * 60 * 1000; // 24 hour by default
//    public int mInterval = 10000;
    private Handler mHandler;
    public boolean autoSync_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO - If no CommonThread Token is stored, show the Login activity to allow the user to log into either an existing or new account

        buttonSync = findViewById(R.id.buttonSync);
        buttonSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TODO - Run a ContactInfo sync with the server in the background
                syncContact();
            }
        });

        buttonTrash = findViewById(R.id.buttonTrash);
        buttonTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Trash.class);
                //myIntent.putExtra("key", value); //Optional parameters
                startActivity(myIntent);
            }
        });

        txtAbout = findViewById(R.id.textAbout);
        txtAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(v.getContext(), About.class);
                //myIntent.putExtra("key", value); //Optional parameters
                v.getContext().startActivity(myIntent);
            }
        });

        txtExit = findViewById(R.id.textExit);
        txtExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mHandler = new Handler();
        enableAutoSync = findViewById(R.id.switchAutoSync);
        enableAutoSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    autoSync_flag = true;
                    startRepeatingAutoSync();
                } else {
                    autoSync_flag = false;
                    stopRepeatingAutoSync();
                }
            }
        });
        syncStatusView = findViewById(R.id.textStatus);
        tokenInfo = SyncLab.get(getApplicationContext()).getTokenInfo();
        checkPermission();
    }

    Runnable autosync_runnable = new Runnable() {
        @Override
        public void run() {
            try {
                autoSync();
            } finally {
                mHandler.postDelayed(autosync_runnable, mInterval);
            }
        }
    };
    public void startRepeatingAutoSync() {
//        autosync_runnable.run();
        mHandler.postDelayed(autosync_runnable, mInterval);
    }

    public void stopRepeatingAutoSync() {
        mHandler.removeCallbacks(autosync_runnable);
    }
    public void autoSync(){
        syncContact();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingAutoSync();
    }
    /** Sync local DEVICE contacts with contacts in SERVER database */
    public void syncContact(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
            if(autoSync_flag){
                stopRepeatingAutoSync();
                mInterval = 1 * 60 * 60 * 1000;
                startRepeatingAutoSync();
            }
            else {
                Toast.makeText(getApplicationContext(), "There is no connection", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        initContactArray();

        if(tokenInfo.getAuthentication() == null || tokenInfo.getAuthentication().isEmpty() || tokenInfo.getAuthentication().length() < 0){
            Toast.makeText(getApplicationContext(), "Authentication Info is not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    syncDialog = ProgressDialog.show(MainActivity.this, "", "synchronizing");
                }
            };
            mainHandler.post(myRunnable);
            /** get checksum from server */
            ServerUtils.getCheckSumFromServer(tokenInfo.getAuthentication(), MainActivity.this);
        }

    }


    public void compareLocalCheckSumWithServer() throws UnsupportedEncodingException, JSONException {
        local_contactList = SyncLab.get(getApplicationContext()).getContacts();
        local_contact_UIDList = getLocal_Contact_UIDList();
        local_contact_IDList = getLocal_Contact_IDList();
        getDeleteContactList();
//        if(server_checksum == -1){
//            Toast.makeText(getApplicationContext(), "Synchronize operation failed", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(getCheckSumFromLocal() == -1) {
            if(trash_contactList.size() > 0){
                ServerUtils.trashContacts(MainActivity.this, trash_contactList, tokenInfo.getAuthentication());
            }
            else{
                getAllContactFromServer();
            }

        }
        else{
            if (server_checksum == getCheckSumFromLocal()) {    /** if 2 checksums is same */
                uploadCheckSum();
                return;
            }
            if (server_checksum != getCheckSumFromLocal()) {    /** if 2 checksums is different */
                /** get all contact list from server */
                if (trash_contactList.size() > 0) {
                    ServerUtils.trashContacts(MainActivity.this, trash_contactList, tokenInfo.getAuthentication());
                } else {
                    getAllContactFromServer();
                }
            }
        }
    }
    public void getAllContactFromServer(){
        ServerUtils.getAllContactListFromServer(tokenInfo.getAuthentication(), MainActivity.this);
    }

    public void syncProcess1() throws JSONException, UnsupportedEncodingException {
        server_contact_IDList = getServer_contact_IDList();
        server_contact_UIDList = getServer_contact_UIDList();
        if(server_contactList.size() > 0){
            for(int i = 0; i < server_contactList.size(); i++){
                ContactInfo s_contact = server_contactList.get(i);
                if(local_contact_UIDList.size() == 0 ){    /** If a downloaded contact from server does not exist in the local DEVICE storage */
                    if(s_contact.getmTrash().equals("trash")){              /** If a downloaded contact is marked as Trash, and it does not already exist in the CLIENT’s Trash database */
                        SyncLab.get(getApplicationContext()).addContact(s_contact);
                    }
                    else{
                        int contactId = ContactUtils.addContactInPhone(s_contact, MainActivity.this);
                        s_contact.setId(String.valueOf(contactId));
                        SyncLab.get(getApplicationContext()).addContact(s_contact);
                    }
                }
                else {
                    if (local_contact_UIDList.contains(s_contact.getUid())) {          /** If a downloaded contact from server does exist in the local DEVICE storage */
                        ContactInfo local_contact = getContactInfoByUID(s_contact.getUid());

                        if (s_contact.getmTrash().equals("trash") && !local_contact.getmTrash().equals("trash")) {
                            local_contact.setmTrash("trash");
                            SyncLab.get(getApplicationContext()).updateContact(local_contact);
                            ContactUtils.deleteContact(MainActivity.this, local_contact.getId());

                        }
                        if(!s_contact.getmTrash().equals("trash") && local_contact.getmTrash().equals("trash")){        // if server contact have restored in server trash
                            SyncLab.get(getApplicationContext()).deleteContact(local_contact);
                            int difference = 0;
                            for(int j = 0; j < local_contactList.size(); j++){
                                if(!local_contactList.get(j).getmTrash().equals("trash")) {
                                    ContactInfo local_cmpContact = local_contactList.get(j);
                                    difference = compareContactData(s_contact, local_cmpContact);
                                    if (difference == 1) {                                                            // if restored contact from server is exist on phone
                                        ContactUtils.deleteContact(MainActivity.this, local_cmpContact.getId());
                                        local_cmpContact.setmTrash("trash");
                                        SyncLab.get(getApplicationContext()).deleteContact(local_cmpContact);
                                        SyncLab.get(getApplicationContext()).addContact(local_cmpContact);
                                    }
                                }
                            }
                            local_contact.setmTrash("untrash");
                            int contactId = ContactUtils.addContactInPhone(local_contact, MainActivity.this);
                            local_contact.setId(String.valueOf(contactId));
                            SyncLab.get(getApplicationContext()).addContact(local_contact);
                        }
                    } else {
                        if(localdb_flag) {                          // if local db is clear, search updated contacts, new contacts by comparing server contact from device
                            List<ContactInfo> c_ContactList = new ArrayList<>();
                            if (local_contactList.size() > 0) {
                                for (int j = 0; j < local_contactList.size(); j++) {
                                    ContactInfo m_contact = local_contactList.get(j);
                                    if (m_contact.getUid().equals("") && !m_contact.getmTrash().equals("trash")) {
                                        c_ContactList.add(m_contact);
                                    }
                                }
                            }
                            List<ContactInfo> s_ContactList = new ArrayList<>();
                            for (int j = 0; j < server_contactList.size(); j++) {
                                ContactInfo m_contact = server_contactList.get(j);
                                if (!m_contact.getmTrash().equals("trash")) {
                                    s_ContactList.add(m_contact);
                                }
                            }
                            if (c_ContactList.size() > 0) {
                                int difference = 0;
                                for (int j = 0; j < c_ContactList.size(); j++) {
                                    ContactInfo c_contact = c_ContactList.get(j);
                                    for (int k = 0; k < s_ContactList.size(); k++) {
                                        ContactInfo server_contact = s_ContactList.get(k);
                                        difference = compareContactData(server_contact, c_contact);
                                        if (difference == 0) {
                                            break;
                                        }
                                        if (difference == 1) {
                                            break;
                                        }
                                    }
                                    if (difference == 2) {
                                        upload_contactList.add(c_ContactList.get(j));
                                        SyncLab.get(getApplicationContext()).deleteContact(c_ContactList.get(j));
                                        ContactUtils.deleteContact(MainActivity.this, c_ContactList.get(j).getId());
                                    }
                                    if (difference == 0) {
                                        ContactUtils.deleteContact(MainActivity.this, c_ContactList.get(j).getId());
                                        SyncLab.get(getApplicationContext()).deleteContact(c_ContactList.get(j));
                                    }
                                    if (difference == 1) {
                                        ContactUtils.deleteContact(MainActivity.this, c_ContactList.get(j).getId());
                                        c_ContactList.get(j).setmTrash("trash");
                                        c_ContactList.get(j).setmSync("sync");
                                        SyncLab.get(getApplicationContext()).updateContact(c_ContactList.get(j));
                                    }
                                    difference = 0;
                                }
                                localdb_flag = false;
                            }
                        }

                        if(s_contact.getmTrash().equals("trash")){              /** If a downloaded contact is marked as Trash, and it does not already exist in the CLIENT’s Trash database */
                            SyncLab.get(getApplicationContext()).addContact(s_contact);
                        }
                        else{
                            int contactId = ContactUtils.addContactInPhone(s_contact, MainActivity.this);
                            s_contact.setId(String.valueOf(contactId));
                            SyncLab.get(getApplicationContext()).addContact(s_contact);
                        }

                    }
                }

            }
            for(int j = 0; j < local_contactList.size(); j++){                  // if sever contact is removed and device contact is not removed
                if(!server_contact_UIDList.contains(local_contactList.get(j).getUid())){
                    ContactUtils.deleteContact(MainActivity.this, local_contactList.get(j).getId());
                    SyncLab.get(getApplicationContext()).deleteContact(local_contactList.get(j));
                }
            }

        }
        else{
            if (local_contactList.size() > 0) {
                if(server_checksum == -1) {                 // if sever contacts is all removed in server, remove contacts in local
                    for (int i = 0; i < local_contactList.size(); i++) {
                        if(!local_contactList.get(i).getUid().equals("")) {
                            SyncLab.get(getApplicationContext()).deleteContact(local_contactList.get(i));
                            ContactUtils.deleteContact(MainActivity.this, local_contactList.get(i).getId());
                        }
                    }
                }
                if(getCheckSumFromLocal() == -1){           // if server contact is all removed in server and new contact is added in device, upload new contacts to server
                    for (int i = 0; i < local_contactList.size(); i++) {
                        if(local_contactList.get(i).getUid().equals("")) {
                            upload_contactList.add(local_contactList.get(i));
                            SyncLab.get(getApplicationContext()).deleteContact(local_contactList.get(i));
                            ContactUtils.deleteContact(MainActivity.this, local_contactList.get(i).getId());
                        }
                    }
                }
            }
        }

        if(upload_contactList.size() > 0){              // if contact to upload is exist
            ServerUtils.uploadContacts(MainActivity.this, upload_contactList, tokenInfo.getAuthentication());
        }
        else{
            syncProcess3();
        }

    }
    public int compareContactData(ContactInfo s_contact, ContactInfo c_contact){
        int difference = 0;
        int same = 0;
        int other = 0;
        if(s_contact.getNamePrefix().equals("") && c_contact.getNamePrefix().equals("")){
            other = 1;
        }
        else {
            if (!CompareUtils.comapareNamePrefix(s_contact, c_contact)) {
                difference = 1;
            } else {
                same = 1;
            }
        }
        if(s_contact.getFirstName().equals("") && c_contact.getFirstName().equals("")){
            other = 1;
        }
        else{
            if(!CompareUtils.compareFirstName(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getLastName().equals("") && c_contact.getLastName().equals("")){
            other = 1;
        }
        else{
            if(!CompareUtils.comapareLastName(s_contact, c_contact)){
                difference  = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getMiddleName().equals("") && c_contact.getMiddleName().equals("")){
            other = 1;
        }
        else {
            if(!CompareUtils.comapareMiddleName(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getNameSuffix().equals("") && c_contact.getNameSuffix().equals("")){
            other = 1;
        }
        else {
            if(!CompareUtils.comapareNameSuffix(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getCompany().getOrganization().equals("") && s_contact.getCompany().getTitle().equals("")){
            if(c_contact.getCompany().getOrganization().equals("") && c_contact.getCompany().getTitle().equals("")){
                other = 1;
            }
        }
        else {
            if (!CompareUtils.comapareCompany(s_contact, c_contact)) {
                difference = 1;
            } else {
                same = 1;
            }
        }
        if(s_contact.getPhones().size() == 0 && c_contact.getPhones().size() == 0){
            other = 1;
        }
        else {
            if(!CompareUtils.comaparePhones(s_contact, c_contact)) {
                difference = 1 ;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getEmails().size() == 0 && c_contact.getEmails().size() == 0){
            other = 1;
        }
        else {
            if(!CompareUtils.comapareEmails(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getAddressList().size() == 0 && c_contact.getAddressList().size() == 0){
            other = 1;
        }
        else{
            if(!CompareUtils.comapareAddress(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getImAddressesList().size() == 0 && c_contact.getImAddressesList().size() == 0){
            other = 1;
        }
        else{
            if(!CompareUtils.comapareIMAddress(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(s_contact.getEvents().size() == 0 && c_contact.getEvents().size() == 0){
            other = 1;
        }
        else{
            if(!CompareUtils.comapareEvents(s_contact, c_contact)){
                difference = 1;
            }
            else{
                same = 1;
            }
        }
        if(same == 1 && difference == 0 && other == 1){
            return 0;
        }
        if(same == 1 && difference == 1 && other == 1){
            return 1;
        }
        if(same == 0 && difference == 1 && other == 1){
            return 2;
        }
        else{
            return -1;
        }

    }
    public void syncProcess2() throws UnsupportedEncodingException, JSONException {
        if(response_uploadContactList.size() > 0){
            for(int i = 0; i < response_uploadContactList.size(); i++) {
                ContactInfo contact = response_uploadContactList.get(i);
                contact.setmSync("sync");
                contact.setmTrash("untrash");
                contact.setmRemove("unremove");
                int contactId = ContactUtils.addContactInPhone(contact, MainActivity.this);
                contact.setId(String.valueOf(contactId));
                SyncLab.get(getApplicationContext()).addContact(contact);
            }
        }

        syncProcess3();
    }
    public void syncProcess3() throws UnsupportedEncodingException, JSONException {
        /** If there are any contacts in the CLIENT database marked as Trash and also marked as deleted */
        if(delete_contactList.size() > 0){
            ServerUtils.deleteContacts(MainActivity.this, delete_contactList, tokenInfo.getAuthentication());
        }
        else{
            uploadCheckSum();
        }
    }
    Handler sync_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            syncDialog.dismiss();
            Toast.makeText(getApplicationContext(), "synchronized", Toast.LENGTH_SHORT).show();
            showSyncStatus();
            if(msg.what == 0){
               if(autoSync_flag && mInterval == 1 * 60 * 60 * 1000){
                   stopRepeatingAutoSync();
                   mInterval = 24 * 60 * 60 * 1000;
                   startRepeatingAutoSync();
               }
            }

        }
    };
    /** get checksum from local */
    public int getCheckSumFromLocal(){
        SyncInfo info = SyncLab.get(getApplicationContext()).getSyncInfo();
        String checksum = info.getUpdate_checksum();
        if(checksum == null || checksum.isEmpty() || checksum.equals("")) {
            return -1;
        }
        else{
            return Integer.parseInt(checksum);
        }
    }
    public void initContactArray(){
        local_contactList.clear();
        local_contact_UIDList.clear();
        local_contact_IDList.clear();
        upload_contactList.clear();
        delete_contactList.clear();
        response_uploadContactList.clear();
        server_contactList.clear();
        realContactList.clear();
        trash_contactList.clear();
    }

    public void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//                showSyncStatus();
            }
            else{
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
            }
        }
        else{
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_REQUEST_CODE);
        }
    }


    public void checkContact(){
        new ImportContactsAsync(this, new ImportContactsAsync.ICallback()
        {
            @Override
            public void mobileContacts(ArrayList<Contact> contactList)
            {
                SyncInfo syncInfo = SyncLab.get(getApplicationContext()).getSyncInfo();
                String checksum = syncInfo.getUpdate_checksum();
                Calendar cal=Calendar.getInstance(TimeZone.getDefault());                    /** get month as string */
                SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                String month_name = month_date.format(cal.getTime());
                int currentYear = cal.get(Calendar.YEAR);
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                String update_Date = month_name + " " + String.valueOf(currentDay) + ", " + String.valueOf(currentYear);

                local_contactList = SyncLab.get(getApplicationContext()).getContacts();
                local_contact_IDList = getLocal_Contact_IDList();
                if(contactList.size() > 0){
                    if(local_contactList.size() > 0){
                        for(int i = 0; i < local_contactList.size(); i++){
                            if(!local_contactList.get(i).getmTrash().equals("trash")){
                                realContactList.add(local_contactList.get(i));
                            }
                        }
                        if(contactList.size() != realContactList.size()){
                            int difference = 0;
                            for(int i = 0; i < contactList.size(); i++){
                                ContactInfo phone_contact = getContactInfo(contactList.get(i));
                                if(!local_contact_IDList.contains(phone_contact.getId())){      // if new contact have added to device
                                    upload_contactList.add(phone_contact);
                                }
                                if(local_contact_IDList.contains(phone_contact.getId())){      // if existing contact have changed in device
                                    ContactInfo db_contact = getContactInfoByID(phone_contact.getId());
                                    difference = compareContactData(db_contact, phone_contact);
                                    if(difference == 1){
                                        phone_contact.setUid(db_contact.getUid());
                                        SyncLab.get(getApplicationContext()).updateContact(phone_contact);

                                    }
                                }
                            }
                            List<String> device_contactIDList = new ArrayList<>();
                            for(int i = 0; i < contactList.size(); i++){
                                device_contactIDList.add(String.valueOf(contactList.get(i).getId()));
                            }
                            for(int i = 0; i < realContactList.size(); i++){
                                if(!device_contactIDList.contains(realContactList.get(i).getId())){     // if existing contact have removed in device
                                    ContactInfo delete_contact = realContactList.get(i);
                                    delete_contact.setmTrash("trash");
                                    SyncLab.get(getApplicationContext()).updateContact(delete_contact);
                                    trash_contactList.add(delete_contact);
                                }
                            }
                            SyncInfo update_info = new SyncInfo(String.valueOf(-1), update_Date);
                            if(checksum == null || checksum.isEmpty() || checksum.equals("")){
                                SyncLab.get(getApplicationContext()).addCheckSum(update_info);
                            }
                            else{
                                SyncLab.get(getApplicationContext()).updateCheckSum(String.valueOf(-1), update_Date);
                            }
                        }
                        if(contactList.size() == realContactList.size()){
                            int difference = 0;
                            for(int i = 0; i < contactList.size(); i++){
                                ContactInfo phone_contact = getContactInfo(contactList.get(i));
                                if(local_contact_IDList.contains(phone_contact.getId())){
                                    ContactInfo db_contact = getContactInfoByID(phone_contact.getId());
                                    difference = compareContactData(db_contact, phone_contact);
                                    if(difference == 1){                                ///   if existing contact have changed in device
                                        trash_contactList.add(db_contact);
                                        upload_contactList.add(phone_contact);
                                        SyncInfo update_info = new SyncInfo(String.valueOf(-1), update_Date);
                                        if(checksum == null || checksum.isEmpty() || checksum.equals("")){
                                            SyncLab.get(getApplicationContext()).addCheckSum(update_info);
                                        }
                                        else{
                                            SyncLab.get(getApplicationContext()).updateCheckSum(String.valueOf(-1), update_Date);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else{                   // if device contact is exist and does not exist in local db, add device contact in local db and change checksum of local
                        localdb_flag = true;
                        for(int i = 0; i < contactList.size(); i++){
                            Contact temp_contact = contactList.get(i);
                            ContactInfo contact = getContactInfo(temp_contact);
                            SyncLab.get(getApplicationContext()).addContact(contact);
                        }
                        SyncInfo update_info = new SyncInfo(String.valueOf(-1), update_Date);
                        if(checksum == null || checksum.isEmpty() || checksum.equals("")){
                            SyncLab.get(getApplicationContext()).addCheckSum(update_info);
                        }
                        else{
                            SyncLab.get(getApplicationContext()).updateCheckSum(String.valueOf(-1), update_Date);
                        }
                    }
                }
                else{                       // device contact does not exist and untrash contact is exist in local db, change as trash in localdb and change checksum of local
                    if(local_contactList.size() > 0){
                        for(int i = 0; i < local_contactList.size(); i++){
                            if(!local_contactList.get(i).getmTrash().equals("trash")){
                                realContactList.add(local_contactList.get(i));
                            }
                        }
                    }
                    if(realContactList.size() > 0){
                        for(int i = 0; i < realContactList.size(); i++){
                            ContactInfo contactInfo = realContactList.get(i);
                            contactInfo.setmTrash("trash");
                            SyncLab.get(getApplicationContext()).updateContact(contactInfo);
                            trash_contactList.add(contactInfo);
                        }
                        SyncInfo update_info = new SyncInfo(String.valueOf(-1), update_Date);
                        if(checksum == null || checksum.isEmpty() || checksum.equals("")){
                            SyncLab.get(getApplicationContext()).addCheckSum(update_info);
                        }
                        else{
                            SyncLab.get(getApplicationContext()).updateCheckSum(String.valueOf(-1), update_Date);
                        }
                    }
                }
                try {
                    compareLocalCheckSumWithServer();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute();
    }

    public ContactInfo getContactInfo(Contact temp_contact){
        ContactInfo data = new ContactInfo();
        data.setId(String.valueOf(temp_contact.getId()));
        data.setUid("");
        String nameprefix = temp_contact.getNamePrefix();
        String lastName = temp_contact.getFirstName();
        String firstName = temp_contact.getLastName();
        if(firstName.equals("")){
            firstName = lastName;
            lastName = "";
        }
        if(lastName.equals("")){
            lastName = firstName;
        }
        if(firstName.equals(lastName)){
            lastName = "";
        }
        String middleName = temp_contact.getMiddleName();
        String namesuffix = temp_contact.getNameSuffix();
        String fullName = firstName + " " + lastName;
        String company_org = temp_contact.getCompanyName();
        if(company_org == null){
            company_org = "";
        }
        String company_title = temp_contact.getCompanyTitle();
        Company company = new Company(company_org, company_title);

        LinkedList<NumberContainer> number = temp_contact.getNumbers();
        List<Phone> phone_list = new ArrayList<>();
        for (int i = 0; i < number.size(); i++) {
            String phone_number = number.get(i).elementValue();
            String type = number.get(i).getNumType();
            switch (type) {
                case "MOBILE":
                    phone_list.add(new Phone(phone_number, 2));
                    break;
                case "WORK":
                    phone_list.add(new Phone(phone_number, 3));
                    break;
                case "HOME":
                    phone_list.add(new Phone(phone_number, 1));
                    break;
                case "MAIN":
                    phone_list.add(new Phone(phone_number, 12));
                    break;
                case "FAX_WORK":
                    phone_list.add(new Phone(phone_number, 4));
                    break;
                case "FAX_HOME":
                    phone_list.add(new Phone(phone_number, 5));
                    break;
                case "PAGER":
                    phone_list.add(new Phone(phone_number, 6));
                    break;
                case "OTHER":
                    phone_list.add(new Phone(phone_number, 7));
                    break;
                case "CUSTOM":
                    phone_list.add(new Phone(phone_number, 0));
                    break;
            }
        }
        LinkedList<EmailContainer> email = temp_contact.getEmails();
        List<Email> email_list = new ArrayList<>();
        for (int j = 0; j < email.size(); j++) {
            String email_string = email.get(j).getEmail();
            String type = email.get(j).getEmailType();
            switch (type) {
                case "HOME":
                    email_list.add(new Email(email_string, 1));
                    break;
                case "WORK":
                    email_list.add(new Email(email_string, 2));
                    break;
                case "OTHER":
                    email_list.add(new Email(email_string, 3));
                    break;
                case "CUSTOME":
                    email_list.add(new Email(email_string, 0));
                    break;
                case "MOBILE":
                    email_list.add(new Email(email_string, 4));
                    break;
            }

        }
        LinkedList<AddressContainer> address_container = temp_contact.getAddresses();
        List<Address> addressList = new ArrayList<>();
        for (int i = 0; i < address_container.size(); i++) {
            String address = address_container.get(i).address.address;
            String type = address_container.get(i).addressType.addressType;
            switch (type) {
                case "HOME":
                    addressList.add(new Address(address, 1));
                    break;
                case "WORK":
                    addressList.add(new Address(address, 2));
                    break;
                case "OTHER":
                    addressList.add(new Address(address, 3));
                    break;
                case "CUSTOME":
                    addressList.add(new Address(address, 0));
                    break;
            }

        }
        LinkedList<IMContainer> imContainers = temp_contact.getIms();
        List<IMAddress> imAddressList = new ArrayList<>();
        for (int i = 0; i < imContainers.size(); i++) {
            String imName = imContainers.get(i).elementValue();
            String imType = imContainers.get(i).getImType();
            imAddressList.add(new IMAddress(imName, Integer.parseInt(imType)));
        }
        LinkedList<EventContainer> eventContainers = temp_contact.getEvents();
        List<Event> eventList = new ArrayList<>();
        for (int i = 0; i < eventContainers.size(); i++) {
            String eventName = eventContainers.get(i).getEventStartDate();
            String eventType = eventContainers.get(i).getEventType();

            switch (eventType) {
                case "Birthday":
                    eventList.add(new Event(eventName, 3));
                    break;
                case "Anniversary":
                    eventList.add(new Event(eventName, 1));
                    break;
                case "Other":
                    eventList.add(new Event(eventName, 2));
                    break;
                case "Custom":
                    eventList.add(new Event(eventName, 0));
                    break;
            }
        }
        data.setNamePrefix(nameprefix);
        data.setFirstName(firstName);
        data.setMiddleName(middleName);
        data.setLastName(lastName);
        data.setNameSuffix(namesuffix);
        data.setFullName(fullName);
        data.setPhones(phone_list);
        data.setEmails(email_list);
        data.setmTrash("untrash");
        data.setmSync("unsync");
        data.setmRemove("unremove");
        data.setCompany(company);
        data.setAddressList(addressList);
        data.setImAddressesList(imAddressList);
        data.setEvents(eventList);
        return data;
    }

    /** get UID list of all local contact list from local device */
    public List<String> getLocal_Contact_UIDList(){
        List<String> uid_list = new ArrayList<>();
        if(local_contactList.size() > 0){
            for(int i = 0; i < local_contactList.size(); i++){
                ContactInfo  local_contact = local_contactList.get(i);
                uid_list.add(local_contact.getUid());
            }
        }
        return uid_list;
    }
    /** get ID list of all local contact list from local device */
    public List<String> getLocal_Contact_IDList(){
        List<String> id_list = new ArrayList<>();
        if(local_contactList.size() > 0){
            for(int i = 0; i < local_contactList.size(); i++){
                ContactInfo  local_contact = local_contactList.get(i);
                id_list.add(local_contact.getId());
            }
        }
        return id_list;
    }
    public List<String> getServer_contact_UIDList(){
        List<String> uid_list = new ArrayList<>();
        if(server_contactList.size() > 0){
            for(int i = 0; i < server_contactList.size(); i++){
                ContactInfo server_contact = server_contactList.get(i);
                uid_list.add(server_contact.getUid());
            }
        }
        return uid_list;
    }
    public List<String> getServer_contact_IDList() {
        List<String> id_list = new ArrayList<>();
        if(server_contactList.size() > 0){
            for(int i = 0; i < server_contactList.size(); i++){
                ContactInfo server_contact = server_contactList.get(i);
                id_list.add(server_contact.getId());
            }
        }
        return id_list;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == READ_CONTACT_REQUEST_CODE){
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, WRITE_CONTACT_REQUEST_CODE);
        }
        if(requestCode == WRITE_CONTACT_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                showSyncStatus();
            }
        }

    }
    /** get contact info from local db by UID */
    public ContactInfo getContactInfoByUID(String uid){
        int index = 0;
        for(int j = 0; j < local_contactList.size(); j++){
            if(local_contactList.get(j).getUid().equals(uid)){
                index = j;
                break;
            }
        }
        return local_contactList.get(index);
    }
    /** get contact info from local db by ID */
    public ContactInfo getContactInfoByID(String id){
        int index = 0;
        for(int j = 0; j < local_contactList.size(); j++){
            if(local_contactList.get(j).getId().equals(id)){
                index = j;
                break;
            }
        }
        return local_contactList.get(index);
    }

    public void updateContactInPhoneAndDB(ContactInfo s_contact, ContactInfo local_contact){
        if(!s_contact.getmTrash().equals("trash")){
            if(!local_contact.getmTrash().equals("trash")){
//                local_contact.setmSync("sync");
//                SyncLab.get(getApplicationContext()).updateContact(local_contact);
            }
            else{
                local_contact.setmSync("sync");
                local_contact.setmTrash("untrash");
                local_contact.setmRemove("unremove");
                int contactId = ContactUtils.addContactInPhone(local_contact, MainActivity.this);
                local_contact.setId(String.valueOf(contactId));
                SyncLab.get(getApplicationContext()).updateContact(local_contact);
            }
        }
        else{
            if(!local_contact.getmTrash().equals("trash")){
                ContactUtils.deleteContact(MainActivity.this, local_contact.getId());
                local_contact.setmTrash("trash");
                local_contact.setmSync("sync");
                local_contact.setmRemove("unremove");
                SyncLab.get(getApplicationContext()).updateContact(local_contact);
            }
            else{

            }
        }
    }

    public int getDeleteContactList(){
        List<ContactInfo> list = SyncLab.get(getApplicationContext()).getContacts();
        if(list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                ContactInfo contactInfo = list.get(i);
                if(contactInfo.getmRemove().equals("remove")){
                    delete_contactList.add(contactInfo);
                }
            }
        }
        return delete_contactList.size();
    }
    public void deleteContactFromLocalDB(List<ContactInfo> contactInfoList){
        for(int i = 0; i < contactInfoList.size(); i++){
            ContactInfo contactInfo = contactInfoList.get(i);
            SyncLab.get(getApplicationContext()).deleteContact(contactInfo);
        }
        /** upload new checksum to server */
        uploadCheckSum();

    }
    public void uploadCheckSum(){
        int local_checksum = SyncLab.get(getApplicationContext()).getContacts().size();
        ServerUtils.setCheckSum(MainActivity.this, local_checksum, tokenInfo.getAuthentication());

    }
    /** set Synchronize info to client db */
    public void saveSyncInfo(){
        int local_checksum = SyncLab.get(getApplicationContext()).getContacts().size();
        SyncInfo syncInfo = SyncLab.get(getApplicationContext()).getSyncInfo();
        String checksum = syncInfo.getUpdate_checksum();
        Calendar cal=Calendar.getInstance(TimeZone.getDefault());                    /** get month as string */
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());
        int currentYear = cal.get(Calendar.YEAR);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        String update_Date = month_name + " " + String.valueOf(currentDay) + ", " + String.valueOf(currentYear);
        SyncInfo update_info = new SyncInfo(String.valueOf(local_checksum), update_Date);
        if(checksum == null || checksum.isEmpty() || checksum.equals("")){
            SyncLab.get(getApplicationContext()).addCheckSum(update_info);
        }
        else{
            SyncLab.get(getApplicationContext()).updateCheckSum(String.valueOf(local_checksum), update_Date);
        }
        sync_handler.sendEmptyMessage(0);
    }
    public void showSyncStatus(){
        SyncInfo info = SyncLab.get(getApplicationContext()).getSyncInfo();
        String date = info.getUpdate_date();
        String sync_count = info.getUpdate_checksum();
        if(date == null || date.isEmpty() || date.equals("")) {
            Calendar cal=Calendar.getInstance(TimeZone.getDefault());                    /** get month as string */
            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
            String month_name = month_date.format(cal.getTime());
            int currentYear = cal.get(Calendar.YEAR);
            int currentDay = cal.get(Calendar.DAY_OF_MONTH);
            String update_Date = month_name + " " + String.valueOf(currentDay) + ", " + String.valueOf(currentYear);
            syncStatusView.setText("Account :" + tokenInfo.getAccount_Number() + "\n" + "Last sync:" + update_Date + "\n" + "Contacts sync:0");
        }
        else{
            syncStatusView.setText("Account " + tokenInfo.getAccount_Number() + "\n" + "Last sync:" + date + "\n" + "Contacts sync:" + sync_count);

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        showSyncStatus();
    }
}

