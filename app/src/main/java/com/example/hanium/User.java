package com.example.hanium;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class User implements Serializable, Parcelable {
    String id;
    String name;
    String cellphone;
    String mail;
    ArrayList<String> mark=new ArrayList<>();
    ArrayList<String> record=new ArrayList<>();

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
                                Log.d(TAG, "make user");
                                name=document.get("NAME").toString();
                                cellphone=document.get("Phone").toString();
                                mail=document.get("Email").toString();
                                mark.addAll(Arrays.asList(document.get("mark").toString().split(",")));
                                record.addAll(Arrays.asList(document.get("Record").toString().split(",")));
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
        mark= (ArrayList<String>)in.readSerializable();
        record= (ArrayList<String>)in.readSerializable();
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
        parcel.writeSerializable(mark);
        parcel.writeSerializable(record);
    }
    public String getId(){
        return this.id;
    }
    public ArrayList<String> getMark(){ return this.mark; }
    public void updateMark(){
        db.collection("User").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                        mark.clear();
                                        mark.addAll(Arrays.asList(document.get("Mark").toString().split(",")));
                                }
                            }
                        }
                });
    }
    public ArrayList<String> getRecord(){ return this.record; }
    public void updateRecord(){
        db.collection("User").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                record.clear();
                                record.addAll(Arrays.asList(document.get("Record").toString().split(",")));
                            }
                        }
                    }
                });
    }

}