package com.psg.sqlstudy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int slot = intent.getIntExtra("slot", -1);
        if (slot >= 0 && slot < Scheduler.SLOTS) {
            Notifier.show(context, slot);
        }
        Scheduler.ensure(context);
        final PendingResult pr = goAsync();
        final Context app = context.getApplicationContext();
        new Thread(() -> {
            Store.refresh(app);
            pr.finish();
        }).start();
    }
}
