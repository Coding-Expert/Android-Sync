package jagerfield.mobilecontactslibrary.FieldElements.AddressElement;

import android.database.Cursor;
import android.provider.ContactsContract;
import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;
import com.google.gson.annotations.Expose;

public class AddressTypeElement extends ElementParent {
    @Expose
    public String addressType = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.StructuredPostal.TYPE;
    public static final String mime = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;


    public AddressTypeElement(Cursor cursor) {
        elementType = this.getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getValue() {
        return elementType;
    }

    @Override
    public void setValue(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        addressType = Utilities.getAddressType(Utilities.getColumnIndex(cursor, column));

        if (addressType == null) {
            addressType = "OTHER";
        }
    }

    @Override
    public String getElementType()
    {
        return getClass().getSimpleName();
    }
}