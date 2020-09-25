package jagerfield.mobilecontactslibrary.FieldElements.CompanyElement;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import jagerfield.mobilecontactslibrary.Abstracts.ElementParent;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class CompanyNameElement extends ElementParent {

    @Expose
    private String companyName = "";
    @Expose
    private final String elementType;
    public static final String column = ContactsContract.CommonDataKinds.Organization.DATA;

    public CompanyNameElement(Cursor cursor){
        elementType = this.getClass().getSimpleName();
        setValue(cursor);
    }

    @Override
    public String getElementType() {
        return elementType;
    }

    @Override
    public String getValue() {
        if (companyName==null)
        {
            companyName="";
        }

        return companyName;
    }

    @Override
    public void setValue(Cursor cursor) {
        if (cursor==null)
        {
            return;
        }

        companyName = Utilities.getColumnIndex(cursor, column);

        if (companyName == null)
        {
            companyName = "";
        }
    }
    public interface IDCompanyNameElement
    {
        String getCompanyName();
    }
}
