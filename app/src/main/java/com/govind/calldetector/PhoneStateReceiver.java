package com.govind.calldetector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import java.util.Objects;

public class PhoneStateReceiver extends BroadcastReceiver {

    private final String TAG_INCOMING_CALL = "Incoming call";

    TelecomManager telecomManager;
    boolean callEnded;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onReceive(Context context, Intent intent) {

        telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);

        try{
            if (Objects.equals(intent.getStringExtra(TelephonyManager.EXTRA_STATE), TelephonyManager.EXTRA_STATE_RINGING)){
                String phoneNum= intent.getStringExtra("incoming_number");
                if (telecomManager!=null){
                    callEnded = telecomManager.endCall();
                }
                if (phoneNum != null && callEnded){
                    Log.d(TAG_INCOMING_CALL,phoneNum);
                    Toast.makeText(context, TAG_INCOMING_CALL+":"+phoneNum, Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
