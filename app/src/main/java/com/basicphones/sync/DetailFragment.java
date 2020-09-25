package com.basicphones.sync;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.ContactModel.Phone;

import org.json.JSONException;

public class DetailFragment extends Fragment {

    private View view;
    private static final String TRASH_CONTACT_ID = "contact_id";
    private ContactInfo mContact;
    private Token token;
    private TextView firstName;
    private TextView lastName;
    private LinearLayout mPhonesLayout;
    private List<TextView> mPhones;
    private List<Phone> phones;
    private LinearLayout mPhonesLayout1;

    private LinearLayout mEmailsLayout;
    private LinearLayout mEmailsLayout1;
    private List<Email> emails;
    private List<TextView> mEmails;

    private LinearLayout mCompanyLayout;
    private List<TextView> mCompany;
    private Company companyList;
    private LinearLayout mCompanyLayout1;

    private LinearLayout mAddressLayout;
    private LinearLayout mAddressLayout1;
    private List<Address> addresses;
    private List<TextView> maddresses;

    private LinearLayout mIMAddressLayout;
    private LinearLayout mIMAddressLayout1;
    private List<IMAddress> imaddresses;
    private List<TextView> mimaddresses;

    private LinearLayout mEventLayout;
    private LinearLayout mEventLayout1;
    private List<Event> events;
    private List<TextView> mevents;
    private static DetailFragment instance = null;
    public ProgressDialog backupDialog;
    public ProgressDialog deleteDialog;
    public List<ContactInfo> local_contactList = new ArrayList<>();

    public static final int CONTACT_BACKUP_REQUESTCODE = 125;


