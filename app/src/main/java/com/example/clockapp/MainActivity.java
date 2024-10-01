package com.example.clockapp;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private List<Alarm> alarmList;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // Toolbar'ı ActionBar olarak ayarlayın
        getSupportActionBar().setTitle("GoodMorning");

        // FAB butonunu bul
        FloatingActionButton fab = findViewById(R.id.fab_add);

        // RecyclerView'u bul ve ayarla
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Alarm listesini başlat
        alarmList = new ArrayList<>();
        loadAlarmsFromPreferences(); // Alarmları kayıttan yükle

        // AlarmAdapter oluştur ve RecyclerView'e bağla
        alarmAdapter = new AlarmAdapter(alarmList,this);
        recyclerView.setAdapter(alarmAdapter);

        // FAB butonuna tıklandığında yeni alarm eklemek için MainActivity2'ye git
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivityForResult(intent, 1); // Yeni alarm eklemek için sonuç bekle
            }
        });
        Intent serviceIntent = new Intent(this, AlarmService.class);
        startService(serviceIntent);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Alarm listesini sonuç olarak al
            ArrayList<Alarm> newAlarms = (ArrayList<Alarm>) data.getSerializableExtra("alarmList");
            if (newAlarms != null) {
                for (Alarm newAlarm : newAlarms) {
                    addAlarm(newAlarm);
                }
            }
        }
    }

    private void addAlarm(Alarm alarm) {
        if (alarm != null) {
            alarmAdapter.addAlarm(alarm);
            saveAlarmsToPreferences(); // Alarmları kaydet
            // Alarm eklendiğinde "Alarm yok" mesajını gizle
            TextView noAlarmText = findViewById(R.id.no_alarm_text);
            noAlarmText.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Alarm eklenemedi!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAlarmsFromPreferences() {
        TextView noAlarmText = findViewById(R.id.no_alarm_text);

        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("alarms", null);
        if (json != null) {
            Gson gson = new Gson();
            Alarm[] alarms = gson.fromJson(json, Alarm[].class);
            if (alarms != null) {
                for (Alarm alarm : alarms) {
                    alarmList.add(alarm);
                }
            }
        }

        // Alarm listesi yüklendikten sonra kontrol et
        if (alarmList.isEmpty()) {
            noAlarmText.setVisibility(View.VISIBLE); // Alarm yoksa görünür yap
        } else {
            noAlarmText.setVisibility(View.GONE); // Alarmlar varsa gizle
        }
    }



    public void saveAlarmsToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AlarmPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmList);
        editor.putString("alarms", json);
        editor.apply();
    }

    public void updateNoAlarmsText() {
        TextView noAlarmText = findViewById(R.id.no_alarm_text);
        noAlarmText.setVisibility(View.GONE);
        if (alarmList.isEmpty()) {
            noAlarmText.setVisibility(View.VISIBLE); // Alarm yok mesajını göster
        } else {
            noAlarmText.setVisibility(View.GONE); // Alarm yok mesajını gizle
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AlarmChannel";
            String description = "Channel for Alarm Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ALARM_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }


}
