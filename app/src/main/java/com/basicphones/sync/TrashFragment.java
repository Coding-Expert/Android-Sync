package com.basicphones.sync;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrashFragment extends Fragment {

    private View view;
    private RecyclerView trashRecyclerView;
    private List<ContactInfo> contacts;
    private ContactAdapter mAdapter;

    public TrashFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.trash_fragment,container, false);
        trashRecyclerView = (RecyclerView) view.findViewById(R.id.trash_recycler_view);
        trashRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        contacts = new ArrayList<>();
        List<ContactInfo> allContacts = SyncLab.get(getActivity()).getContacts();
        if(allContacts.size() > 0){
            for(int i = 0; i < allContacts.size(); i++){
                ContactInfo contactInfo = allContacts.get(i);
                if(contactInfo.getmTrash().equals("trash") && !contactInfo.getmRemove().equals("remove")){
                    contacts.add(contactInfo);
                }
            }
        }
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(contacts);
            trashRecyclerView.setAdapter(mAdapter);

        } else {
            mAdapter.setContacts(contacts);
            mAdapter.notifyDataSetChanged();
        }
    }
    public class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mNameTextView;
        public TextView mNumberTextView;
        private ContactInfo mContact;
        private ContactAdapter mAdapter;
        public LinearLayout item_layout;
        public int select_pos = 0;

        public ContactHolder(View itemView, ContactAdapter adaptor ) {
            super(itemView);
            mNameTextView = (TextView) itemView.findViewById(R.id.textFullName);
            mNumberTextView = (TextView) itemView.findViewById(R.id.textPrimaryNumber);
            itemView.setOnClickListener(this);
            mAdapter=adaptor;
        }

        public void bindContact(ContactInfo contact, int position) {
            mContact = contact;
            mNameTextView.setText(mContact.getFullName());
            if(mContact.getPhones().size() > 0){
                mNumberTextView.setText(mContact.getPhones().get(0).getNumber());
            }
            else{
                mNumberTextView.setText("");
            }
            select_pos = position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), TrashDetail.class);
            intent.putExtra("trash_contact", mContact);
            startActivityForResult(intent, 1);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {

            return;
        }

        String returnValue = data.getStringExtra(TrashDetail.BACKUP_STATE);
        if(returnValue!=null) {
            if (returnValue.equals("0")){
                Toast.makeText(getActivity(), "delete",
                        Toast.LENGTH_SHORT).show();
            }
            else if (returnValue.equals("1")) {
                Toast.makeText(getActivity(), "backup",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    public class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<ContactInfo> mContacts;
        private List<String> sections;
        private LinearLayout layout;
        private int position;

        public ContactAdapter(List<ContactInfo> contacts) {
            mContacts = contacts;
            setHasStableIds(true);
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from( getActivity());
            View view = layoutInflater
                    .inflate(R.layout.trash_item, parent, false);
            return new ContactHolder(view,this);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            ContactInfo contact = mContacts.get(position);
            holder.bindContact(contact, position);

        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }

        public void setContacts(List<ContactInfo> contacts) {
            mContacts = contacts;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public int getItemViewType(int position)
        {
            return position;
        }

    }
}
