package com.psg.sqlstudy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/** Picks 4 random times a day between 9:00 and 22:00 and sets alarms for them. */
final class Scheduler {
    static final int SLOTS = 4;
    static final int ROLLOVER = 99;
    private static final int START_MIN = 9 * 60;
    private static final int END_MIN = 22 * 60;
    private static final String PREFS = "schedule";

    private Scheduler() {}

    static String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    /** Minutes after midnight for today's 4 reminders; generated once per day. */
    static int[] timesForToday(Context c) {
        SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String d = today();
        String csv = p.getString("times", "");
        if (!d.equals(p.getString("date", "")) || csv.isEmpty()) {
            Random r = new Random();
            int seg = (END_MIN - START_MIN) / SLOTS;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SLOTS; i++) {
                // one reminder per segment, kept 20 min clear of the next segment
                int m = START_MIN + i * seg + r.nextInt(seg - 20);
                if (i > 0) sb.append(',');
                sb.append(m);
            }
            csv = sb.toString();
            p.edit().putString("date", d).putString("times", csv).apply();
        }
        String[] parts = csv.split(",");
        int[] out = new int[parts.length];
        for (int i = 0; i < parts.length; i++) out[i] = Integer.parseInt(parts[i]);
        return out;
    }

    /** Sets alarms for today's remaining reminders and one just after midnight to plan tomorrow. */
    static void ensure(Context c) {
        AlarmManager am = c.getSystemService(AlarmManager.class);
        if (am == null) return;
        int[] t = timesForToday(c);
        Calendar now = Calendar.getInstance();
        for (int i = 0; i < t.length; i++) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, t[i] / 60);
            cal.set(Calendar.MINUTE, t[i] % 60);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (cal.after(now)) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending(c, i));
            }
        }
        Calendar next = Calendar.getInstance();
        next.add(Calendar.DAY_OF_YEAR, 1);
        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 5);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.getTimeInMillis(), pending(c, ROLLOVER));
    }

    private static PendingIntent pending(Context c, int slot) {
        Intent i = new Intent(c, AlarmReceiver.class);
        i.putExtra("slot", slot);
        return PendingIntent.getBroadcast(c, slot, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    static String format(int minutes) {
        int h = minutes / 60, m = minutes % 60;
        String ampm = h >= 12 ? "pm" : "am";
        int h12 = h % 12 == 0 ? 12 : h % 12;
        return String.format(Locale.US, "%d:%02d %s", h12, m, ampm);
    }
}
