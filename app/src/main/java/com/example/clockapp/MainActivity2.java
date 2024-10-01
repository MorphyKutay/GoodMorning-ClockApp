package com.example.clockapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Environment;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    private TimePicker simpleTimePicker;
    private Button setAlarmButton, selectMusicButton;
    private AlarmManager alarmManager;
    private ArrayList<Alarm> alarmList;  // List to hold set alarms
    private String selectedMusicUri; // Selected music URI

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("GoodMorning");

        // Bind TimePicker and Buttons
        simpleTimePicker = findViewById(R.id.simpleTimePicker);
        setAlarmButton = findViewById(R.id.button);
        selectMusicButton = findViewById(R.id.selectMusicButton);

        // Set 24-hour format
        simpleTimePicker.setIs24HourView(true);

        // Create an instance of AlarmManager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Initialize alarm list
        alarmList = new ArrayList<>();

        // Set onClickListener for set alarm button
        setAlarmButton.setOnClickListener(view -> setAlarm());

        // Set onClickListener for select music button
        selectMusicButton.setOnClickListener(view -> checkPermissionAndSelectMusic());
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm() {
        // Get selected time from TimePicker
        int hour = simpleTimePicker.getHour();
        int minute = simpleTimePicker.getMinute();

        // Get current time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If selected time is in the past, schedule for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Create an alarm intent
        Intent intent = new Intent(MainActivity2.this, AlarmReceiver.class);
        intent.putExtra("musicUri", selectedMusicUri); // Pass the selected music URI
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity2.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // Show a toast to inform the user
        Toast.makeText(MainActivity2.this, "Alarm set for " + hour + ":" + String.format("%02d", minute), Toast.LENGTH_SHORT).show();

        // Add the alarm to the list
        alarmList.add(new Alarm(hour, minute));

        // Return the alarm list to MainActivity and finish this activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("alarmList", alarmList); // Send the list back
        setResult(RESULT_OK, resultIntent);
        finish(); // Close this activity
    }

    // Method to handle permission and select music based on Android version
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionAndSelectMusic() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // For Android 11 and above
            if (!Environment.isExternalStorageManager()) {
                // Request permission to manage external storage
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            } else {
                selectMusic(); // Proceed if permission granted
            }
        } else { // For Android versions below 11
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request storage permission
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                selectMusic(); // Proceed if permission already granted
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectMusic(); // Permission granted, proceed to select music
            } else {
                Toast.makeText(this, "Permission denied to read external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to select music from MediaStore
    private void selectMusic() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    // Override to handle the result of selected music
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedAudioUri = data.getData();
            selectedMusicUri = selectedAudioUri.toString(); // Save the selected music URI
            Toast.makeText(this, "Music selected: " + selectedMusicUri, Toast.LENGTH_SHORT).show();
        }
    }

    // Method to return the alarm list (can be used when necessary)
    public ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }
}
