package com.example.clockapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private List<Alarm> alarmList;
    private Context context;  // Toast mesajı için context gerekli

    public AlarmAdapter(List<Alarm> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.alarmTime.setText(String.format("%02d:%02d", alarm.getHour(), alarm.getMinute()));
        holder.alarmLabel.setText(alarm.getLabel());

        // Silme butonu tıklama işlemi
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();  // Güncel pozisyonu al
                if (currentPosition != RecyclerView.NO_POSITION) {
                    removeAlarm(currentPosition);  // Alarmı sil
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    // Alarm silme işlemi
    public void removeAlarm(int position) {
        Alarm alarmToRemove = alarmList.get(position); // Silinecek alarmı al
        alarmList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, alarmList.size()); // Kalan öğeleri güncelle
        ((MainActivity) context).saveAlarmsToPreferences(); // Alarmları kaydet

        // PendingIntent'i iptal et
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null) {
            ((MainActivity) context).getAlarmManager().cancel(pendingIntent); // Alarmı iptal et
        }

        // "Alarm silindi" mesajı göster
        Toast.makeText(context, "Alarm silindi", Toast.LENGTH_SHORT).show();

        ((MainActivity) context).updateNoAlarmsText();
    }


    public void addAlarm(Alarm alarm) {
        alarmList.add(alarm);
        notifyItemInserted(alarmList.size() - 1);
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView alarmTime;
        TextView alarmLabel;
        Button deleteButton;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_time);
            alarmLabel = itemView.findViewById(R.id.alarm_label);
            deleteButton = itemView.findViewById(R.id.delete_button); // Silme butonu referansı
        }
    }
}
