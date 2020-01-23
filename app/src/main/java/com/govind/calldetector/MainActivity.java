package com.govind.calldetector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_ACTION = "ACTION_DATA_AVAILABLE";
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static final long TIME_TO_REFRESH_LIST = 1000;

    private RecyclerView rvMissedCall;
    private MissedCallAdapter adapter;
    private TextView txtPhoneNumber;
    PhoneStateReceiver phoneStateReceiver;
    private TextView txtNoCallLogs;
    TelephonyManager telephonyManager;
    private Timer timer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMissedCall = findViewById(R.id.rv_call_list);
        txtNoCallLogs = findViewById(R.id.txt_no_call_logs);
        txtPhoneNumber = findViewById(R.id.txt_phone_number);

        adapter = new MissedCallAdapter(new ArrayList<>());
        rvMissedCall.setAdapter(adapter);


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (checkAndRequestPermissions()) {
            if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                showToast("SIM not available");
            }
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                showToast("Can't make calls");
            }
        }

        phoneStateReceiver = new PhoneStateReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                final String action = intent.getAction();
                if (action.equals(TAG_ACTION)) {
                    String number = intent.getStringExtra("missedCallNumber");
                    showEmptyListMessage();
                    adapter.addItems(new UserNumber(number));
                    txtNoCallLogs.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        showEmptyListMessage();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  boolean checkAndRequestPermissions() {
        int permissionReadPhoneNumber = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS);
        int permissionReadSms = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionAnswerPhoneCalls = ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS);
        int permissionReadCallLogs = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadPhoneNumber != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_NUMBERS);
        }
        if (permissionReadSms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        registerReceiver(phoneStateReceiver, new IntentFilter(TAG_ACTION));
        txtPhoneNumber.setText(getPhoneNumber());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        unregisterReceiver(phoneStateReceiver);
    }

    private void showEmptyListMessage() {
        if (adapter.getItemCount() == 0) {
            txtNoCallLogs.setVisibility(View.VISIBLE);
        } else {
            txtNoCallLogs.setVisibility(View.GONE);
        }
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> adapter.refresh());
            }
        };
    }

    private void startTimer() {
        TimerTask timerTask = getTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 0, TIME_TO_REFRESH_LIST);
    }

    private void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    private String getPhoneNumber() {
        String phoneNumber = "";
        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            showToast("Don't have permissions");
        } else {
            try {
                //phoneNumber = PhoneNumberUtils.formatNumber(telephonyManager.getLine1Number(),"+91");
                phoneNumber = telephonyManager.getLine1Number();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return phoneNumber;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.set_calling_number) {
            enterNumberDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void enterNumberDialog() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setPadding(30, 30, 30, 30);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        final EditText edtPhone = new EditText(this);
        edtPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        edtPhone.setHint("Enter number");

        dialogLayout.addView(edtPhone);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set calling number")
                .setView(dialogLayout)
                .setPositiveButton("Set", (dialogInterface, i) -> showToast(edtPhone.getText().toString()))
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                .create();

        dialog.show();
    }
}
