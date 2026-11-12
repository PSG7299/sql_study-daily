# SQL Study Daily

Android app for daily advanced Oracle SQL and PL/SQL practice, focused on banking and finance interviews.

- 4 reminders a day at random times between 9 am and 10 pm, only on the phone it is installed on
- Tapping a reminder opens the question inside the app; no sign-in needed
- Streak, review of missed concepts, lessons, and job-search shortcuts
- Works offline with the built-in question bank; new questions download from `feed/feed.json`

## Install

1. On your phone, open the [latest release](../../releases/latest) and download `sql-study-daily.apk`.
2. Open the file and allow "Install unknown apps" when asked.
3. Open the app and allow notifications.

Every push to `main` rebuilds the APK and runs an emulator test through GitHub Actions.

## Feed format

`feed/feed.json` holds `{"questions": [...], "lessons": [...]}` using the same fields as `app/src/main/assets/bank.json`. New items are merged by `id`.
