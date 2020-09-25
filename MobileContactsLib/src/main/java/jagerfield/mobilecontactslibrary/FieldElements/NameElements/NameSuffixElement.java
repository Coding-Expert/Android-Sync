package jagerfield.mobilecontactslibrary.FieldElements.NameElements;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class NameSuffixElement extends ElementParent {

    @Expose
    private String nameSuffix = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.StructuredName.SUFFIX;
    public static final String mime = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;


    public NameSuffixElement(Cursor cursor) {
        elementType = getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }
    @Override
    public String getValue() { return nameSuffix;  }
    @Override
    public void setValue(Cursor cursor)
    {
        if (cursor==null)
        {
            return;
        }

        nameSuffix = Utilities.getColumnIndex(cursor, column);

        if (nameSuffix == null)
        {
            nameSuffix = "";
        }
    }

    public interface INameSuffixElement
    {
        String getNameSuffix();
    }
}
