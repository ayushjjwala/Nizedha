package com.robustest.nizedha;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver{

    final SmsManager sms = SmsManager.getDefault();
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try{
            if(bundle!=null){
                final Object[] pdusobj = (Object[]) bundle.get("pdus");

                ContentValues values = new ContentValues();
                for(int i=0; i<pdusobj.length;i++){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusobj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String message = currentMessage.getDisplayMessageBody();

                    values.put("address", phoneNumber);
                    values.put("body",message);
                    context.getContentResolver().insert(Uri.parse("content://sms/inbox"),values);
                }
            }
        }catch (Exception e){
            Log.e("SmsReceiver","Exception smsReceiver" + e);
        }
    }
}