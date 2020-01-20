package com.example.hanium.Chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.hanium.HomeActivity;
import com.example.hanium.IntentData;
import com.example.hanium.Point;
import com.example.hanium.R;
import com.example.hanium.User;
import com.example.hanium.stringSimilar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private Button buttonSend;
    IntentData intentdata;
    String date,data;
    String[] aeng,akor,bkor,beng;
    Toolbar toolbar;
    int checkpoint=0;
    int cnt=0;
    private boolean side = false;
    private TextToSpeech tts;
    private boolean isAvailableToTTS = false;

    private Intent STTservice;
    private MyBroadcastReceiver myBroadCastReceiver;
    Handler mHandler = new Handler();

    //kde
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    int keynum, chatnum;
    boolean check=true;
    static String[] keyArray;
    int step = 0;
    Point point;
    User user;
    final ArrayList<String> study_record=new ArrayList<>();
    //Map<String, Object> record = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_interface);
        intentdata = (IntentData) getIntent().getParcelableExtra("data");
        date=intentdata.getDate();
        data=intentdata.getData();
        user = getIntent().getParcelableExtra("user");

        toolbar=(Toolbar)findViewById(R.id.chattool);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dialog");


        db.collection("sentence").document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            keyArray = document.getData().keySet().toArray(new String[document.getData().keySet().size()]);
                            Arrays.sort(keyArray);
                            if (document.exists()) {
                                if(data.equals("d")){
                                    //Log.d(TAG, document.getData().toString());
                                    keynum=0;
                                    while(keynum<keyArray.length) {
                                        map.put(keyArray[keynum],document.get(keyArray[keynum]).toString().trim());
                                        keynum = keynum+1;
                                    }
                                    Log.d(TAG,"key 값: "+map.keySet()+" value 값: "+map.values());
                                }
                            } else {

                                Log.d(TAG, "Error getting documents: ", task.getException());

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
                        for (int i = 0; i < deniedPermissions.size(); i++) {
                            Log.d(TAG, "onPermissionDenied: " + deniedPermissions.get(i));
                        }
                        Toast.makeText(ChatActivity.this, "권한을 허락해주셔야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{onBeginningOfSpeech:
            finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void initView(){
        chatnum=1;

        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(step>=4&&chatnum==3){
                    Toast.makeText(ChatActivity.this, "학습을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                    buttonSend.setText("<   학습 완료   >");
                    //Log.d(TAG,"아이디 : "+user.getId());
                    //record.put("check","done");
                    /*DocumentReference chRef = db.collection("User").document(user.getId());
                    study_record.add(date);
                    chRef.update("check","done")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                }
                            });*/
                    String study_date;
                    ArrayList<String> tmp=new ArrayList();
                    study_date = date;
                    if(user.getRecord() == null){
                        Log.d(TAG,"Record==null");
                        tmp.add(study_date);
                        Toast.makeText(ChatActivity.this, "학습 완료", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        tmp=user.getRecord();
                        Log.d(TAG,"add");
                        tmp.add(study_date);
                        Toast.makeText(ChatActivity.this, "학습완료", Toast.LENGTH_SHORT).show();
                    }
                    tmp.remove("");
                    String s=String.join(",",tmp);
                    Log.d(TAG,"추가될 record : "+s);
                    db.collection("User")
                            .document(user.getId())
                            .update("Record",s);
                    user.updateRecord();
                }
                else{
                    if(step%2==0) {
                        for (cnt = 0; cnt <= 9; cnt = cnt + 2) {
                            if (keyArray[cnt].startsWith("a" + chatnum) && (check == true)) {
                                sendChatMessage(1,map.get(keyArray[cnt]).replace(". ",".\n") + "\n" + map.get(keyArray[cnt + 1]).replace(". ",".\n"));
                                speech(map.get(keyArray[cnt]));
                                check = (!check);
                                buttonSend.setText("<   Next   >");
                            }
                        }
                    }
                    else {
                        for (cnt = 0; cnt <= 9; cnt = cnt + 2) {
                            if ((keyArray[cnt].startsWith("b" + chatnum)) && check == false) {
                                sendChatMessage(2,map.get(keyArray[cnt]).replace(". ",".\n") + "\n" + map.get(keyArray[cnt + 1]).replace(". ",".\n"));
                                mHandler.postDelayed(mMyTask, 1000);
                                myBroadCastReceiver = new MyBroadcastReceiver();
                                IntentFilter intentFilter = new IntentFilter();
                                intentFilter.addAction("get_stt_result");
                                registerReceiver(myBroadCastReceiver, intentFilter);
                                STTservice = new Intent(ChatActivity.this, com.example.hanium.STTS.STTservice.class);
                                startService(STTservice);
                                checkpoint=cnt;
                                chatnum = chatnum + 1;
                                check = (!check);
                                buttonSend.setText("<   Next   >");
                            }
                        }
                    }
                    step++;

                }

            }
        });
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int isAvailable) {
                if (isAvailable == TextToSpeech.SUCCESS) {
                    int language = tts.setLanguage(Locale.ENGLISH);
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ChatActivity.this, "지원되지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
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
    Runnable mMyTask = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(ChatActivity.this, "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }
    };
    public void speech(String message) {
        if (isAvailableToTTS) {
            tts.speak(message.trim(), TextToSpeech.QUEUE_ADD, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        }
    }


    private boolean sendChatMessage(int side, String text) {
        chatArrayAdapter.add(new ChatMessage(side, text));
        //chatArrayAdapter.notifyDataSetChanged();
        return true;
    }
    @Override
    protected void onDestroy() {
        if(STTservice != null) {
            stopService(STTservice);
        }
        if (tts != null) {

            tts.stop();

            tts.shutdown();

        }
        super.onDestroy();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("get_stt_result")) {
                ArrayList<String> results = intent.getStringArrayListExtra("result");
                sendChatMessage(3,results.get(0));
                point= stringSimilar.stringsSimilar(map.get(keyArray[checkpoint]),results.get(0));
                sendChatMessage(3,Integer.toString(point.getS()));
                unregisterReceiver(myBroadCastReceiver);
                if(STTservice != null) {
                    stopService(STTservice);
                }
            }
        }
    }

}