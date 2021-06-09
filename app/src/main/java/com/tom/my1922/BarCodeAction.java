package com.tom.my1922;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.regex.Pattern;

public class BarCodeAction {
    private static final String SMSTO = "SMSTO:";
    private Activity activity;
    BarCodeAction(Activity activity){
        this.activity = activity;
    }
    public  void parseBarCode(String text){
        SmsInput smsInput = parseSms(text);

        if(smsInput!=null){
            sendSmsByIntent(smsInput.getReceiver(), smsInput.getMsg());
            return;
        }
        if(text.toLowerCase().startsWith("http://")||text.toLowerCase().startsWith("https://")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(text));
            activity.startActivity(browserIntent);
            return;
        }


    }

    public static SmsInput parseSms(String text) {
//        String origText = text; //可能引用到舊String
//        String origText = new String(text);
        String origText = ""+text;
        SmsInput smsInput = null;
        //解析 SMSTO_1922  SMSTO:1922:
        if(text.toUpperCase().startsWith(SMSTO)){
            String newText = text.substring(SMSTO.length());
           // Toast.makeText(activity, "newText substring: "+newText+"  ", Toast.LENGTH_LONG).show();
//            String newText = text.replace(SMSTO, "");//大寫
//            newText = newText.replace(SMSTO.toLowerCase(), "");//小寫
            int secondColIndex = newText.indexOf(":");
            if(secondColIndex >0 && secondColIndex<=10){
                String toPhoneNum = newText.substring(0, secondColIndex);
               // Toast.makeText(activity, "newText newText.substring(0, secondColIndex): "+newText+"  ", Toast.LENGTH_LONG).show();
                if( isPhoneNum(toPhoneNum)){
                    //解析格式符合 SMSTO:XXXX:則設定新值
                    String receiver = toPhoneNum;
//                    String msg = origText.substring(SMSTO.length()+toPhoneNum.length()+1);
                    //String msg = newText.substring(toPhoneNum.length()+1);
                    String msg = newText.replace(toPhoneNum+":", "");
                    //Toast.makeText(activity, "msg newText.replace(toPhoneNum: "+msg+"  ", Toast.LENGTH_LONG).show();
                    smsInput = new SmsInput(receiver, msg);
                }
            }
        }
        return smsInput;
    }

    //    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static Pattern patternPhone = Pattern.compile("\\d+");
    public static boolean isPhoneNum(String strNum) {
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

        //確保有簡訊程式
        /*
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
         */
//        Toast.makeText(activity,"call 送簡訊程式。。", Toast.LENGTH_LONG).show();
        activity.startActivity(intent);
    }
}
