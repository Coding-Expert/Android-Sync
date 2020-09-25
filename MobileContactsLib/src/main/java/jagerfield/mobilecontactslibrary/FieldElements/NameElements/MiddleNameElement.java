package jagerfield.mobilecontactslibrary.FieldElements.NameElements;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class MiddleNameElement extends ElementParent {

    @Expose
    private String middleName = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME;
    public static final String mime = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;

    public MiddleNameElement(Cursor cursor)
    {
        elementType = getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType()
    {
        return elementType;
    }
    @Override
    public String getValue() {

        if (middleName==null)
        {
            middleName="";
        }
        return middleName;
    }
    @Override
    public void setValue(Cursor cursor)
    {
        if (cursor==null)
        {
            return;
        }

        middleName = Utilities.getColumnIndex(cursor, column);

        if (middleName == null)
        {
            middleName = "";
        }
    }

    public interface IMiddleNameElement
    {
        String getMiddleName();
    }
}
