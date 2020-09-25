package com.basicphones.sync;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.basicphones.sync.ContactModel.Company;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import com.basicphones.sync.ContactModel.Phone;

public class ServerUtils {

    public static void getAllContactListFromServer(String token, final MainActivity activity) {
        final RequestParams rp = new RequestParams();
        final String url = "/contacts";
        HttpUtils.get(url, rp, token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                try {
                    JSONArray result_array = response.getJSONArray("results");
                    List<ContactInfo> contactInfoList = new ArrayList<>();
                    for(int i = 0; i < result_array.length(); i++){
                        JSONObject object = result_array.getJSONObject(i);
                        ContactInfo contactInfo = new ContactInfo();
                        contactInfo.setId("");
                        contactInfo.setUid(object.getString("id"));
                        contactInfo.setNamePrefix(object.getString("name_prefix"));
                        contactInfo.setFirstName(object.getString("first_name"));
                        contactInfo.setMiddleName(object.getString("middle_name"));
                        contactInfo.setLastName(object.getString("last_name"));
                        contactInfo.setNameSuffix(object.getString("name_suffix"));
                        contactInfo.setFullName(object.getString("full_name"));
                        JSONArray number_objects = object.getJSONArray("numbers");
                        for(int j = 0; j < number_objects.length(); j++){
                            JSONObject numberObject = number_objects.getJSONObject(j);
                            String number = numberObject.getString("number");
                            int type = 2;
                            if(numberObject.getString("type").equals("M")){
                                type = 12;
                            }
                            if(numberObject.getString("type").equals("W")){
                                type = 3;
                            }
                            if(numberObject.getString("type").equals("H")){
                                type = 1;
                            }
                            if(numberObject.getString("type").equals("FW")){
                                type = 4;
                            }
                            if(numberObject.getString("type").equals("FH")){
                                type = 5;
                            }
                            if(numberObject.getString("type").equals("P")){
                                type = 6;
                            }
                            if(numberObject.getString("type").equals("O")){
                                type = 7;
                            }

                            contactInfo.addPhone(number, type);
                        }
                        JSONArray email_objects = object.getJSONArray("emails");
                        for(int j = 0; j < email_objects.length(); j++){
                            JSONObject emailObject = email_objects.getJSONObject(j);
                            int type = 1;
                            if(emailObject.getString("email_type").equals("W")){
                                type = 2;
                            }
                            if(emailObject.getString("email_type").equals("O")){
                                type = 3;
                            }
                            contactInfo.addEmail(emailObject.getString("email_address"), type);
                        }
                        JSONArray address_objects = object.getJSONArray("addresses");
                        for(int j = 0; j < address_objects.length(); j++){
                            JSONObject addressObject = address_objects.getJSONObject(j);
                            int type = 1;
                            if(addressObject.getString("address_type").equals("W")){
                                type = 2;
                            }
                            if(addressObject.getString("address_type").equals("O")){
                                type = 3;
                            }
                            contactInfo.addAddress(addressObject.getString("physical_address"), type);
                        }
                        JSONArray ims_objects = object.getJSONArray("ims");
                        for(int j = 0; j < ims_objects.length(); j++){
                            JSONObject imsObject = ims_objects.getJSONObject(j);
                            int type = 0;
                            if(imsObject.getString("address_type").equals("WL")){
                                type = 1;
                            }
                            if(imsObject.getString("address_type").equals("YA")){
                                type = 2;
                            }
                            if(imsObject.getString("address_type").equals("SK")){
                                type = 3;
                            }
                            if(imsObject.getString("address_type").equals("QQ")){
                                type = 4;
                            }
                            if(imsObject.getString("address_type").equals("HA")){
                                type = 5;
                            }
                            if(imsObject.getString("address_type").equals("IC")){
                                type = 6;
                            }
                            if(imsObject.getString("address_type").equals("JA")){
                                type = 7;
                            }
                            if(imsObject.getString("address_type").equals("O")){
                                type = -1;
                            }
                            contactInfo.addIMAddress(imsObject.getString("im_address"), type);
                        }
                        JSONArray event_objects = object.getJSONArray("events");
                        for(int j = 0; j < event_objects.length(); j++){
                            JSONObject eventObject = event_objects.getJSONObject(j);
                            int type = 3;
                            if(eventObject.getString("event_type").equals("A")){
                                type = 1;
                            }
                            if(eventObject.getString("event_type").equals("O")){
                                type = 2;
                            }
                            contactInfo.addEvent(eventObject.getString("event_date"), type);
                        }
                        contactInfo.setCompany(new Company(object.getString("company"), object.getString("title")));
                        if(object.getString("trash").equals("False")){
                            contactInfo.setmTrash("untrash");
                        }
                        else{
                            contactInfo.setmTrash("trash");
                        }
                        contactInfo.setmSync("sync");
                        contactInfo.setmRemove("unremove");
                        contactInfoList.add(contactInfo);
                    }
                    activity.server_contactList = contactInfoList;
                    activity.syncProcess1();
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                // Pull out the first event on the public timeline
                Log.d("apr_error: ", "statuscode=   " + statusCode);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + errorResponse.toString());
                Toast.makeText(activity, "Synchronize operation failed", Toast.LENGTH_SHORT).show();
                activity.syncDialog.dismiss();
                if(activity.autoSync_flag){
                    activity.stopRepeatingAutoSync();
                    activity.mInterval = 1 * 60 * 60 * 1000;
                    activity.startRepeatingAutoSync();
                }
                else{
                    Toast.makeText(activity.getApplicationContext(), "Synchronize operation failed", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    public static void deleteContacts(final MainActivity activity, final List<ContactInfo> contactInfoList, final String token) throws JSONException, UnsupportedEncodingException {
        JSONArray array = new JSONArray();
        for(int i = 0; i < contactInfoList.size(); i++){
            ContactInfo contactInfo = contactInfoList.get(i);
            JSONObject object = new JSONObject();
            object.put("id", contactInfo.getUid());
            array.put(object);
        }
        final JSONObject postData = new JSONObject();
        postData.put("delete_data", array);
        final String url = "/contacts/delete-contact/";
        HttpUtils.post(activity, url, postData, token, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " +response.toString());
                activity.deleteContactFromLocalDB(contactInfoList);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                activity.syncDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), "deleting contact failed!", Toast.LENGTH_SHORT).show();

            }
        });

    }
    public static void trashContacts(final MainActivity activity, final List<ContactInfo> contactInfoList, final String token) throws JSONException, UnsupportedEncodingException {
        JSONArray array = new JSONArray();
        for(int i = 0; i < contactInfoList.size(); i++){
            ContactInfo contactInfo = contactInfoList.get(i);
            JSONObject object = new JSONObject();
            object.put("id", contactInfo.getUid());
            array.put(object);
        }
        final JSONObject postData = new JSONObject();
        postData.put("trash_data", array);
        final String url = "/contacts/trash-contact/";
        HttpUtils.post(activity, url, postData, token, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " +response.toString());
                activity.getAllContactFromServer();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                activity.syncDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), "deleting contact failed!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public static void getCheckSumFromServer(final String token, final MainActivity activity){
        final RequestParams rp = new RequestParams();
        final String url = "/devicedetail/get-checksum/";
        HttpUtils.get(url, rp, token, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int checksum = 0;
                try {
                    checksum = response.getInt("checksum");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                activity.server_checksum = checksum;
                activity.checkContact();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                activity.syncDialog.dismiss();
                if(activity.autoSync_flag){
                    activity.stopRepeatingAutoSync();
                    activity.mInterval = 1 * 60 * 60 * 1000;
                    Toast.makeText(activity.getApplicationContext(), "will try again after 1 hour", Toast.LENGTH_SHORT).show();
                    activity.startRepeatingAutoSync();
                }
                else{
                    Toast.makeText(activity.getApplicationContext(), "authentication failed", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    public static void uploadContacts(final MainActivity activity, final List<ContactInfo> contactInfoList, String token) throws JSONException, UnsupportedEncodingException {
        JSONArray array = new JSONArray();
        for(int i = 0; i < contactInfoList.size(); i++){
            ContactInfo contactInfo = contactInfoList.get(i);
            JSONObject object = new JSONObject();
            object.put("name_prefix", contactInfo.getNamePrefix());
            object.put("first_name", contactInfo.getFirstName());
            object.put("middle_name", contactInfo.getMiddleName());
            object.put("last_name", contactInfo.getLastName());
            object.put("name_suffix", contactInfo.getNameSuffix());
            object.put("full_name", contactInfo.getFullName());
            object.put("trash", "False");
            JSONArray phone_objects = new JSONArray();
            for(int j = 0; j < contactInfo.getPhones().size(); j++){
                JSONObject phoneObject = new JSONObject();
                phoneObject.put("number", contactInfo.getPhones().get(j).getNumber());
                int type = contactInfo.getPhones().get(j).getType();
                switch (type){
                    case 1:
                        phoneObject.put("type", "H");
                        break;
                    case 2:
                        phoneObject.put("type", "C");
                        break;
                    case 3:
                        phoneObject.put("type", "W");
                        break;
                    case 4:
                        phoneObject.put("type", "FW");
                        break;
                    case 5:
                        phoneObject.put("type", "FH");
                        break;
                    case 6:
                        phoneObject.put("type", "P");
                        break;
                    case 7:
                        phoneObject.put("type", "O");
                        break;
                    case 12:
                        phoneObject.put("type", "M");
                        break;
                }
                phoneObject.put("contact", 0);
                phoneObject.put("primary_number", false);
                phone_objects.put(phoneObject);
            }
            object.put("numbers", phone_objects);
            JSONArray email_objects = new JSONArray();
            for(int j = 0; j < contactInfo.getEmails().size(); j++){
                JSONObject emailObject = new JSONObject();
                emailObject.put("email_address", contactInfo.getEmails().get(j).getEmail());
                int type = contactInfo.getEmails().get(j).getType();
                switch (type){
                    case 1:
                        emailObject.put("email_type", "H");
                        break;
                    case 2:
                        emailObject.put("email_type", "W");
                        break;
                    case 3:
                        emailObject.put("email_type", "O");
                        break;
                }
                emailObject.put("contact", 0);
                email_objects.put(emailObject);
            }
            object.put("emails", email_objects);
            JSONObject companyObject = new JSONObject();
            companyObject.put("organizatin", contactInfo.getCompany().getOrganization());
            companyObject.put("title", contactInfo.getCompany().getTitle());
//            object.put("company", companyObject);
            object.put("company", contactInfo.getCompany().getOrganization());
            object.put("title", contactInfo.getCompany().getTitle());
            JSONArray address_objects = new JSONArray();
            for(int j = 0; j < contactInfo.getAddressList().size(); j++){
                JSONObject addressObject = new JSONObject();
                addressObject.put("physical_address", contactInfo.getAddressList().get(j).getAddressName());
                int type = contactInfo.getAddressList().get(j).getAddressType();
                switch (type){
                    case 1:
                        addressObject.put("address_type", "H");
                        break;
                    case 2:
                        addressObject.put("address_type", "W");
                        break;
                    case 3:
                        addressObject.put("address_type", "O");
                        break;

                }
                addressObject.put("contact", 0);
                address_objects.put(addressObject);
            }
            object.put("addresses", address_objects);
            JSONArray imaddress_objects = new JSONArray();
            for(int j = 0; j < contactInfo.getImAddressesList().size(); j++){
                JSONObject imaddressObject = new JSONObject();
                imaddressObject.put("im_address", contactInfo.getImAddressesList().get(j).getImaddressName());
                int type = contactInfo.getImAddressesList().get(j).getImaddressType();
                switch (type){
                    case 1:
                        imaddressObject.put("address_type", "WL");
                        break;
                    case 2:
                        imaddressObject.put("address_type", "YA");
                        break;
                    case 3:
                        imaddressObject.put("address_type", "SK");
                        break;
                    case 4:
                        imaddressObject.put("address_type", "QQ");
                        break;
                    case 5:
                        imaddressObject.put("address_type", "HA");
                        break;
                    case 6:
                        imaddressObject.put("address_type", "IC");
                        break;
                    case 7:
                        imaddressObject.put("address_type", "JA");
                        break;
                    case -1:
                        imaddressObject.put("address_type", "O");
                        break;
                    case 0:
                        imaddressObject.put("address_type", "AI");
                        break;
                }
                imaddressObject.put("contact", 0);
                imaddress_objects.put(imaddressObject);
            }
            object.put("ims", imaddress_objects);
            JSONArray event_objects = new JSONArray();
            for(int j = 0; j < contactInfo.getEvents().size(); j++){
                JSONObject eventObject = new JSONObject();
                eventObject.put("event_date", contactInfo.getEvents().get(j).getEventDate());
                int type = contactInfo.getEvents().get(j).getEventType();
                switch (type) {
                    case 1:
                        eventObject.put("event_type", "A");
                        break;
                    case 2:
                        eventObject.put("event_type", "O");
                        break;
                    case 3:
                        eventObject.put("event_type", "B");
                        break;
                }
                eventObject.put("contact", 0);
                event_objects.put(eventObject);
            }
            object.put("events", event_objects);
            JSONArray group_object = new JSONArray();
            for(int j = 0; j < contactInfo.getGroups().size(); j++){
                JSONObject groupObject = new JSONObject();
                groupObject.put("contact", 0);
                groupObject.put("group_name", contactInfo.getGroups().get(j).getGroupName());
                group_object.put(groupObject);
            }
            object.put("groups", group_object);
            array.put(object);
        }
        JSONObject postData = new JSONObject();
        postData.put("upload_data", array);
        String url = "/contacts/create-contact/";
        HttpUtils.post(activity, url, postData, token, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " +response.toString());
                for(int i = 0; i < contactInfoList.size(); i++){
                    ContactInfo deleteContact = contactInfoList.get(i);
                    ContactUtils.deleteContact(activity, deleteContact.getId());
                }
                try {
                    JSONArray result_array = response.getJSONArray("results");
                    List<ContactInfo> responseContactList = new ArrayList<>();
                    for(int i = 0; i < result_array.length(); i++){
                        JSONObject object = result_array.getJSONObject(i);
                        ContactInfo contactInfo = new ContactInfo();
                        contactInfo.setId("");
                        contactInfo.setUid(object.getString("id"));
                        contactInfo.setNamePrefix(object.getString("name_prefix"));
                        contactInfo.setFirstName(object.getString("first_name"));
                        contactInfo.setMiddleName(object.getString("middle_name"));
                        contactInfo.setLastName(object.getString("last_name"));
                        contactInfo.setNameSuffix(object.getString("name_suffix"));
                        contactInfo.setFullName(object.getString("full_name"));
                        JSONArray number_objects = object.getJSONArray("numbers");
                        for(int j = 0; j < number_objects.length(); j++){
                            JSONObject numberObject = number_objects.getJSONObject(j);
                            String number = numberObject.getString("number");
                            int type = 2;
                            if(numberObject.getString("type").equals("M")){
                                type = 12;
                            }
                            if(numberObject.getString("type").equals("W")){
                                type = 3;
                            }
                            if(numberObject.getString("type").equals("H")){
                                type = 1;
                            }
                            if(numberObject.getString("type").equals("FW")){
                                type = 4;
                            }
                            if(numberObject.getString("type").equals("FH")){
                                type = 5;
                            }
                            if(numberObject.getString("type").equals("P")){
                                type = 6;
                            }
                            if(numberObject.getString("type").equals("O")){
                                type = 7;
                            }

                            contactInfo.addPhone(number, type);
                        }
                        JSONArray email_objects = object.getJSONArray("emails");
                        for(int j = 0; j < email_objects.length(); j++){
                            JSONObject emailObject = email_objects.getJSONObject(j);
                            int type = 1;
                            if(emailObject.getString("email_type").equals("W")){
                                type = 2;
                            }
                            if(emailObject.getString("email_type").equals("O")){
                                type = 3;
                            }
                            contactInfo.addEmail(emailObject.getString("email_address"), type);
                        }
                        JSONArray address_objects = object.getJSONArray("addresses");
                        for(int j = 0; j < address_objects.length(); j++){
                            JSONObject addressObject = address_objects.getJSONObject(j);
                            int type = 1;
                            if(addressObject.getString("address_type").equals("W")){
                                type = 2;
                            }
                            if(addressObject.getString("address_type").equals("O")){
                                type = 3;
                            }
                            contactInfo.addAddress(addressObject.getString("physical_address"), type);
                        }
                        JSONArray ims_objects = object.getJSONArray("ims");
                        for(int j = 0; j < ims_objects.length(); j++){
                            JSONObject imsObject = ims_objects.getJSONObject(j);
                            int type = 0;
                            if(imsObject.getString("address_type").equals("WL")){
                                type = 1;
                            }
                            if(imsObject.getString("address_type").equals("YA")){
                                type = 2;
                            }
                            if(imsObject.getString("address_type").equals("SK")){
                                type = 3;
                            }
                            if(imsObject.getString("address_type").equals("QQ")){
                                type = 4;
                            }
                            if(imsObject.getString("address_type").equals("HA")){
                                type = 5;
                            }
                            if(imsObject.getString("address_type").equals("IC")){
                                type = 6;
                            }
                            if(imsObject.getString("address_type").equals("JA")){
                                type = 7;
                            }
                            if(imsObject.getString("address_type").equals("O")){
                                type = -1;
                            }
                            contactInfo.addIMAddress(imsObject.getString("im_address"), type);
                        }
                        JSONArray event_objects = object.getJSONArray("events");
                        for(int j = 0; j < event_objects.length(); j++){
                            JSONObject eventObject = event_objects.getJSONObject(j);
                            int type = 3;
                            if(eventObject.getString("event_type").equals("A")){
                                type = 1;
                            }
                            if(eventObject.getString("event_type").equals("O")){
                                type = 2;
                            }
                            contactInfo.addEvent(eventObject.getString("event_date"), type);
                        }
                        contactInfo.setCompany(new Company(object.getString("company"), object.getString("title")));
                        if(object.getString("trash").equals("False")){
                            contactInfo.setmTrash("untrash");
                        }
                        else{
                            contactInfo.setmTrash("trash");
                        }
                        contactInfo.setmSync("sync");
                        contactInfo.setmRemove("unremove");
                        responseContactList.add(contactInfo);
                    }
                    activity.response_uploadContactList = responseContactList;
                    activity.syncProcess2();
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                activity.syncDialog.dismiss();
                if(activity.autoSync_flag){
                    activity.stopRepeatingAutoSync();
                    activity.mInterval = 1 * 60 * 60 * 1000;
                    activity.startRepeatingAutoSync();
                }
                else{
                    Toast.makeText(activity.getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void backupContact(final Context context, final ContactInfo contact, String token) throws JSONException, UnsupportedEncodingException {
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("name_prefix", contact.getNamePrefix());
        object.put("first_name", contact.getFirstName());
        object.put("middle_name", contact.getMiddleName());
        object.put("last_name", contact.getLastName());
        object.put("name_suffix", contact.getNameSuffix());
        object.put("full_name", contact.getFullName());
        object.put("trash", "False");
        object.put("id", contact.getUid());
        JSONArray phone_objects = new JSONArray();
        for(int j = 0; j < contact.getPhones().size(); j++){
            JSONObject phoneObject = new JSONObject();
            phoneObject.put("number", contact.getPhones().get(j).getNumber());
            int type = contact.getPhones().get(j).getType();
            switch (type){
                case 1:
                    phoneObject.put("type", "H");
                    break;
                case 2:
                    phoneObject.put("type", "C");
                    break;
                case 3:
                    phoneObject.put("type", "W");
                    break;
                case 4:
                    phoneObject.put("type", "FW");
                    break;
                case 5:
                    phoneObject.put("type", "FH");
                    break;
                case 6:
                    phoneObject.put("type", "P");
                    break;
                case 7:
                    phoneObject.put("type", "O");
                    break;
                case 12:
                    phoneObject.put("type", "M");
                    break;
            }
            phoneObject.put("contact", 0);
            phoneObject.put("primary_number", false);
            phone_objects.put(phoneObject);
        }
        object.put("numbers", phone_objects);
        JSONArray email_objects = new JSONArray();
        for(int j = 0; j < contact.getEmails().size(); j++){
            JSONObject emailObject = new JSONObject();
            emailObject.put("email_address", contact.getEmails().get(j).getEmail());
            int type = contact.getEmails().get(j).getType();
            switch (type){
                case 1:
                    emailObject.put("email_type", "H");
                    break;
                case 2:
                    emailObject.put("email_type", "W");
                    break;
                case 3:
                    emailObject.put("email_type", "O");
                    break;
            }
            emailObject.put("contact", 0);
            email_objects.put(emailObject);
        }
        object.put("emails", email_objects);
        JSONObject companyObject = new JSONObject();
        companyObject.put("organizatin", contact.getCompany().getOrganization());
        companyObject.put("title", contact.getCompany().getTitle());
//            object.put("company", companyObject);
        object.put("company", contact.getCompany().getOrganization());
        object.put("title", contact.getCompany().getTitle());
        JSONArray address_objects = new JSONArray();
        for(int j = 0; j < contact.getAddressList().size(); j++){
            JSONObject addressObject = new JSONObject();
            addressObject.put("physical_address", contact.getAddressList().get(j).getAddressName());
            int type = contact.getAddressList().get(j).getAddressType();
            switch (type){
                case 1:
                    addressObject.put("address_type", "H");
                    break;
                case 2:
                    addressObject.put("address_type", "W");
                    break;
                case 3:
                    addressObject.put("address_type", "O");
                    break;

            }
            addressObject.put("contact", 0);
            address_objects.put(addressObject);
        }
        object.put("addresses", address_objects);
        JSONArray imaddress_objects = new JSONArray();
        for(int j = 0; j < contact.getImAddressesList().size(); j++){
            JSONObject imaddressObject = new JSONObject();
            imaddressObject.put("im_address", contact.getImAddressesList().get(j).getImaddressName());
            int type = contact.getImAddressesList().get(j).getImaddressType();
            switch (type){
                case 1:
                    imaddressObject.put("address_type", "WL");
                    break;
                case 2:
                    imaddressObject.put("address_type", "YA");
                    break;
                case 3:
                    imaddressObject.put("address_type", "SK");
                    break;
                case 4:
                    imaddressObject.put("address_type", "QQ");
                    break;
                case 5:
                    imaddressObject.put("address_type", "HA");
                    break;
                case 6:
                    imaddressObject.put("address_type", "IC");
                    break;
                case 7:
                    imaddressObject.put("address_type", "JA");
                    break;
                case -1:
                    imaddressObject.put("address_type", "O");
                    break;
                case 0:
                    imaddressObject.put("address_type", "AI");
                    break;
            }
            imaddressObject.put("contact", 0);
            imaddress_objects.put(imaddressObject);
        }
        object.put("ims", imaddress_objects);
        JSONArray event_objects = new JSONArray();
        for(int j = 0; j < contact.getEvents().size(); j++){
            JSONObject eventObject = new JSONObject();
            eventObject.put("event_date", contact.getEvents().get(j).getEventDate());
            int type = contact.getEvents().get(j).getEventType();
            switch (type) {
                case 1:
                    eventObject.put("event_type", "A");
                    break;
                case 2:
                    eventObject.put("event_type", "O");
                    break;
                case 3:
                    eventObject.put("event_type", "B");
                    break;
            }
            eventObject.put("contact", 0);
            event_objects.put(eventObject);
        }
        object.put("events", event_objects);
        JSONArray group_object = new JSONArray();
        for(int j = 0; j < contact.getGroups().size(); j++){
            JSONObject groupObject = new JSONObject();
            groupObject.put("contact", 0);
            groupObject.put("group_name", contact.getGroups().get(j).getGroupName());
            group_object.put(groupObject);
        }
        object.put("groups", group_object);
        array.put(object);
        JSONObject postData = new JSONObject();
        postData.put("backupdata", array);
        String url = "/contacts/restore-contact/";
        HttpUtils.post(context, url, postData, token, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " +response.toString());
                SyncLab.get(context).deleteContact(contact);
                try {
                    ContactInfo contactInfo = new ContactInfo();
                    JSONArray result_array = response.getJSONArray("results");
                    for(int i = 0; i < result_array.length(); i++) {
                        JSONObject object = result_array.getJSONObject(i);
                        contactInfo.setId("");
                        contactInfo.setUid(object.getString("id"));
                        contactInfo.setNamePrefix(object.getString("name_prefix"));
                        contactInfo.setFirstName(object.getString("first_name"));
                        contactInfo.setMiddleName(object.getString("middle_name"));
                        contactInfo.setLastName(object.getString("last_name"));
                        contactInfo.setNameSuffix(object.getString("name_suffix"));
                        contactInfo.setFullName(object.getString("full_name"));
                        JSONArray number_objects = object.getJSONArray("numbers");
                        for (int j = 0; j < number_objects.length(); j++) {
                            JSONObject numberObject = number_objects.getJSONObject(j);
                            String number = numberObject.getString("number");
                            int type = 2;
                            if (numberObject.getString("type").equals("M")) {
                                type = 12;
                            }
                            if (numberObject.getString("type").equals("W")) {
                                type = 3;
                            }
                            if (numberObject.getString("type").equals("H")) {
                                type = 1;
                            }
                            if (numberObject.getString("type").equals("FW")) {
                                type = 4;
                            }
                            if (numberObject.getString("type").equals("FH")) {
                                type = 5;
                            }
                            if (numberObject.getString("type").equals("P")) {
                                type = 6;
                            }
                            if (numberObject.getString("type").equals("O")) {
                                type = 7;
                            }

                            contactInfo.addPhone(number, type);
                        }
                        JSONArray email_objects = object.getJSONArray("emails");
                        for (int j = 0; j < email_objects.length(); j++) {
                            JSONObject emailObject = email_objects.getJSONObject(j);
                            int type = 1;
                            if (emailObject.getString("email_type").equals("W")) {
                                type = 2;
                            }
                            if (emailObject.getString("email_type").equals("O")) {
                                type = 3;
                            }
                            contactInfo.addEmail(emailObject.getString("email_address"), type);
                        }
                        JSONArray address_objects = object.getJSONArray("addresses");
                        for (int j = 0; j < address_objects.length(); j++) {
                            JSONObject addressObject = address_objects.getJSONObject(j);
                            int type = 1;
                            if (addressObject.getString("address_type").equals("W")) {
                                type = 2;
                            }
                            if (addressObject.getString("address_type").equals("O")) {
                                type = 3;
                            }
                            contactInfo.addAddress(addressObject.getString("physical_address"), type);
                        }
                        JSONArray ims_objects = object.getJSONArray("ims");
                        for (int j = 0; j < ims_objects.length(); j++) {
                            JSONObject imsObject = ims_objects.getJSONObject(j);
                            int type = 0;
                            if (imsObject.getString("address_type").equals("WL")) {
                                type = 1;
                            }
                            if (imsObject.getString("address_type").equals("YA")) {
                                type = 2;
                            }
                            if (imsObject.getString("address_type").equals("SK")) {
                                type = 3;
                            }
                            if (imsObject.getString("address_type").equals("QQ")) {
                                type = 4;
                            }
                            if (imsObject.getString("address_type").equals("HA")) {
                                type = 5;
                            }
                            if (imsObject.getString("address_type").equals("IC")) {
                                type = 6;
                            }
                            if (imsObject.getString("address_type").equals("JA")) {
                                type = 7;
                            }
                            if (imsObject.getString("address_type").equals("O")) {
                                type = -1;
                            }
                            contactInfo.addIMAddress(imsObject.getString("im_address"), type);
                        }
                        JSONArray event_objects = object.getJSONArray("events");
                        for (int j = 0; j < event_objects.length(); j++) {
                            JSONObject eventObject = event_objects.getJSONObject(j);
                            int type = 3;
                            if (eventObject.getString("event_type").equals("A")) {
                                type = 1;
                            }
                            if (eventObject.getString("event_type").equals("O")) {
                                type = 2;
                            }
                            contactInfo.addEvent(eventObject.getString("event_date"), type);
                        }
                        contactInfo.setCompany(new Company(object.getString("company"), object.getString("title")));
                        if (object.getString("trash").equals("False")) {
                            contactInfo.setmTrash("untrash");
                        } else {
                            contactInfo.setmTrash("trash");
                        }
                        contactInfo.setmSync("sync");
                        contactInfo.setmRemove("unremove");
                        contactInfo.setmRemove("untrash");
                        contactInfo.setmRemove("unremove");
                    }
                    DetailFragment.getInstance().backUp(contactInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("apr_error: ", "statuscode=   " + statusCode + " : " + errorResponse.toString());
                DetailFragment.getInstance().backupDialog.dismiss();
                Toast.makeText(context, "backup failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void setCheckSum(final MainActivity activity, int checksum, String token){
        RequestParams rp = new RequestParams();
        rp.put("checksum", String.valueOf(checksum));
        String url = "/devicedetail/set-checksum/";
        HttpUtils.setToken(token);
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + response.toString());
//                activity.setSyncInfo();
                activity.saveSyncInfo();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                // called when response HTTP status is "200 OK"
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + res);
                activity.syncDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), "checksum is not registered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void changeCheckSum(final Context context, int checksum, String token){
        RequestParams rp = new RequestParams();
        rp.put("checksum", String.valueOf(checksum));
        String url = "/devicedetail/set-checksum/";
        HttpUtils.setToken(token);
        HttpUtils.post(url, rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + response.toString());
                DetailFragment.getInstance().saveSyncInfo();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String res) {
                // called when response HTTP status is "200 OK"
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("apr_error: ", "statuscode=   " + statusCode + " " + res);
                DetailFragment.getInstance().backupDialog.dismiss();
                Toast.makeText(context, "backup failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
