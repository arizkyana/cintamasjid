package app.rasendriya.cintamasjid.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.activity.CmMainActivity;
import app.rasendriya.cintamasjid.fragment.ScheduleFragment;
import app.rasendriya.cintamasjid.utils.Constant;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        wl.acquire();

        showNotif(context, intent.getStringExtra("title"),
                intent.getStringExtra("time"), intent.getStringExtra("notif"));
        Intent service = new Intent(context, AlarmService.class);
        service.putExtra("title", intent.getStringExtra("title"));
        service.putExtra("time", intent.getStringExtra("time"));
        service.putExtra("notif", intent.getStringExtra("notif"));
        context.startService(service);

        wl.release();
    }

    @SuppressWarnings("deprecation")
    private long getNextAlarm(String strDate24H, String strDate24HTomorrow) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
            Date date;
            date = sdf.parse(strDate24H);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, date.getHours());
            cal.set(Calendar.MINUTE, date.getMinutes());
            cal.set(Calendar.SECOND, 0);

            Calendar calToday = Calendar.getInstance();
            calToday.setTimeInMillis(System.currentTimeMillis());
            if (cal.before(calToday)) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            // cal.set(Calendar.MINUTE, minute);
            // cal.set(Calendar.SECOND, 0);

            return cal.getTimeInMillis();
        } catch (ParseException e) {
            return 0;
        }

    }

    public void SetAlarm(Context context) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        List<Map<String, String>> tempListSchedule = new ArrayList<Map<String, String>>();
        List<Map<String, String>> tempListScheduleTomorrow = new ArrayList<Map<String, String>>();
        ScheduleFragment.setPrayTime(context, tempListSchedule);
        ScheduleFragment.setPrayTime(context, tempListScheduleTomorrow, true);
        for (int i = 0; i < 7; i++) {
            String key;
//			String keyNotif;
            switch (i) {
                case 0:
                    key = Constant.Pref.ALARM_IMSAK;
//				keyNotif = Constant.Pref.NOTIF_IMSAK;
                    break;
                case 1:
                    key = Constant.Pref.ALARM_SUBUH;
//				keyNotif = Constant.Pref.NOTIF_SUBUH;
                    break;
                case 2:
                    key = Constant.Pref.ALARM_SYURUQ;
//				keyNotif = Constant.Pref.NOTIF_SYURUQ;
                    break;
                case 3:
                    key = Constant.Pref.ALARM_DZUHUR;
//				keyNotif = Constant.Pref.NOTIF_DZUHUR;
                    break;
                case 4:
                    key = Constant.Pref.ALARM_ASAR;
//				keyNotif = Constant.Pref.NOTIF_ASAR;
                    break;
                case 5:
                    key = Constant.Pref.ALARM_MAGHRIB;
//				keyNotif = Constant.Pref.NOTIF_MAGHRIB;
                    break;
                case 6:
                    key = Constant.Pref.ALARM_ISYA;
//				keyNotif = Constant.Pref.NOTIF_ISYA;
                    break;

                default:
                    key = "";
//				keyNotif = "";
                    break;
            }

            if (pref.getBoolean(key, false)) {
                SetAlarm(
                        context,
                        i,
                        getNextAlarm(tempListSchedule.get(i).get("time"),
                                tempListScheduleTomorrow.get(i).get("time")),
                        tempListSchedule.get(i).get("title"));

            } else {
                CancelAlarm(context, i);

            }
        }
    }


    private void SetAlarm(Context context, int requestCode, long time,
                          String title) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("title", title);
        i.putExtra("time", DateFormat.format("kk:mm", new Date(time)));
        i.putExtra("notif", DateFormat.format("kk:mm", new Date(time)));
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, i,
                0);
        // Calendar cal = Calendar.getInstance();
        // cal.setTimeInMillis(date.getTime());
        // cal.add(Calendar.SECOND, 5);
        // cal.set(Calendar.HOUR_OF_DAY, hour);
        // cal.set(Calendar.MINUTE, minute);
        // cal.set(Calendar.SECOND, 0);

        Log.d("okta", title + DateFormat.format("kk:mm", new Date(time)));
        am.set(AlarmManager.RTC_WAKEUP, time, pi);

    }

    private void SetNotif(Context context, int requestCode, long time,
                          String title) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("title", title + " 15 menit lagi");
        i.putExtra("time", DateFormat.format("kk:mm", new Date(time)));
        i.putExtra("notif", DateFormat.format("kk:mm", new Date(time)));
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, i,
                0);
        // Calendar cal = Calendar.getInstance();
        // cal.setTimeInMillis(date.getTime());
        // cal.add(Calendar.SECOND, 5);
        // cal.set(Calendar.HOUR_OF_DAY, hour);
        // cal.set(Calendar.MINUTE, minute);
        // cal.set(Calendar.SECOND, 0);

        am.set(AlarmManager.RTC_WAKEUP, time, pi);

    }

    public void CancelAlarm(Context context, int requestCode) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode,
                intent, 0);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    @SuppressWarnings("deprecation")
    private void showNotif(Context context, String title, String message, String notif) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(ns);

        int icon = R.mipmap.ic_notif; // icon from resources
        CharSequence tickerText = title; // ticker-text
        long when = System.currentTimeMillis(); // notification time

        CharSequence contentTitle = title; // message title
        CharSequence contentText = message; // message text

        Intent notificationIntent = new Intent(context, CmMainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        // the next two lines initialize the Notification, using the
        // configurations above
        Notification notification = new Notification(icon, tickerText, when);

        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        mNotificationManager.notify(0, notification);
    }
}
