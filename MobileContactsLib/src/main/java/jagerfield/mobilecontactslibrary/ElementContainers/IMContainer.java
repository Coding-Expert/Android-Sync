package jagerfield.mobilecontactslibrary.ElementContainers;

import android.database.Cursor;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

import jagerfield.mobilecontactslibrary.FieldElements.IMElements.IMElement;
import jagerfield.mobilecontactslibrary.FieldElements.IMElements.IMTypeElement;
import jagerfield.mobilecontactslibrary.Utilities.Utilities;

public class IMContainer {

    @Expose
    private IMElement im;
    @Expose
    private IMTypeElement imType;

    public IMContainer(Cursor cursor){
        im = new IMElement(cursor);
        imType = new IMTypeElement(cursor);
    }

    public static Set<String> getFieldColumns() {
        Set<String> columns = new HashSet<>();
        columns.add(IMElement.column);
        columns.add(IMTypeElement.column);

        return columns;
    }
    public String elementValue() {

        String result = Utilities.elementValue(im);
        return result;
    }
    public String getImType() {
        String result = Utilities.elementValue(imType);
        return result;
    }
}
