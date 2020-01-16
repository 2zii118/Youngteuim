package com.example.hanium;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class IntentData implements Serializable, Parcelable {
    String date;
    String data;


    public IntentData(){

    }

    protected IntentData(Parcel in) {
        date= in.readString();
        data = in.readString();
    }

    public static final Creator<IntentData> CREATOR = new Creator<IntentData>() {
        @Override
        public IntentData createFromParcel(Parcel in) {
            return new IntentData(in);
        }

        @Override
        public IntentData[] newArray(int size) {
            return new IntentData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(data);
    }
    public String getData(){
        return this.data;
    }
    public String getDate(){return this.date;}
    public void setDate(String date){this.date=date;}
    public void setData(String data){this.data=data;}
}