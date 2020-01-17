package com.example.hanium;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;

public class User implements Serializable, Parcelable {
    String id;
    String name;
    String cellphone;
    String mail;

    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private static final String TAG = "User";
    public User(String id) {
        this.id = id;
        db.collection("User")
                .whereEqualTo("ID",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                name=document.get("NAME").toString();
                                cellphone=document.get("Phone").toString();
                                mail=document.get("Email").toString();
                            }
                        }

                    }
                });

    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        cellphone = in.readString();
        mail = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(cellphone);
        parcel.writeString(mail);
    }
    public String getId(){
        return this.id;
    }
}