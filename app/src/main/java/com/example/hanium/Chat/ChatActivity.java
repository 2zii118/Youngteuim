package com.example.hanium.Chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
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

import com.example.hanium.IntentData;
import com.example.hanium.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    int nowIndex=0;
    int cnt=0;
    private boolean side = false;
    private TextToSpeech tts;
    private boolean isAvailableToTTS = false;

    private Intent STTservice;
    private MyBroadcastReceiver myBroadCastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_interface);
        intentdata = (IntentData) getIntent().getParcelableExtra("data");
        date=intentdata.getDate();
        data=intentdata.getData();

        aeng=new String[2];
        beng=new String[2];
        akor=new String[2];
        bkor=new String[2];

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
                            if (document.exists()) {
                              if(data.equals("d")){
                                    aeng[0]=document.get("a1eng").toString();
                                    aeng[1]=document.get("a2eng").toString();
                                    beng[0]=document.get("b1eng").toString();
                                    beng[1]=document.get("b2eng").toString();
                                    akor[0]=document.get("a1kor").toString();
                                    akor[1]=document.get("a2kor").toString();
                                    bkor[0]=document.get("b1kor").toString();
                                    bkor[1]=document.get("b2kor").toString();
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

        //buttonSend = (Button) findViewById(R.id.buttonSend);

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
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void initView(){
        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setText("<   시작하기   >");
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cnt>=4) {
                    Toast.makeText(ChatActivity.this, "학습을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        buttonSend.setText("<   학습 완료   >");
                    }
                else{
                    if(cnt%2==0) {
                        String txt = aeng[nowIndex];
                        String kor=akor[nowIndex];
                        Log.d(TAG, "시작");
                        speech(txt);
                        sendChatMessage(txt+"\n"+kor);
                        buttonSend.setText("<   말하기   >");
                        nowIndex++;
                    }
                    else{
    //                    myBroadCastReceiver = new MyBroadcastReceiver();
    //                    IntentFilter intentFilter = new IntentFilter();
    //                    intentFilter.addAction("get_stt_result");
    //                    registerReceiver(myBroadCastReceiver, intentFilter);
    //                    STTservice = new Intent(ChatActivity.this, com.example.hanium.STTS.STTservice.class);
    //                    startService(STTservice);
                            sendChatMessage("일단 패스");
                            sendChatMessage("점수");
                            buttonSend.setText("<   다음 문장   >");

                    }
                    cnt++;
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
    public void speech(String message) {
        if (isAvailableToTTS) {
            tts.speak(message.trim(), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
        }
    }

    private boolean sendChatMessage(String text) {
        chatArrayAdapter.add(new ChatMessage(side, text));
        side = !side;
        return true;
    }
    @Override
    protected void onDestroy() {
        if(STTservice != null) {
            stopService(STTservice);
        }
        super.onDestroy();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("get_stt_result")) {
                ArrayList<String> results = intent.getStringArrayListExtra("result");
                sendChatMessage(results.toString());
                unregisterReceiver(myBroadCastReceiver);
                if(STTservice != null) {
                    stopService(STTservice);
                }
            }
        }
    }

}
