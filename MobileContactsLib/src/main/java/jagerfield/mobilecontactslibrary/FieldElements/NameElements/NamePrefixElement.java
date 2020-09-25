package jagerfield.mobilecontactslibrary.FieldElements.NameElements;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class NamePrefixElement extends ElementParent {

    @Expose
    private String namePrefix = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.StructuredName.PREFIX;
    public static final String mime = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;


    public NamePrefixElement(Cursor cursor) {
        elementType = getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }
    @Override
    public String getValue() { return namePrefix;  }
    @Override
    public void setValue(Cursor cursor)
    {
        if (cursor==null)
        {
            return;
        }

        namePrefix = Utilities.getColumnIndex(cursor, column);

        if (namePrefix == null)
        {
            namePrefix = "";
        }
    }

    public interface INamePrefixElement
    {
        String getNamePrefix();
    }
}
