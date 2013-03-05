package com.mayqlzu.apphere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
// import android.telephony.gsm.SmsMessage; which one? not sure

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SMSReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent recieved: " + intent.getAction());

        if (intent.getAction() == SMS_RECEIVED) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                // todo: think, why multiple not one?
                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    messages[i] = msg;
                    Log.i(TAG, "Message recieved: " 
                    		+ "from: " + msg.getOriginatingAddress() +
                    		" content: " + msg.getMessageBody());
                }
            }
        }
    }
}