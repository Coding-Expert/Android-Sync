package com.basicphones.sync.database;

public class SyncDbSchema {
    public static final class TokenTable {
        public static final String NAME = "token";

        public static final class Cols {
            public static final String AccountNumber = "number";
            public static final String Authentication = "tokeninfo";
        }
    }
    public static final class CheckSumTable {
        public static final String NAME = "checksum";
        public static final class Cols {
            public static final String CheckSum = "contactsum";
            public static final String UpdateSyncDate = "date";
        }
    }
    public static final class PersonTable {
        public static final String NAME = "person";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String NAMEPREFIX = "prefix";
            public static final String FIRSTNAME = "firstname";
            public static final String MIDDLENAME = "middlename";
            public static final String LASTNAME = "lastname";
            public static final String NAMESUFFIX = "namesuffix";
            public static final String FULLNAME = "fullname";
            public static final String TRASH = "trash";
            public static final String SYNC = "sync";
            public static final String REMOVE = "remove";
        }
    }
    public static final class PhoneTable {
        public static final String NAME = "phones";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String PHONE = "phone";
            public static final String TYPE = "type";
        }
    }
    public static final class EmailTable {
        public static final String NAME = "emails";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String EMAIL = "email";
            public static final String TYPE = "type";
        }
    }
    public static final class CompanyTable {
        public static final String NAME = "company";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String ORGANIZATION = "organization";
            public static final String TITLE = "title";
        }
    }
    public static final class AddressTable {
        public static final String NAME = "addresses";

        public static final class Cols{
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String ADDRESSNAME = "name";
            public static final String ADDRESSTYPE = "type";
        }
    }
    public static final class IMAddressTable {
        public static final String NAME = "imaddress";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String IMADDRESSNAME = "name";
            public static final String IMADDRESSTYPE = "type";
        }
    }
    public static final class EventTable {
        public static final String NAME = "events";

        public static final class Cols {
            public static final String ID = "id";
            public static final String UID = "uid";
            public static final String EVENTDATE = "date";
            public static final String EVENTTYPE = "type";
        }
    }
}
