package com.example.hanium.Chat;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hanium.R;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter {

    private TextView chatText;
    private List chatMessageList = new ArrayList();
    private LinearLayout singleMessageContainer;


    //@Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }


    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return (ChatMessage) this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.activity_chat_singlemessage, parent, false);
        }
        singleMessageContainer = (LinearLayout) row.findViewById(R.id.singleMessageContainer);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.singleMessage);
        chatText.setText(chatMessageObj.message);
        //chatText.setTextColor();
        if(position%3==2){
            chatText.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            chatText.setTextColor(Color.BLACK);
            singleMessageContainer.setGravity(Gravity.RIGHT);
        }
        else if(position%3==0){
            chatText.setBackgroundResource( R.drawable.bubble_com); //이미지 번갈아 출력
            singleMessageContainer.setGravity(Gravity.LEFT); //좌측&우측정렬 번갈아 실행
        }
        else{
            chatText.setBackgroundResource(R.drawable.bubble_user); //이미지 번갈아 출력
            singleMessageContainer.setGravity(Gravity.RIGHT ); //좌측&우측정렬 번갈아 실행
        }
        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}