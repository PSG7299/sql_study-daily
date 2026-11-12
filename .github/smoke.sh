#!/usr/bin/env bash
# Installs the app on the emulator and checks that it starts, schedules alarms and posts a notification.
set -euo pipefail
PKG=com.psg.sqlstudy

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell pm grant $PKG android.permission.POST_NOTIFICATIONS
adb logcat -c
adb shell am start -W -n $PKG/.MainActivity
sleep 8

echo "== 1. App is running =="
adb shell pidof $PKG

echo "== 2. No crash in log =="
if adb logcat -d | grep -A 12 "FATAL EXCEPTION" | grep -q "$PKG"; then
  adb logcat -d | grep -A 20 "FATAL EXCEPTION"
  exit 1
fi
echo "no crash"

echo "== 3. Alarms are scheduled =="
adb shell dumpsys alarm | grep -c "$PKG"

echo "== 4. Reminder receiver posts a notification =="
adb root
sleep 3
adb shell am broadcast -n $PKG/.AlarmReceiver --ei slot 0
sleep 4
adb shell dumpsys notification --noredact | grep -m1 "pkg=$PKG"

echo "== 5. Still no crash =="
if adb logcat -d | grep -A 12 "FATAL EXCEPTION" | grep -q "$PKG"; then
  adb logcat -d | grep -A 20 "FATAL EXCEPTION"
  exit 1
fi
echo "ALL CHECKS PASSED"
