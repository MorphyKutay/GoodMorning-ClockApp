package com.example.clockapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        // Servis oluşturulduğunda yapılacak işlemler
        Log.d("AlarmService", "Service Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Servis başlatıldığında yapılacak işlemler
        Log.d("AlarmService", "Service Started");

        // Burada alarmları kontrol edebilir veya diğer işlemleri yapabilirsiniz

        return START_STICKY; // Servisin yeniden başlatılmasını sağlar
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AlarmService", "Service Destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Bağlantı yok
    }
}
