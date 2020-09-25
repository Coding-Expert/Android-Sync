package jagerfield.mobilecontactslibrary.FieldElements.CompanyElement;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class CompanyTitleElement extends ElementParent {

    @Expose
    private String companyTitle = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.Organization.TITLE;

    public CompanyTitleElement(Cursor cursor){
        elementType = this.getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    @Override
    public String getValue() {
        if (companyTitle==null)
        {
            companyTitle="";
        }

        return companyTitle;
    }

    @Override
    public void setValue(Cursor cursor) {
        if (cursor==null)
        {
            return;
        }

        companyTitle = Utilities.getColumnIndex(cursor, column);

        if (companyTitle == null)
        {
            companyTitle = "";
        }
    }
    public interface IDCompanyTitleElement
    {
        String getCompanyTitle();
    }
}
