package com.example.hanium.STTS;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
public class STTSActivity extends AppCompatActivity {
    /**
     * Made By LSC
     */
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    private final String TAG = "[MyLog]";
    Button ttspeech,stt;
    TextView txtRead;
    TextView txtSpeech;

    private TextToSpeech tts;
    private boolean isAvailableToTTS = false;

    private Intent STTservice;
    private MyBroadcastReceiver myBroadCastReceiver;

    String date;
    String[] s1eng=null;
    private int nowIndex = 0;
    private int cnt = 0;
    /**
     * FiNISH
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stts_interface);
        date=getIntent().getStringExtra("date");
        db.collection("sentence").document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String s=document.get("s1eng").toString();
                                Log.d(TAG,s);
                                s1eng=document.get("s1eng").toString().split("\\.");
                                cnt=s1eng.length;
                                Log.d(TAG,"문장불러오기 성공"+cnt);
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
                        Toast.makeText(STTSActivity.this, "권한을 허락해주셔야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .check();
    }
    public void initView(){
        stt = findViewById(R.id.stt);
        stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myBroadCastReceiver = new MyBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("get_stt_result");
                registerReceiver(myBroadCastReceiver, intentFilter);
                STTservice = new Intent(STTSActivity.this, STTservice.class);
                startService(STTservice);
            }
        });

        ttspeech = (Button) findViewById(R.id.ttspeech);

        ttspeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nowIndex>=cnt)
                    Toast.makeText(STTSActivity.this, "학습을 완료하였습니다.", Toast.LENGTH_SHORT).show();
                else {
                    String txt = s1eng[nowIndex];
                    Log.d(TAG,"시작");
                    speech(txt);

                    txtRead.setText(txt);
                    nowIndex++;

                }
            }
        });
        txtRead = (TextView) findViewById(R.id.txtRead);
        txtSpeech=(TextView) findViewById(R.id.sttext);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int isAvailable) {
                if (isAvailable == TextToSpeech.SUCCESS) {
                    int language = tts.setLanguage(Locale.ENGLISH);
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(STTSActivity.this, "지원되지 않는 언어입니다.", Toast.LENGTH_SHORT).show();
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

                txtSpeech.setText(results.toString());
                unregisterReceiver(myBroadCastReceiver);
                if(STTservice != null) {
                    stopService(STTservice);
                }
            }
        }
    }


}