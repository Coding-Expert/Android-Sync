package com.basicphones.sync;

import com.basicphones.sync.ContactModel.Address;
import com.basicphones.sync.ContactModel.Email;
import com.basicphones.sync.ContactModel.Event;
import com.basicphones.sync.ContactModel.IMAddress;
import com.basicphones.sync.ContactModel.Phone;

public class CompareUtils {

    public static boolean compareFirstName(ContactInfo local_contact, ContactInfo server_contact){
        if(!local_contact.getFirstName().isEmpty() && !local_contact.getFirstName().equals("")){
            if(!server_contact.getFirstName().isEmpty() && !server_contact.getFirstName().equals("")){
                if(!local_contact.getFirstName().equals(server_contact.getFirstName())){
                    return false;
                }

            }
        }
        if(!local_contact.getFirstName().isEmpty() && !local_contact.getFirstName().equals("")){
            if(server_contact.getFirstName().isEmpty() && server_contact.getFirstName().equals("")){
                return false;
            }
        }
        if(local_contact.getFirstName().isEmpty() && local_contact.getFirstName().equals("")){
            if(!server_contact.getFirstName().isEmpty() && !server_contact.getFirstName().equals("")){
                return false;
            }
        }
        return true;
    }
    public static boolean comapareNamePrefix(ContactInfo local_contact, ContactInfo server_contact){
        if(!local_contact.getNamePrefix().isEmpty() && !local_contact.getNamePrefix().equals("")){
            if(!server_contact.getNamePrefix().isEmpty() && !server_contact.getNamePrefix().equals("")){
                if(!local_contact.getNamePrefix().equals(server_contact.getNamePrefix())){
                    return false;
                }

            }
        }
        if(!local_contact.getNamePrefix().isEmpty() && !local_contact.getNamePrefix().equals("")){
            if(server_contact.getNamePrefix().isEmpty() && server_contact.getNamePrefix().equals("")){
                return false;
            }
        }
        if(local_contact.getNamePrefix().isEmpty() && local_contact.getNamePrefix().equals("")){
            if(!server_contact.getNamePrefix().isEmpty() && !server_contact.getNamePrefix().equals("")){
                return false;
            }
        }
        return true;
    }
    public static boolean comapareLastName(ContactInfo local_contact, ContactInfo server_contact){
        if(!local_contact.getLastName().isEmpty() && !local_contact.getLastName().equals("")){
            if(!server_contact.getLastName().isEmpty() && !server_contact.getLastName().equals("")){
                if(!local_contact.getLastName().equals(server_contact.getLastName())){
                    return false;
                }

            }
        }
        if(!local_contact.getLastName().isEmpty() && !local_contact.getLastName().equals("")){
            if(server_contact.getLastName().isEmpty() && server_contact.getLastName().equals("")){
                return false;
            }
        }
        if(local_contact.getLastName().isEmpty() && local_contact.getLastName().equals("")){
            if(!server_contact.getLastName().isEmpty() && !server_contact.getLastName().equals("")){
                return false;
            }
        }
        return true;
    }
    public static boolean comapareMiddleName(ContactInfo local_contact, ContactInfo server_contact){
        if(!local_contact.getMiddleName().isEmpty() && !local_contact.getMiddleName().equals("")){
            if(!server_contact.getMiddleName().isEmpty() && !server_contact.getMiddleName().equals("")){
                if(!local_contact.getMiddleName().equals(server_contact.getMiddleName())){
                    return false;
                }

            }
        }
        if(!local_contact.getMiddleName().isEmpty() && !local_contact.getMiddleName().equals("")){
            if(server_contact.getMiddleName().isEmpty() && server_contact.getMiddleName().equals("")){
                return false;
            }
        }
        if(local_contact.getMiddleName().isEmpty() && local_contact.getMiddleName().equals("")){
            if(!server_contact.getMiddleName().isEmpty() && !server_contact.getMiddleName().equals("")){
                return false;
            }
        }
        return true;
    }

