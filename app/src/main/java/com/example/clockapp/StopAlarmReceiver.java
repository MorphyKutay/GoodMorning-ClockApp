package com.example.clockapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmReceiver.stopAlarm(); // Alarm durdurma fonksiyonunu çağır
    }
}
