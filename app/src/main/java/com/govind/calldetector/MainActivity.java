package com.govind.calldetector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private RecyclerView rvMissedCall;
    private MissedCallAdapter adapter;
    TelephonyManager telephonyManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMissedCall = findViewById(R.id.rv_call_list);

        adapter = new MissedCallAdapter(new ArrayList<String>());
        rvMissedCall.setAdapter(adapter);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if(checkAndRequestPermissions()) {
            if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT){
                showToast("SIM not available");
            }
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
                showToast("Can't make calls");
            }
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);
        int permissionAnswerPhoneCalls = ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS);
        int permissionReadCallLogs = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionAnswerPhoneCalls != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ANSWER_PHONE_CALLS);
        }
        if (permissionReadCallLogs != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CALL_LOG);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
