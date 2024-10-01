package com.example.clockapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "AlarmChannel";
    public static MediaPlayer mediaPlayer; // Static MediaPlayer

    @Override
    public void onReceive(Context context, Intent intent) {
        // Alarm zamanı geldiğinde çalışacak kod
        String musicUriString = intent.getStringExtra("musicUri");
        if (musicUriString != null) {
            playAlarmSound(context, musicUriString);
        } else {
            // Varsayılan alarm sesi kullan
            playDefaultAlarmSound(context);
        }

        // Send notification
        sendNotification(context);
    }


    private void playAlarmSound(Context context, String musicUriString) {
        Uri musicUri = Uri.parse(musicUriString);
        playMedia(context, musicUri);
    }

    private void playDefaultAlarmSound(Context context) {
        Uri defaultAlarmUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_sound); // Varsayılan alarm sesi URI'si
        playMedia(context, defaultAlarmUri);
    }

    private void playMedia(Context context, Uri musicUri) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(context, musicUri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(context, "Alarm çalıyor!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Alarm sesi çalınamadı!", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendNotification(Context context) {
        Intent stopIntent = new Intent(context, StopAlarmReceiver.class);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Alarm Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        builder.setContentTitle("Alarm Çaldı!")
                .setContentText("Alarmınızı durdurmak için buraya tıklayın.")
                .setSmallIcon(R.drawable.ic_alarm) // Bildirim simgesi
                .setContentIntent(stopPendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }

    public static void stopAlarm() { // Static durdurma metodu
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null; // mediaPlayer'ı null yap
        }
    }
}