    public DetailFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        instance = this;
        mContact = (ContactInfo) getArguments().getSerializable(TRASH_CONTACT_ID);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.detail_fragment, container, false);
        firstName = (TextView) view.findViewById(R.id.first_name);
        lastName = (TextView) view.findViewById(R.id.last_name);
        mPhonesLayout = (LinearLayout) view.findViewById(R.id.view_contact_phones_list);
        mEmailsLayout = (LinearLayout) view.findViewById(R.id.view_contact_emails_list);
        mCompanyLayout = (LinearLayout) view.findViewById(R.id.company_list);
        mAddressLayout = (LinearLayout) view.findViewById(R.id.address_list);
        mIMAddressLayout = (LinearLayout) view.findViewById(R.id.imaddress_list);
        mEventLayout = (LinearLayout) view.findViewById(R.id.event_list);
        token = SyncLab.get(getActivity()).getTokenInfo();
        return view;
    }

    public static DetailFragment newInstance(ContactInfo contact) {
        Bundle args = new Bundle();
        args.putSerializable(TRASH_CONTACT_ID, contact);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    public void updateUI() {
        if(mContact!=null)
        {
            firstName.setText(mContact.getFirstName());
            lastName.setText(mContact.getLastName());

            mPhonesLayout.removeAllViews();
            phones = mContact.getPhones();
            mPhones = new ArrayList<>();
            for(int i=0;i!=phones.size();i++)
            {
                addPhoneTextView(phones.get(i));
            }

            mEmailsLayout.removeAllViews();
            emails = mContact.getEmails();
            mEmails = new ArrayList<>();
            for(int j=0; j < emails.size();j++)
            {
                addEmailTextView(emails.get(j));
            }

            mCompanyLayout.removeAllViews();
            companyList = mContact.getCompany();
            mCompany = new ArrayList<>();
            addCompanyTextView(companyList);

            mAddressLayout.removeAllViews();
            addresses = mContact.getAddressList();
            maddresses = new ArrayList<>();
            for(int i=0;i!=addresses.size();i++)
            {
                addAddressTextView(addresses.get(i));
            }

            mIMAddressLayout.removeAllViews();
            imaddresses = mContact.getImAddressesList();
            mimaddresses = new ArrayList<>();
            for(int i=0;i!=imaddresses.size();i++)
            {
                addIMAddressTextView(imaddresses.get(i));
            }

            mEventLayout.removeAllViews();
            events = mContact.getEvents();
            mevents = new ArrayList<>();
            for(int i=0;i!=events.size();i++)
            {
                addEventTextView(events.get(i));
            }

        }
    }

    private void addPhoneTextView(Phone m_phone)
    {
        TextView phoneTextView = new TextView(getContext());
        mPhones.add(phoneTextView);
        int i = mPhones.size()-1;
        mPhones.get(i).setText(m_phone.getNumber());
        mPhones.get(i).setPadding(0,0,0,0);
        mPhones.get(i).setId(i);
        mPhones.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mPhones.get(i).setLayoutParams(params);

        TextView typeView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 0);
        typeView.setLayoutParams(params1);
        typeView.setId(i);
        int type = m_phone.getType();
        switch (type){
            case 2:
                typeView.setText("Mobile");
                break;
            case 3:
                typeView.setText("Work");
                break;
            case 1:
                typeView.setText("Home");
                break;
            case 12:
                typeView.setText("Main");
                break;
            case 4:
                typeView.setText("Work Fax");
                break;
            case 5:
                typeView.setText("Home Fax");
                break;
            case 6:
                typeView.setText("Pager");
                break;
            case 7:
                typeView.setText("Other");
                break;
            case 0:
                typeView.setText("Custom");
                break;
        }

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPhonesLayout1 = new LinearLayout(getContext());
        mPhonesLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mPhonesLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mPhonesLayout1.addView(mPhones.get(i));
        mPhonesLayout1.addView(typeView);
        mPhonesLayout.addView(mPhonesLayout1);
    }

    private void addEmailTextView(Email m_email)
    {
        TextView emailTextView = new TextView(getContext());
        mEmails.add(emailTextView);
        int i = mEmails.size()-1;
        mEmails.get(i).setText(m_email.getEmail());
        mEmails.get(i).setPadding(0,0,0,0);
        mEmails.get(i).setId(i);
        mEmails.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mEmails.get(i).setLayoutParams(params);

        TextView typeView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        typeView.setLayoutParams(params1);
        int type = m_email.getType();
        switch (type){
            case 1:
                typeView.setText("Home");
                break;
            case 2:
                typeView.setText("Work");
                break;
            case 3:
                typeView.setText("Other");
                break;
            case 0:
                typeView.setText("Custom");
                break;

        }

        mEmailsLayout1 = new LinearLayout(getContext());
        mEmailsLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mEmailsLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mEmailsLayout1.addView(mEmails.get(i));
        mEmailsLayout1.addView(typeView);
        mEmailsLayout.addView(mEmailsLayout1);

    }

    public void addCompanyTextView(Company m_company){
        TextView companyTextView = new TextView(getContext());
        mCompany.add(companyTextView);
        int i = mCompany.size()-1;
        mCompany.get(i).setText(m_company.getOrganization());
        mCompany.get(i).setPadding(0,0,10,0);
        mCompany.get(i).setId(i);
        mCompany.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mCompany.get(i).setLayoutParams(params);

        TextView titleView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleView.setLayoutParams(params1);
        titleView.setText(m_company.getTitle());

        mCompanyLayout1 = new LinearLayout(getContext());
        mCompanyLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mCompanyLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mCompanyLayout1.addView(mCompany.get(i));
        mCompanyLayout1.addView(titleView);
        mCompanyLayout.addView(mCompanyLayout1);
    }

    private void addAddressTextView(Address m_address){
        TextView addressTextView = new TextView(getContext());
        maddresses.add(addressTextView);
        int i = maddresses.size()-1;
        maddresses.get(i).setText(m_address.getAddressName());
        maddresses.get(i).setPadding(0,0,0,0);
        maddresses.get(i).setId(i);
        maddresses.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        maddresses.get(i).setLayoutParams(params);

        TextView typeView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 0);
        typeView.setLayoutParams(params1);
        typeView.setId(i);
        int type = m_address.getAddressType();
        switch (type){
            case 1:
                typeView.setText("Home");
                break;
            case 2:
                typeView.setText("Work");
                break;
            case 3:
                typeView.setText("Other");
                break;
            case 0:
                typeView.setText("Custom");
                break;
        }

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAddressLayout1 = new LinearLayout(getContext());
        mAddressLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mAddressLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mAddressLayout1.addView(maddresses.get(i));
        mAddressLayout1.addView(typeView);
        mAddressLayout.addView(mAddressLayout1);
    }

    private void addIMAddressTextView(IMAddress m_imaddress){
        TextView imaddressTextView = new TextView(getContext());
        mimaddresses.add(imaddressTextView);
        int i = maddresses.size()-1;
        mimaddresses.get(i).setText(m_imaddress.getImaddressName());
        mimaddresses.get(i).setPadding(0,0,0,0);
        mimaddresses.get(i).setId(i);
        mimaddresses.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mimaddresses.get(i).setLayoutParams(params);

        TextView typeView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 0);
        typeView.setLayoutParams(params1);
        typeView.setId(i);
        int type = m_imaddress.getImaddressType();
        switch (type){
            case 0:
                typeView.setText("AIM");
                break;
            case 2:
                typeView.setText("Yahoo");
                break;
            case 3:
                typeView.setText("Skype");
                break;
            case 4:
                typeView.setText("QQ");
                break;
            case 5:
                typeView.setText("Hangouts");
                break;
            case 6:
                typeView.setText("ICQ");
                break;
            case 7:
                typeView.setText("Jabber");
                break;
            case 1:
                typeView.setText("Windows Live");
                break;
            case -1:
                typeView.setText("Custom");
                break;
        }

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mIMAddressLayout1 = new LinearLayout(getContext());
        mIMAddressLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mIMAddressLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mIMAddressLayout1.addView(mimaddresses.get(i));
        mIMAddressLayout1.addView(typeView);
        mIMAddressLayout.addView(mIMAddressLayout1);
    }

    private void addEventTextView(Event m_event){
        TextView eventTextView = new TextView(getContext());
        mevents.add(eventTextView);
        int i = mevents.size()-1;
        mevents.get(i).setText(m_event.getEventDate());
        mevents.get(i).setPadding(0,0,0,0);
        mevents.get(i).setId(i);
        mevents.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(0, 0, 0, 0);
        params.gravity = Gravity.CENTER;
        mevents.get(i).setLayoutParams(params);

        TextView typeView = new TextView(getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 0, 0, 0);
        typeView.setLayoutParams(params1);
        typeView.setId(i);
        int type = m_event.getEventType();
        switch (type){
            case 0:
                typeView.setText("Custom");
                break;
            case 1:
                typeView.setText("Anniversary");
                break;
            case 2:
                typeView.setText("Other");
                break;
            case 3:
                typeView.setText("Birthday");
                break;
        }

        typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mEventLayout1 = new LinearLayout(getContext());
        mEventLayout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        mEventLayout1.setOrientation(LinearLayout.HORIZONTAL);
        mEventLayout1.addView(mevents.get(i));
        mEventLayout1.addView(typeView);
        mEventLayout.addView(mEventLayout1);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static DetailFragment getInstance(){
        return instance;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.trashview, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                showConfirmBackupDialogue();
                return true;
            case R.id.delete:
                showConfirmDeleteDialogue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showConfirmBackupDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Contact Backup")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
                            backupDialog = ProgressDialog.show(getActivity(), "", "backup");
                            try {
                                ServerUtils.backupContact(getContext(), mContact, token.getAuthentication());
                            } catch (JSONException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, CONTACT_BACKUP_REQUESTCODE);
                        }

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        cancel();
                    }

                })
                .show();
    }
    private void showConfirmDeleteDialogue() {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Contact Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//                            deleteDialog = ProgressDialog.show(getActivity(), "", "deleting");
                            mContact.setmRemove("remove");
                            SyncLab.get(getContext()).updateContact(mContact);

                            Calendar cal=Calendar.getInstance(TimeZone.getDefault());                    /** get month as string */
                            SimpleDateFormat month_date = new SimpleDateFormat("MMM");
                            String month_name = month_date.format(cal.getTime());
                            int currentYear = cal.get(Calendar.YEAR);
                            int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                            String update_Date = month_name + " " + String.valueOf(currentDay) + ", " + String.valueOf(currentYear);
                            SyncLab.get(getContext()).updateCheckSum(String.valueOf(-1), update_Date);
                            ((TrashDetail)getActivity()).goTrashListScreen1();
                        }

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CONTACT_BACKUP_REQUESTCODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                backupDialog = ProgressDialog.show(getActivity(), "", "backup");
                try {
                    ServerUtils.backupContact(getContext(), mContact, token.getAuthentication());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void backUp(ContactInfo contact){
        local_contactList = SyncLab.get(getActivity()).getContacts();
        if(local_contactList.size() > 0){
            int difference = 0;
            for(int i = 0; i < local_contactList.size(); i++){
                ContactInfo local_cmpContact = local_contactList.get(i);
                difference = compareContactData(contact, local_cmpContact);
                if(difference == 1){                                                            // if restored contact from server is exist on phone
                    ContactUtils.deleteContact(getActivity(), local_cmpContact.getId());
                    local_cmpContact.setmTrash("trash");
                    SyncLab.get(getActivity()).updateContact(local_cmpContact);
                }
            }
        }
        int contactId = ContactUtils.addContactInPhone(contact, getActivity());
        contact.setId(String.valueOf(contactId));
        SyncLab.get(getActivity()).addContact(contact);
        changeCheckSumofServer();

    }
    Handler backup_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            backupDialog.dismiss();
            Toast.makeText(getContext(), "backup success!", Toast.LENGTH_SHORT).show();
        }
    };

    public void saveSyncInfo(){
        int local_checksum = SyncLab.get(getActivity()).getContacts().size();
        SyncInfo syncInfo = SyncLab.get(getActivity()).getSyncInfo();
        String checksum = syncInfo.getUpdate_checksum();
        Calendar cal=Calendar.getInstance(TimeZone.getDefault());                    /** get month as string */
        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(cal.getTime());
        int currentYear = cal.get(Calendar.YEAR);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        String update_Date = month_name + " " + String.valueOf(currentDay) + ", " + String.valueOf(currentYear);
        SyncInfo update_info = new SyncInfo(String.valueOf(local_checksum), update_Date);
        if(checksum == null || checksum.isEmpty() || checksum.equals("")){
            SyncLab.get(getActivity()).addCheckSum(update_info);
        }
        else{
            SyncLab.get(getActivity()).updateCheckSum(String.valueOf(local_checksum), update_Date);
        }
        backupDialog.dismiss();
        ((TrashDetail)getActivity()).goTrashListScreen();
    }
    public void changeCheckSumofServer(){
        int local_checksum = SyncLab.get(getActivity()).getContacts().size();
        ServerUtils.changeCheckSum(getContext(), local_checksum, token.getAuthentication());
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
}
