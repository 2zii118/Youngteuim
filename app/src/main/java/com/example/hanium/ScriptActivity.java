package com.example.hanium;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScriptActivity extends AppCompatActivity {
    private static final String TAG = "ScriptActivity";
    Toolbar toolbar;
    ListView script,scriptrepeat;
    Button bt,bt1,repeatbt;
    private Intent STTservice;
    private TextToSpeech tts;
    private boolean isAvailableToTTS = false;
    private MyBroadcastReceiver myBroadCastReceiver;
    final ArrayList<String> list=new ArrayList<>();
    final ArrayList<String> liststt=new ArrayList<>();
    IntentData intentdata;
    String skor,seng,data,date;
    String[] stringe,stringk;
    ArrayAdapter<String> adapterstt,adapter;

    int cnt=-1,cnt1=-1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.script_interface);
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    intentdata = (IntentData) getIntent().getParcelableExtra("data");
    date=intentdata.getDate();
    data=intentdata.getData();
    Log.d(TAG,date+","+data);
    toolbar=(Toolbar)findViewById(R.id.scripttool);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    script=(ListView)findViewById(R.id.script);
    scriptrepeat=(ListView)findViewById(R.id.scriptrepeat);
        db.collection("sentence").document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(data.equals("s1")){
                                    seng =document.get("s1eng").toString();
                                    skor=document.get("s1kor").toString();
                                    getSupportActionBar().setTitle("Script #1");
                                }
                                else if(data.equals("s2")){
                                    seng =document.get("s2eng").toString();
                                    skor=document.get("s2kor").toString();
                                    getSupportActionBar().setTitle("Script #2");
                                }
                                stringe=seng.split("\\.");
                                stringk=skor.split("\\.");
                                Log.d(TAG,stringe.length+","+stringk.length);
                                for(int i=0;i<stringe.length;i++){
                                    list.add(" #"+(i+1)+"  "+stringe[i]);
                                    Log.d(TAG,Integer.toString(i));
                                }

                                adapterstt = new ArrayAdapter<String>(ScriptActivity.this, android.R.layout.simple_list_item_1,liststt) ;
                                scriptrepeat.setAdapter(adapterstt);
                                adapter = new ArrayAdapter<String>(ScriptActivity.this, android.R.layout.simple_list_item_1,list) ;
                                script.setAdapter(adapter);
                            } else {

                            }
                        }
                    }
                });
        TedPermission.with(this)
                .setPermissions(Manifest.permission.RECORD_AUDIO)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Log.d(TAG, "onPermissionGranted: ");
                        initView();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        for (int j = 0; j < deniedPermissions.size(); j++) {
                            Log.d(TAG, "onPermissionDenied: " + deniedPermissions.get(j));
                        }
                        Toast.makeText(ScriptActivity.this, "권한을 허락해주셔야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();





    }
    public void initView(){
        bt = (Button) findViewById(R.id.allListenbt);
        repeatbt=(Button)findViewById(R.id.repeatbt) ;
        bt1=(Button)findViewById(R.id.Listenbt);
        repeatbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBroadCastReceiver = new MyBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("get_stt_result");
                registerReceiver(myBroadCastReceiver, intentFilter);
                STTservice = new Intent(ScriptActivity.this, com.example.hanium.STTS.STTservice.class);
                startService(STTservice);
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,Integer.toString(list.size()));
                speech(seng);
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,Integer.toString(list.size()));
                cnt++;
                speech(stringe[cnt]);
                bt1.setText(" #"+(cnt+1)+" 듣기");
                if((cnt+1)>=stringe.length)cnt=-1;
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int isAvailable) {
                if (isAvailable == TextToSpeech.SUCCESS) {
                    int language = tts.setLanguage(Locale.ENGLISH);
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ScriptActivity.this, "지원되지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
                        isAvailableToTTS = false;
                    } else {
                        isAvailableToTTS = true;
                    }
                    tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String s) {
                            Log.d(TAG, "TTS On Start");
                        }

                        @Override
                        public void onDone(String s) {
                            Log.d(TAG, "TTS On Done");


                        }

                        @Override
                        public void onError(String s) {
                            Log.d(TAG, "TTS On Error");

                        }
                    });
                }
            }
        });
    }
    protected void onDestroy() {
        if(STTservice != null) {
            stopService(STTservice);
        }
        super.onDestroy();
    }
    public void speech(String message) {
        if (isAvailableToTTS) {
            tts.speak(message.trim(), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        }
    }
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("get_stt_result")) {
                ArrayList<String> results = intent.getStringArrayListExtra("result");
                liststt.add(results.get(0));
                cnt1++;
                Point point=stringSimilar.stringsSimilar(results.get(0),stringe[cnt1]);
                liststt.add(results.get(0)+"\n점수 : "+point.getS());
                adapterstt.notifyDataSetChanged();
                unregisterReceiver(myBroadCastReceiver);
                bt.setText(" #"+(cnt1+1)+" 듣기");
                if((cnt1-1)>=stringe.length)cnt1=-1;

                if(STTservice != null) {
                    stopService(STTservice);
                }
            }
        }
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
