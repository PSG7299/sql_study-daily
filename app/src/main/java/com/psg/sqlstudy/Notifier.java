package com.psg.sqlstudy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

final class Notifier {
    static final String APP_URL = "https://claude.ai/artifact/VWJDopynreVqgBj3S1FPHU";
    private static final String CHANNEL = "sql_daily";

    private static final String[] TITLES = {
            "Q1: Advanced SQL, banking scenario",
            "Q2: Real-world finance question",
            "Q3: Advanced SQL challenge",
            "Q4: Advanced SQL or PL/SQL build"
    };
    private static final String[] TEXTS = {
            "5 minutes now keeps your streak alive. Tap to solve.",
            "A manager needs a report. Can you write the query? Tap to solve.",
            "Interviewers at banks ask this kind. Tap to try it.",
            "Last one today. Finish 4/4 before you open Instagram."
    };

    private Notifier() {}

    static void show(Context c, int slot) {
        NotificationManager nm = c.getSystemService(NotificationManager.class);
        if (nm == null) return;
        nm.createNotificationChannel(new NotificationChannel(
                CHANNEL, "Daily SQL questions", NotificationManager.IMPORTANCE_HIGH));

        int i = Math.max(0, Math.min(slot, TITLES.length - 1));
        Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_URL));
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi = PendingIntent.getActivity(c, 100 + i, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new Notification.Builder(c, CHANNEL)
                .setSmallIcon(R.drawable.ic_stat)
                .setContentTitle(TITLES[i])
                .setContentText(TEXTS[i])
                .setStyle(new Notification.BigTextStyle().bigText(TEXTS[i]))
                .setAutoCancel(true)
                .setContentIntent(pi)
                .build();
        nm.notify(200 + i, n);
    }
}
