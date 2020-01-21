package com.govind.calldetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG_INCOMING_CALL = "Incoming call";
    private static final String TAG_ACTION = "ACTION_DATA_AVAILABLE";

    TelecomManager telecomManager;
    boolean callEnded;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {

        telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        try{
            if (Objects.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE), TelephonyManager.EXTRA_STATE_RINGING)){
                String phoneNum= intent.getStringExtra("incoming_number");
                Intent i = new Intent(TAG_ACTION);
                if (telecomManager!=null){
                    callEnded = telecomManager.endCall();
                }
                if (phoneNum != null && callEnded){
                    i.putExtra("missedCallNumber", phoneNum);
                    Log.d(TAG_INCOMING_CALL,phoneNum);
                    context.sendBroadcast(i);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
