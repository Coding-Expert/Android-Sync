package com.basicphones.sync;


import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Company;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.Group;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.ContactModel.Phone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ContactInfo implements Serializable {

    private String Id;
    private String uid;
    private String namePrefix;
    private String firstName;
    private String lastName;
    private String middleName;
    private String nameSuffix;
    private String fullName;
    private String mTrash;
    private String mSync;
    private String mRemove;
    private List<Phone> mPhones = new ArrayList<>();
    private List<Email> mEmails = new ArrayList<>();
    private Company company;
    private List<Address> addressList = new ArrayList<>();
    private List<IMAddress> imAddressesList = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    public ContactInfo(){

    }
    public ContactInfo(String id) {
        Id = id;
    }
    public void setUid(String m_uid){
        uid = m_uid;
    }
    public String getUid(){
        return uid;
    }
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String prefix) {
        namePrefix = prefix;
    }
    public void setFirstName(String first_name) {
        firstName = first_name;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setLastName(String last_name) {
        lastName = last_name;
    }
    public String getLastName(){
        return lastName;
    }
    public void setMiddleName(String middle_name) {
        middleName = middle_name;
    }
    public String getMiddleName() {
        return middleName;
    }
    public void setNameSuffix(String suffix) {
        nameSuffix = suffix;
    }
    public String getNameSuffix() {
        return nameSuffix;
    }
    public void setFullName(String full_name) {
        fullName = full_name;
    }
    public String getFullName() {
        return fullName;
    }

    public List<Phone> getPhones() {
        return mPhones;
    }

    public void setPhones(List<Phone> phones) {
        mPhones = phones;
    }

    public void addPhone(String phone, int type) {
        mPhones.add(new Phone(phone, type));
    }

    public List<Email> getEmails() {
        return mEmails;
    }

    public void setEmails(List<Email> emails) {
        mEmails = emails;
    }

    public void addEmail(String email, int type) {
        mEmails.add(new Email(email, type));
    }

    public String getPhotoFilename() {
        return "IMG_" + getId() + ".png";
    }

    public void setmTrash(String trash){
        mTrash = trash;
    }
    public String getmTrash() {
        return mTrash;
    }
    public void setmSync(String sync){
        mSync = sync;
    }
    public String getmSync(){
        return mSync;
    }
    public void setmRemove(String remove){
        mRemove = remove;
    }
    public String getmRemove(){
        return mRemove;
    }

    public void setCompany(Company m_company) {
        company = m_company;
    }
    public Company getCompany() {
        return company;
    }
    public void setAddressList(List<Address> m_addresslist){
        addressList = m_addresslist;
    }
    public List<Address> getAddressList() {
        return addressList;
    }
    public void addAddress(String address_name, int address_type){
        addressList.add(new Address(address_name, address_type));
    }
    public void setImAddressesList(List<IMAddress> m_imaddresslist){
        imAddressesList = m_imaddresslist;
    }
    public List<IMAddress> getImAddressesList(){
        return imAddressesList;
    }
    public void addIMAddress(String name, int type){
        imAddressesList.add(new IMAddress(name, type));
    }
    public void setEvents(List<Event> eventList){
        events = eventList;
    }
    public List<Event> getEvents(){
        return events;
    }
    public void addEvent(String date, int type){
        events.add(new Event(date, type));
    }
    public void setGroups(List<Group> groupList){
        groups = groupList;
    }
    public void addGroup(String id, String name){
        groups.add(new Group(id, name));
    }
    public List<Group> getGroups(){
        return groups;
    }

}
