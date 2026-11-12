package com.psg.sqlstudy;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** Bundled question bank plus a daily feed downloaded from the GitHub repo. */
final class Store {
    static final String FEED_URL =
            "https://raw.githubusercontent.com/PSG7299/sql_study-daily/main/feed/feed.json";
    private static final String FEED_FILE = "feed.json";

    private Store() {}

    static String read(InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
        return out.toString(StandardCharsets.UTF_8.name());
    }

    static String bundled(Context c) {
        try {
            return read(c.getAssets().open("bank.json"));
        } catch (Exception e) {
            return "{\"questions\":[],\"lessons\":[]}";
        }
    }

    static String cachedFeed(Context c) {
        File f = new File(c.getFilesDir(), FEED_FILE);
        if (!f.exists()) return "{\"questions\":[],\"lessons\":[]}";
        try {
            return read(new java.io.FileInputStream(f));
        } catch (Exception e) {
            return "{\"questions\":[],\"lessons\":[]}";
        }
    }

    /** Downloads the daily feed. Returns true when new content was saved. Call off the main thread. */
    static boolean refresh(Context c) {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(FEED_URL + "?t=" + System.currentTimeMillis()).openConnection();
            con.setConnectTimeout(8000);
            con.setReadTimeout(8000);
            con.setUseCaches(false);
            if (con.getResponseCode() != 200) return false;
            String body = read(con.getInputStream());
            new org.json.JSONObject(body); // validate before saving
            if (body.equals(cachedFeed(c))) return false;
            try (FileOutputStream out = new FileOutputStream(new File(c.getFilesDir(), FEED_FILE))) {
                out.write(body.getBytes(StandardCharsets.UTF_8));
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (con != null) con.disconnect();
        }
    }
}
