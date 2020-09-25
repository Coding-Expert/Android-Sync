package jagerfield.mobilecontactslibrary.FieldElements.IMElements;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class IMElement extends ElementParent {

    @Expose
    private String imString = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.Im.DATA;
    public static final String mime = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;

    public IMElement(Cursor cursor){
        elementType = getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    @Override
    public String getValue() {
        return imString;
    }

    @Override
    public void setValue(Cursor cursor) {
        if (cursor==null)
        {
            return;
        }

        imString = Utilities.getColumnIndex(cursor, column);

        if (imString == null)
        {
            imString = "";
        }
    }
}
