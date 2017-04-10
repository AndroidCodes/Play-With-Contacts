package com.example.androidcodes.contactsexample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.androidcodes.contactsexample.R;
import com.example.androidcodes.contactsexample.model.ContactDetails;

import java.util.ArrayList;

/**
 * Created by peacock on 7/4/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.CustomViewHolder> {

    private Context context;

    private ArrayList<ContactDetails> contactDetailsList = null;

    public ContactListAdapter(Context context, ArrayList<ContactDetails> contactDetailsList) {

        this.context = context;

        this.contactDetailsList = contactDetailsList;

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View customView = LayoutInflater.from(context).inflate(R.layout.contact_list_item_layout,
                parent, false);

        return new CustomViewHolder(customView);

    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        holder.tv_name.setText(contactDetailsList.get(position).getName());

        holder.tv_number.setText(contactDetailsList.get(position).getNumber());

    }

    @Override
    public int getItemCount() {

        return contactDetailsList != null ? contactDetailsList.size() : 0;

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_number;

        public CustomViewHolder(View convertView) {
            super(convertView);

            tv_name = (TextView) convertView.findViewById(R.id.tv_name);

            tv_number = (TextView) convertView.findViewById(R.id.tv_number);

        }
    }
}
