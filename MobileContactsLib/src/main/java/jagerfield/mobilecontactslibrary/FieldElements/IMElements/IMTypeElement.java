package jagerfield.mobilecontactslibrary.FieldElements.IMElements;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class IMTypeElement extends ElementParent {

    @Expose
    private String imType = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.Im.PROTOCOL;
    public static final String mime = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;

    public IMTypeElement(Cursor cursor){
        elementType = getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    @Override
    public String getValue() {
        return imType;
    }

    @Override
    public void setValue(Cursor cursor) {
        if (cursor==null)
        {
            return;
        }

        imType = Utilities.getColumnIndex(cursor, column);

        if (imType == null)
        {
            imType = "";
        }
    }
}
