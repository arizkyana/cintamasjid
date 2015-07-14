package app.rasendriya.cintamasjid.alarm;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.utils.Constant;

public class AlarmService extends Service {
    private String title;
    private String time;
    private String notif;

    private OnRingtonePlayListener onRingtonePlayListener;

    public interface OnRingtonePlayListener {
        abstract void onRingtonePlay(String title, String time, String notif);
    }

    Ringtone r;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public AlarmService getService() {
            // Return this instance of LocalService so clients can call public
            // methods
            return AlarmService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
    }

    public void setOnRingtonePlayListener(
            OnRingtonePlayListener onRingtonePlayListener) {
        this.onRingtonePlayListener = onRingtonePlayListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            this.title = intent.getStringExtra("title");
            this.time = intent.getStringExtra("time");
            this.notif = intent.getStringExtra("notif");
            startRingtone();
        }

        return START_NOT_STICKY;
    }

    public void stopRingtone() {
        if (r != null)
            r.stop();
    }

    private void startRingtone() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String customUri = pref.getString(Constant.Pref.RINGTONE_URI, null);
        Uri alarmSound = null;
        if (customUri == null) {

            //			alarmSound = Uri.fromFile(new File("//assets/adzan.mp3"));
            alarmSound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.adzan);

            if (alarmSound == null) {
                alarmSound = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmSound == null) {
                    alarmSound = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (alarmSound == null) {
                        alarmSound = RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    }
                }
                //			}
            } else {
                alarmSound = Uri.parse(customUri);
            }
            r = RingtoneManager.getRingtone(this, alarmSound);
            r.play();

            if (onRingtonePlayListener != null) {
                onRingtonePlayListener.onRingtonePlay(title, time, notif);
            }
        }
    }

    public boolean isRingtoneActive() {
        if (r != null && r.isPlaying())
            return true;
        else
            return false;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTime() {
        return this.time;
    }

    public String getNotif() {
        return this.notif;
    }

}