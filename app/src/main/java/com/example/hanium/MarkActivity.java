package com.example.hanium;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hanium.Chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class MarkActivity extends AppCompatActivity {
    private static final String TAG = "MarkActivity";
    Toolbar toolbar;
    ListView vocalist;
    TextView text;
    IntentData intentdata=new IntentData();
    String data,date;
    User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voca_interface);
        final Intent Script_intent = new Intent(MarkActivity.this, ScriptActivity.class);
        final Intent Chat_intent = new Intent(MarkActivity.this, ChatActivity.class);
        user = (User) getIntent().getParcelableExtra("user");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final ArrayList<String> list=new ArrayList<>();
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text=(TextView)findViewById(R.id.textView) ;
        vocalist=(ListView)findViewById(R.id.vocalist);
        getSupportActionBar().setTitle("Mark List");
        db.collection("User").document(user.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                list.addAll(Arrays.asList(document.get("Mark").toString().split(",")));
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MarkActivity.this, android.R.layout.simple_list_item_1,list) ;
                                vocalist.setAdapter(adapter);
                            } else {

                            }
                        }
                    }
                });
        vocalist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] s=list.get(position).split(":");
                date=s[0].trim();data=s[1].trim();
                if(data.equals("Script#1"))data="s1";
                else if(data.equals("Script#2"))data="s2";
                else if(data.equals("Dialog"))data="d";
                intentdata.setData(data);
                intentdata.setDate(date);
                if(data.equals("d")){
                    Chat_intent.putExtra("data", (Parcelable) intentdata);
                    startActivity(Chat_intent);
                }
                else {
                    Script_intent.putExtra("data", (Parcelable) intentdata);
                    startActivity(Script_intent);
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
