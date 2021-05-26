package com.tom.my1922;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.PendingIntent;
import android.telephony.SmsManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String SMSTO = "SMSTO:";
    private IntentIntegrator scanIntegrator;
    /** Called when the activity is first created. */
    private EditText editTextReceiver;
    private EditText editTextContent;
    private Button buttonSend;
    private Button buttonCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------
        editTextReceiver = (EditText)findViewById(R.id.EditTextReceiver);
        editTextContent = (EditText)findViewById(R.id.EditTextContent);
        buttonSend = (Button)findViewById(R.id.ButtonSend);
        buttonCancel = (Button)findViewById(R.id.ButtonCancel);

        buttonSend.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String receiver = editTextReceiver.getText().toString();
                String text = editTextContent.getText().toString();
                //解析 SMSTO_1922  SMSTO:1922:
                if(text.startsWith(SMSTO)){
                    String newText = text.replace(SMSTO, "");
                    int secondColIndex = newText.indexOf(":");
                    if(secondColIndex >0 && secondColIndex<=10){
                        String toPhoneNum = newText.substring(0, secondColIndex);
                        if( isPhoneNum(toPhoneNum)){
                            newText = newText.substring(secondColIndex+1);
                            //解析格式符合 SMSTO:XXXX:則設定新值
                            receiver = toPhoneNum;
                            text = newText;

                            editTextReceiver.setText(toPhoneNum);
                            editTextContent.setText(newText);
                        }
                    }
                }
//                sendSms(receiver, text);
                sendSmsByIntent(receiver, text);
                //sendSmsWithPendingIntent(receiver, text);
            }
        });
        //---
    }

//    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private Pattern patternPhone = Pattern.compile("\\d+");
    public boolean isPhoneNum(String strNum) {
        if (strNum == null) {
            return false;
        }
        return patternPhone.matcher(strNum).matches();
    }
    private void sendSmsByIntent(String receiver, String text) {
        Intent intent= new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+receiver));
        intent.putExtra("sms_body", text);
        startActivity(intent);
    }
    private void sendSms(String receiver, String text) {
        //Toast.makeText(getApplicationContext(),"傳送SMS ", Toast.LENGTH_LONG).show();
        // TODO Auto-generated method stub
        SmsManager smsManager = SmsManager.getDefault();
        try{
            smsManager.sendTextMessage(receiver,
                    null, text,
                    PendingIntent.getBroadcast(getApplicationContext(), 0,new Intent(), 0),
                    null);
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"傳送SMS錯誤: "+e.getMessage(), Toast.LENGTH_LONG).show();
        };
    }
    private final static String SEND_ACTION      = "send";
    private final static String DELIVERED_ACTION = "delivered";

    /*
    版權聲明：本文為CSDN博主「YAnG_Linux」的原創文章，遵循CC 4.0 BY-SA版權協議，轉載請附上原文出處鏈接及本聲明。
    原文鏈接：https://blog.csdn.net/YUZHIBOYI/article/details/8484771
     */
    private void sendSmsWithPendingIntent(String receiver, String text) {
        SmsManager s = SmsManager.getDefault();
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SEND_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
        // 發送完成
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "簡訊發送成功 Send Success!", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "簡訊發送失敗 Send Failed because generic failure cause.",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "簡訊發送失敗 Send Failed because service is currently unavailable.",
                                Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "簡訊發送失敗 Send Failed because no pdu provided.", Toast.LENGTH_LONG).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "簡訊發送失敗 Send Failed because radio was explicitly turned off.",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "簡訊發送失敗 Send Failed.", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new IntentFilter(SEND_ACTION));

        // 對方接受完成
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "簡訊送達成功 Delivered Success!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "簡訊送達失敗 Delivered Failed!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED_ACTION));

        // 發送短信，sentPI和deliveredPI將分別在短信發送成功和對方接受成功時被廣播
        s.sendTextMessage(receiver, null, text, sentPI, deliveredPI);
    }


    public void onbuttonclick(View view) {
        View button1 = (View) findViewById(R.id.button);

        scanIntegrator = new IntentIntegrator(MainActivity.this);
        scanIntegrator.setPrompt("請掃描");
        scanIntegrator.setTimeout(300000);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null)
        {
            if(scanningResult.getContents() != null)
            {
                String scanContent = scanningResult.getContents();
                if (!scanContent.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"掃描內容: "+scanContent.toString(), Toast.LENGTH_LONG).show();
                    //掃描內容設定為簡訊內容
                    editTextContent.setText(scanContent.toString());
                }
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
            Toast.makeText(getApplicationContext(),"發生錯誤",Toast.LENGTH_LONG).show();
        }
    }
}
