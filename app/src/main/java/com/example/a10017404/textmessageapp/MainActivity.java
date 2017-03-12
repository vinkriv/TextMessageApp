package com.example.a10017404.textmessageapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.TextView;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    BroadcastReceiver receiver;
    String stage=null;
    int responsetwo;
    String[] ReceivedOne = {"hi", "hello", "ohayo", "namaskar"};
    String[] ResponsesOne = {"Hi", "Whassup", "Hola", "Hello"};
    String[] ReceivedTwo = {"where", "major", "campus"};
    String[] ResponsesTwo = {"New Jersey", "School of Arts and Sciences", "Computer Science", "Livingston"};
    String[] ReceivedThree = {"bye", "see ya", "adios", "later"};
    String[] ResponsesThree = {"Catch you later", "Cya", "Bye", "Au revoir"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        textView = (TextView)findViewById(R.id.textView);
        receiver = new TextMonitor();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,filter);
    }

    public class TextMonitor extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[])bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for (int x=0;x<pdus.length;x++){
                messages[x]=SmsMessage.createFromPdu((byte[])pdus[x],bundle.getString("format"));
            }

            for (int x=0;x<4;x++){
                if (stage==null && messages[0].getMessageBody().toLowerCase().contains(ReceivedOne[x])){
                    stage="Greeting";
                    x=4;
                }
                else if ((stage=="Greeting" || stage=="Error") && messages[0].getMessageBody().toLowerCase().contains(ReceivedTwo[x])){
                    stage="Question";
                    responsetwo = x;
                    x=4;
                }
                else if ((stage=="Question" || stage=="Error") && messages[0].getMessageBody().toLowerCase().contains(ReceivedThree[x])){
                    stage="Farewell";
                    x=4;
                }
                else stage="Error";
            }

            textView.setText(stage);
            final SmsManager manager = SmsManager.getDefault();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int rand =  (int)(Math.random()*4);
                    if (stage=="Greeting"){
                        manager.sendTextMessage(messages[0].getOriginatingAddress(),null,ResponsesOne[rand],null,null);
                    }
                    else if (stage=="Question"){
                        manager.sendTextMessage(messages[0].getOriginatingAddress(),null,ResponsesTwo[responsetwo],null,null);
                    }
                    else if (stage=="Farewell"){
                        manager.sendTextMessage(messages[0].getOriginatingAddress(),null,ResponsesThree[rand],null,null);
                    }
                    else if (stage=="Error"){
                        manager.sendTextMessage(messages[0].getOriginatingAddress(),null,"Could we please move on?",null,null);
                    }
                }
            },2000);
        }

    }
}