    public static boolean comapareNameSuffix(ContactInfo local_contact, ContactInfo server_contact){
        if(!local_contact.getNameSuffix().isEmpty() && !local_contact.getNameSuffix().equals("")){
            if(!server_contact.getNameSuffix().isEmpty() && !server_contact.getNameSuffix().equals("")){
                if(!local_contact.getNameSuffix().equals(server_contact.getNameSuffix())){
                    return false;
                }

            }
        }
        if(!local_contact.getNameSuffix().isEmpty() && !local_contact.getNameSuffix().equals("")){
            if(server_contact.getNameSuffix().isEmpty() && server_contact.getNameSuffix().equals("")){
                return false;
            }
        }
        if(local_contact.getNameSuffix().isEmpty() && local_contact.getNameSuffix().equals("")){
            if(!server_contact.getNameSuffix().isEmpty() && !server_contact.getNameSuffix().equals("")){
                return false;
            }
        }
        return true;
    }
    public static boolean comaparePhones(ContactInfo local_contact, ContactInfo server_contact){
        int difference_count = 0;
        if(local_contact.getPhones().size() > 0 && server_contact.getPhones().size() > 0){
            if(local_contact.getPhones().size() == server_contact.getPhones().size()){
                for(int k = 0; k < local_contact.getPhones().size(); k++){
                    Phone local_phone = local_contact.getPhones().get(k);
                    for(int m = 0; m < server_contact.getPhones().size(); m++){
                        Phone server_phone = server_contact.getPhones().get(m);
                        if(local_phone.getNumber().equals(server_phone.getNumber()) && local_phone.getType() == server_phone.getType()){
                            difference_count ++;
                        }

                    }
                }
                if(difference_count == local_contact.getPhones().size()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        if(local_contact.getPhones().size() > 0 && server_contact.getPhones().size() < 1){
            return false;
        }
        if(local_contact.getPhones().size() < 1 && server_contact.getPhones().size() > 0){
            return false;
        }
        return true;
    }

    public static boolean comapareEmails(ContactInfo local_contact, ContactInfo server_contact){
        int difference_count = 0;
        if(local_contact.getEmails().size() > 0 && server_contact.getEmails().size() > 0){
            if(local_contact.getEmails().size() == server_contact.getEmails().size()){
                for(int k = 0; k < local_contact.getEmails().size(); k++){
                    Email local_email = local_contact.getEmails().get(k);
                    for(int m = 0; m < server_contact.getEmails().size(); m++){
                        Email server_email = server_contact.getEmails().get(m);
                        if(local_email.getEmail().equals(server_email.getEmail()) && local_email.getType() == server_email.getType()){
                            difference_count ++;
                        }

                    }
                }
                if(difference_count == local_contact.getEmails().size()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        if(local_contact.getEmails().size() > 0 && server_contact.getEmails().size() < 1){
            return false;
        }
        if(local_contact.getEmails().size() < 1 && server_contact.getEmails().size() > 0){
            return false;
        }
        if(local_contact.getEmails().size() == 0 && server_contact.getEmails().size() == 0){
            return false;
        }
        return true;
    }
    public static boolean comapareCompany(ContactInfo local_contact, ContactInfo server_contact){
        String local_organization = local_contact.getCompany().getOrganization();
        String server_organization = server_contact.getCompany().getOrganization();
        String local_title = local_contact.getCompany().getTitle();
        String server_title = server_contact.getCompany().getTitle();
        if(!local_organization.isEmpty() && !local_organization.equals("")){
            if(!server_organization.isEmpty() && !server_organization.equals("")){
                if(!local_organization.equals(server_organization)){
                    return false;
                }

            }
        }
        if(!local_title.isEmpty() && !local_title.equals("")){
            if(!server_title.isEmpty() && !server_title.equals("")){
                if(!local_title.equals(server_title)){
                    return false;
                }

            }
        }
        if(!local_organization.isEmpty() && !local_organization.equals("")){
            if(server_organization.isEmpty() && server_organization.equals("")){
                return false;
            }
        }
        if(local_organization.isEmpty() && local_organization.equals("")){
            if(!server_organization.isEmpty() && !server_organization.equals("")){
                return false;
            }
        }
        if(!local_title.isEmpty() && !local_title.equals("")){
            if(server_title.isEmpty() && server_title.equals("")){
                return false;
            }
        }
        if(local_title.isEmpty() && local_title.equals("")){
            if(!server_title.isEmpty() && !server_title.equals("")){
                return false;
            }
        }
        return true;
    }

    public static boolean comapareAddress(ContactInfo local_contact, ContactInfo server_contact){
        int difference_count = 0;
        if(local_contact.getAddressList().size() > 0 && server_contact.getAddressList().size() > 0){
            if(local_contact.getAddressList().size() == server_contact.getAddressList().size()){
                for(int k = 0; k < local_contact.getEmails().size(); k++){
                    Address local_address = local_contact.getAddressList().get(k);
                    for(int m = 0; m < server_contact.getAddressList().size(); m++){
                        Address server_address = server_contact.getAddressList().get(m);
                        if(local_address.getAddressName().equals(server_address.getAddressName()) && local_address.getAddressType() == server_address.getAddressType()){
                            difference_count ++;
                        }

                    }

                }
                if(difference_count == local_contact.getAddressList().size()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        if(local_contact.getAddressList().size() > 0 && server_contact.getAddressList().size() < 1){
            return false;
        }
        if(local_contact.getAddressList().size() < 1 && server_contact.getAddressList().size() > 0){
            return false;
        }

        return true;
    }
    public static boolean comapareIMAddress(ContactInfo local_contact, ContactInfo server_contact){
        int difference_count = 0;
        if(local_contact.getImAddressesList().size() > 0 && server_contact.getImAddressesList().size() > 0){
            if(local_contact.getImAddressesList().size() == server_contact.getImAddressesList().size()){
                for(int k = 0; k < local_contact.getImAddressesList().size(); k++){
                    IMAddress local_imaddress = local_contact.getImAddressesList().get(k);
                    for(int m = 0; m < server_contact.getImAddressesList().size(); m++){
                        IMAddress server_imaddress = server_contact.getImAddressesList().get(m);
                        if(local_imaddress.getImaddressName().equals(server_imaddress.getImaddressName()) && local_imaddress.getImaddressType() == server_imaddress.getImaddressType()){
                            difference_count ++;
                        }
                    }
                }
                if(difference_count == local_contact.getImAddressesList().size()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        if(local_contact.getImAddressesList().size() > 0 && server_contact.getImAddressesList().size() < 1){
            return false;
        }
        if(local_contact.getImAddressesList().size() < 1 && server_contact.getImAddressesList().size() > 0){
            return false;
        }

        return true;
    }
    public static boolean comapareEvents(ContactInfo local_contact, ContactInfo server_contact){
        int difference_count = 0;
        if(local_contact.getEvents().size() > 0 && server_contact.getEvents().size() > 0){
            if(local_contact.getEvents().size() == server_contact.getEvents().size()){
                for(int k = 0; k < local_contact.getEvents().size(); k++){
                    Event local_event = local_contact.getEvents().get(k);
                    for(int m = 0; m < server_contact.getEvents().size(); m++){
                        Event server_event = server_contact.getEvents().get(m);
                        if(local_event.getEventDate().equals(server_event.getEventDate()) && local_event.getEventType() == server_event.getEventType()){
                            difference_count ++;
                        }

                    }
                }
                if(difference_count == local_contact.getEvents().size()){
                    return true;
                }
                else{
                    return false;
                }
            }
            else{
                return false;
            }
        }
        if(local_contact.getEvents().size() > 0 && server_contact.getEvents().size() < 1){
            return false;
        }
        if(local_contact.getEvents().size() < 1 && server_contact.getEvents().size() > 0){
            return false;
        }

        return true;
    }

}
