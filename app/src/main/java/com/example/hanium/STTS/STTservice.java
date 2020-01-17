package com.example.hanium.STTS;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class STTservice extends Service implements RecognitionListener {

    private final String TAG = "[MySTTService]";

    /* Speech Recognizer */
    private SpeechRecognizer speechRecognizer;
    private Intent intent;


    public STTservice() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        speechRecognizer.startListening(this.intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        speechRecognizer.destroy();
        super.onDestroy();
    }

    /**
     *
     * 여기서 부터가 STT 리스너얌
     *
     */
    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onBeginningOfSpeech: ");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech: ");
    }

    @Override
    public void onRmsChanged(float v) {

    }


    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech: ");
    }

    @Override
    public void onError(int i) {

    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i=0; i<results.size(); i++) {
            Log.d(TAG, "onResults: " + results.get(i));
        }
        Intent intent = new Intent("get_stt_result");
        intent.putStringArrayListExtra("result", results);

        sendBroadcast(intent);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }
}
