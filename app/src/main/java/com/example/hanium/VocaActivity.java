package com.example.hanium;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VocaActivity extends AppCompatActivity {
    private static final String TAG = "VocaActivity";
    Toolbar toolbar;
    ListView vocalist;
    TextView text;
    IntentData intentdata;
    String voca,data,date;
    String[] string;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voca_interface);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<String> list=new ArrayList<>();
        intentdata = (IntentData) getIntent().getParcelableExtra("data");
        date=intentdata.getDate();
        data=intentdata.getData();
        Log.d(TAG,date+","+data);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text=(TextView)findViewById(R.id.textView) ;
        vocalist=(ListView)findViewById(R.id.vocalist);

        db.collection("sentence").document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                              if(data.equals("s1")){
                                  voca =document.get("s1voca").toString();
                                  Log.d(TAG,voca);
                                  getSupportActionBar().setTitle("Script #1 voca");
                              }
                              else if(data.equals("s2")){
                                  voca=document.get("s2voca").toString();
                                  Log.d(TAG,voca);
                                  getSupportActionBar().setTitle("Script #2 voca");
                              }
                              string=voca.split("\\.");
                              Log.d(TAG,string[0]);
                              for(String temp:string){
                                  list.add(temp);
                                  Log.d(TAG,temp);
                              }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(VocaActivity.this, android.R.layout.simple_list_item_1,list) ;
                                vocalist.setAdapter(adapter);
                            } else {

                            }
                        }
                    }
                });



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
