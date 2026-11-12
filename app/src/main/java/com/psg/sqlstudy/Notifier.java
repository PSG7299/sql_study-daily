package com.psg.sqlstudy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

final class Notifier {
    private static final String CHANNEL = "sql_daily";
    private static final String[] LABELS = {
            "Q1 · Advanced SQL", "Q2 · Real-world scenario", "Q3 · Advanced SQL", "Q4 · Advanced SQL / PL/SQL"
    };

    private Notifier() {}

    static void show(Context c, int slot) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        if (nm == null) return;
        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL, "Daily SQL questions", NotificationManager.IMPORTANCE_HIGH));

        int i = Math.max(0, Math.min(slot, LABELS.length - 1));
        SharedPreferences p = c.getSharedPreferences("next", Context.MODE_PRIVATE);
        String title = p.getString("title", "");
        String teaser = p.getString("teaser", "");
        String heading = title.isEmpty() ? LABELS[i] : LABELS[i] + ": " + title;
        String body = teaser.isEmpty() ? "Your next question is ready. Tap to solve it." : teaser;

        Intent open = new Intent(c, MainActivity.class);
        open.putExtra("slot", i);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(c, 100 + i, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(heading)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();
        nm.notify(200 + i, n);
    }
}
