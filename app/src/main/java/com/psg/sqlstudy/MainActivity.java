package com.psg.sqlstudy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String[][] JOBS = {
            {"Naukri: PL/SQL Developer, Pune", "https://www.naukri.com/pl-sql-developer-jobs-in-pune"},
            {"Naukri: Oracle PL/SQL, banking", "https://www.naukri.com/oracle-pl-sql-developer-banking-jobs"},
            {"LinkedIn: PL/SQL banking (Easy Apply)", "https://www.linkedin.com/jobs/search/?keywords=PL%2FSQL%20Developer%20banking&location=Pune&f_AL=true"},
            {"LinkedIn: Data Engineer, Oracle ETL", "https://www.linkedin.com/jobs/search/?keywords=Data%20Engineer%20Oracle%20ETL&location=Pune&f_AL=true"},
            {"Indeed: PL/SQL Developer, finance", "https://in.indeed.com/jobs?q=pl+sql+developer+banking&l=Pune"},
            {"Foundit: Oracle PL/SQL Developer", "https://www.foundit.in/srp/results?query=oracle%20pl%20sql%20developer&locations=Pune"}
    };

    private TextView timesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        Scheduler.ensure(this);

        int pad = dp(16);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, dp(32), pad, dp(32));
        root.setBackgroundColor(Color.parseColor("#F3F5F4"));

        root.addView(text("SQL Study Daily", 24, true, "#13212B"));
        root.addView(text("Advanced Oracle SQL for banking and finance interviews", 14, false, "#5C6B74"));

        root.addView(space(16));
        root.addView(button("Solve a question now", "#0D6B82", v -> open(Notifier.APP_URL)));

        root.addView(space(20));
        root.addView(text("Today's reminders", 18, true, "#13212B"));
        timesView = text("", 15, false, "#13212B");
        root.addView(timesView);
        root.addView(text("4 new random times are picked every day between 9 am and 10 pm. They ring only on this phone.", 13, false, "#5C6B74"));
        root.addView(space(8));
        root.addView(button("Send a test notification", "#5C6B74", v -> {
            Notifier.show(this, 0);
            Toast.makeText(this, "Sent. If nothing shows, allow notifications for this app.", Toast.LENGTH_LONG).show();
        }));

        root.addView(space(20));
        root.addView(text("Apply to jobs", 18, true, "#13212B"));
        root.addView(text("Each button opens a ready-made search. Apply on the site with your saved profile.", 13, false, "#5C6B74"));
        root.addView(space(8));
        for (String[] j : JOBS) {
            final String url = j[1];
            root.addView(button(j[0], "#B5761B", v -> open(url)));
            root.addView(space(6));
        }

        root.addView(space(16));
        root.addView(text("Tip: in phone Settings, turn off battery optimisation for this app so reminders arrive on time.", 13, false, "#5C6B74"));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Scheduler.ensure(this);
        int[] t = Scheduler.timesForToday(this);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.length; i++) {
            if (i > 0) sb.append("   ");
            sb.append(Scheduler.format(t[i]));
        }
        timesView.setText(sb.toString());
    }

    private void open(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            Toast.makeText(this, "No browser found to open this link.", Toast.LENGTH_LONG).show();
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private TextView text(String s, int sp, boolean bold, String color) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(Color.parseColor(color));
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setPadding(0, dp(2), 0, dp(2));
        return t;
    }

    private View space(int h) {
        View v = new View(this);
        v.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h)));
        return v;
    }

    private Button button(String label, String color, View.OnClickListener l) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.parseColor(color));
        b.setOnClickListener(l);
        return b;
    }
}
