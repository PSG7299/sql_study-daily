package com.psg.sqlstudy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {
    private WebView web;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 33
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
        Scheduler.ensure(this);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        web.loadUrl("file:///android_asset/app.html");

        new Thread(() -> {
            if (Store.refresh(getApplicationContext())) {
                runOnUiThread(() -> web.evaluateJavascript("window.onFeedUpdated && onFeedUpdated()", null));
            }
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (web != null) web.evaluateJavascript("window.openToday && openToday()", null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Scheduler.ensure(this);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    /** Methods the page can call as Android.name(). */
    class Bridge {
        @JavascriptInterface public String bank() { return Store.bundled(MainActivity.this); }
        @JavascriptInterface public String feed() { return Store.cachedFeed(MainActivity.this); }

        @JavascriptInterface public String times() {
            int[] t = Scheduler.timesForToday(MainActivity.this);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < t.length; i++) {
                if (i > 0) sb.append(',');
                sb.append(Scheduler.format(t[i]));
            }
            return sb.toString();
        }

        @JavascriptInterface public void setNext(String title, String teaser) {
            getSharedPreferences("next", Context.MODE_PRIVATE).edit()
                    .putString("title", title).putString("teaser", teaser).apply();
        }

        @JavascriptInterface public void openUrl(String url) {
            runOnUiThread(() -> {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "No browser found to open this link.", Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface public void testNotification() {
            Notifier.show(MainActivity.this, 0);
        }

        @JavascriptInterface public void toast(String msg) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
        }
    }
}
