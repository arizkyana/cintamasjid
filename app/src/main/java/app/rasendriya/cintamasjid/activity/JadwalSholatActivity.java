package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.alarm.Alarm;
import app.rasendriya.cintamasjid.alarm.AlarmService;
import app.rasendriya.cintamasjid.alarm.AlarmService.LocalBinder;
import app.rasendriya.cintamasjid.alarm.AlarmService.OnRingtonePlayListener;
import app.rasendriya.cintamasjid.fragment.ScheduleFragment;
import app.rasendriya.cintamasjid.utils.Constant;
import app.rasendriya.cintamasjid.utils.MyLocation;
import app.rasendriya.cintamasjid.utils.MyLocation.LocationResult;

public class JadwalSholatActivity extends ActionBarActivity {

    private Context ctx = this;
    private SharedPreferences sharedPreferences;
    ProgressDialog dialog;

    AlarmService mService;
    boolean mBound = false;

    ScheduleFragment scheduleFragment;

    public MyLocation myLocation = new MyLocation();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 2, 0, "Refresh")
                .setIcon(R.drawable.ab_refresh)
                .setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM
                                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 2:
                dialog = ProgressDialog.show(ctx, "Please wait",
                        "Get your location...");
                checkLocation();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0: // searchlocation
                initPrayData();
                initTitle();
                break;
            case 5: // ringtone
                if (resultCode == RESULT_OK) {
                    Uri uri = data
                            .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                    if (uri != null) {
                        sharedPreferences
                                .edit()
                                .putString(Constant.Pref.RINGTONE_URI,
                                        uri.toString()).commit();
                    } else {
                        sharedPreferences.edit().remove(Constant.Pref.RINGTONE_URI)
                                .commit();
                    }
                }
                break;

            default:
                break;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal_sholat);

        scheduleFragment = ScheduleFragment.newInstance(0);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c2922")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        Alarm alarm = new Alarm();
        alarm.SetAlarm(this);

        checkLocation();
        initTitle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, AlarmService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.setOnRingtonePlayListener(new OnRingtonePlayListener() {

                @Override
                public void onRingtonePlay(String title, String time,
                                           String notif) {
                    // TODO Auto-generated method stub

                }
            });

            // cek alarm apakah nyala
            if (mService.isRingtoneActive()) {
                showAlarmDialog(mService.getTitle(), mService.getTime());
            } else {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
                mNotificationManager.cancelAll();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService.setOnRingtonePlayListener(null);
        }
    };

    private void showAlarmDialog(String label, String message) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        mNotificationManager.cancelAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(label).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mService.stopRingtone();
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        try {
            alert.show();

        } catch (Exception e) {
        }
    }

    public void checkLocation() {
        if (sharedPreferences.getString(Constant.Pref.LOC_LAT, "0").equals("0")) {
            dialog = ProgressDialog.show(ctx, "Please wait",
                    "Get your location...");
        }
        // GET LOCATION
        if (!myLocation.getLocation(this, new MyLocationResult())) {
            if (dialog != null)
                dialog.cancel();
            startSearchLocation();
        }
    }

    public void startSearchLocation() {
        if (sharedPreferences.getString(Constant.Pref.LOC_LAT, "0").equals("0")) {
            Toast.makeText(
                    ctx,
                    "We can't determine your location. Please type your location.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(
                    ctx,
                    "We can't determine your location. Now we use your last location",
                    Toast.LENGTH_LONG).show();
        }
    }

    public class MyLocationResult extends LocationResult {
        @Override
        public void gotLocation(final Location location) {
            if (dialog != null)
                dialog.cancel();

            if (location == null) {
                // jika ga dapet lokasinya
                ((Activity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startSearchLocation();
                    }
                });
                return;
            }

            // Simpan lokasi untuk digunakan selanjutnya
            final Editor prefsPrivateEditor = sharedPreferences.edit();
            prefsPrivateEditor.putString(Constant.Pref.LOC_LAT,
                    location.getLatitude() + "");
            prefsPrivateEditor.putString(Constant.Pref.LOC_LNG,
                    location.getLongitude() + "");
            prefsPrivateEditor.putString(Constant.Pref.LOC_ALT,
                    location.getAltitude() + "");
            prefsPrivateEditor.commit();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initPrayData();
                    initTitle();

                    new Thread() {
                        public void run() {
                            try {
                                Geocoder geocoder = new Geocoder(ctx);
                                List<Address> listAddresses = geocoder
                                        .getFromLocation(
                                                location.getLatitude(),
                                                location.getLongitude(), 1);

                                Address item = listAddresses.get(0);
//                                int maxAddressLineIndex = item
//                                        .getMaxAddressLineIndex();
                                String addressLine = item.getPostalCode() + ", " + item.getSubAdminArea().replace(" City", "") + ", " + item.getCountryName();
//                                for (int j = 0; j <= maxAddressLineIndex; j++) {
//                                    addressLine += ", "
//                                            + item.getAddressLine(j);
//                                }

                                prefsPrivateEditor.putString(
                                        Constant.Pref.LOC_NAME,
                                        addressLine);
                                prefsPrivateEditor.commit();
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        initTitle();
                                        scheduleFragment.initNextTime();
                                    }
                                });
                            } catch (IOException e) {
                            } catch (IllegalArgumentException e) {
                            }
                        }

                        ;
                    }.start();
                }
            });

        }

    }

    public void initPrayData() {
        scheduleFragment.initData(ctx);
    }

    private void initTitle() {
        setTitle("Jadwal Sholat");
        getSupportActionBar().setSubtitle(DateFormat.format("dd MMMM yyyy", new Date()).toString());
    }

}
