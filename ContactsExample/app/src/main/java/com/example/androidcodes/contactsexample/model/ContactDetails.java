package com.example.androidcodes.contactsexample.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by peacock on 7/4/17.
 */

public class ContactDetails implements Parcelable {

    public static Parcelable.Creator<ContactDetails> CREATOR = new Creator<ContactDetails>() {

        @Override
        public ContactDetails createFromParcel(Parcel parcel) {

            return new ContactDetails(parcel);

        }

        @Override
        public ContactDetails[] newArray(int size) {

            return new ContactDetails[size];

        }
    };

    public ContactDetails() {
    }

    private String name, number;

    public ContactDetails(Parcel parcel) {

        this.name = parcel.readString();

        this.number = parcel.readString();

        //this.choices = parcel.readArrayList(null);

    }

    @Override
    public int describeContents() {

        return 0;

    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(name);

        parcel.writeString(number);

        //dest.writeList(choices);

    }

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }

    public String getNumber() {

        return number;

    }

    public void setNumber(String number) {

        this.number = number;

    }
}
