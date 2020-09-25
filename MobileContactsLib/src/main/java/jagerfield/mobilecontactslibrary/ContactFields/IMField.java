package jagerfield.mobilecontactslibrary.ContactFields;

import android.database.Cursor;
import android.provider.ContactsContract;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.Set;

import jagerfield.mobilecontactslibrary.Abstracts.FieldParent;
import jagerfield.mobilecontactslibrary.ElementContainers.IMContainer;

public class IMField extends FieldParent {

    public final String fieldMime = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;
    @Expose
    private LinkedList<IMContainer> ims = new LinkedList<>();

    public IMField() {

    }

    @Override
    public Set<String> registerElementsColumns() {
        return IMContainer.getFieldColumns();
    }

    @Override
    public void execute(String mime, Cursor cursor) {
        if (mime.equals(fieldMime))
        {
            ims.add(new IMContainer(cursor));
        }
    }
    public LinkedList<IMContainer> getIms()
    {
        return ims;
    }
    public interface IIMField
    {
        public LinkedList<IMContainer> getIms();
    }
}
