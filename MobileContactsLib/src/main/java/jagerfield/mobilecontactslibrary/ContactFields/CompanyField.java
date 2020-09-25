package jagerfield.mobilecontactslibrary.ContactFields;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import jagerfield.mobilecontactslibrary.Abstracts.FieldParent;
import jagerfield.mobilecontactslibrary.FieldElements.CompanyElement.CompanyNameElement;
import jagerfield.mobilecontactslibrary.FieldElements.CompanyElement.CompanyTitleElement;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;


public class CompanyField extends FieldParent {

    public final String fieldMime = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;

    @Expose
    private CompanyNameElement companyName;
    @Expose
    private CompanyTitleElement companyTitle;

    public CompanyField() {

    }

    @Override
    public Set<String> registerElementsColumns() {
        Set<String> columns = new HashSet<>();
        columns.add(CompanyNameElement.column);
        columns.add(CompanyTitleElement.column);
        return columns;
    }

    @Override
    public void execute(String mime, Cursor cursor) {
        if (mime.equals(fieldMime))
        {
            companyName = new CompanyNameElement(cursor);
            companyTitle = new CompanyTitleElement(cursor);

        }
    }
    public String getCompanyName(){
        String result = Utilities.elementValue(companyName);
        return result;
    }
    public String getCompanyTitle(){
        String result = Utilities.elementValue(companyTitle);
        return result;
    }
    public interface ICompanyField extends  CompanyNameElement.IDCompanyNameElement, CompanyTitleElement.IDCompanyTitleElement {

    }
}
