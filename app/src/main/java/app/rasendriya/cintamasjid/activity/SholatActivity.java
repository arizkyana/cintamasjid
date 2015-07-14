package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.service.HijriCalendar;
import app.rasendriya.cintamasjid.service.PrayTime;
import app.rasendriya.cintamasjid.view.KiblatView;

public class SholatActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    TextView waktuSubuh, waktuDzuhur, waktuAshar, waktuMagrib, waktuIsya;

    int subuh = 0;
    int dzuhur = 0;
    int ashar = 0;
    int magrib = 0;
    int isya = 0;

    protected static final String TAG = "masjid-location";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private LocationManager locationManager;

    private float arahKiblat = 0;
    private float busur = 0;

    private double lonMasjid;
    private double latMasjid;

    private KiblatView kiblatView;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sholat);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c2922")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Jadwal Sholat");

//        initJadwalSholat(-6.9167, 107.6000, 7);
//        Toast.makeText(getBaseContext(), "kiblat dapat berjalam dalam mode tidur", Toast.LENGTH_SHORT).show();
        // inisialisasi Lokasi
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // kriteria penggunaan lokasi
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        // latMasjid= location.getLatitude();
        // lonMasjid= location.getLongitude();
        //
        // float kiblat=(float) segitigaBola(lonMasjid,latMasjid);
        float kiblat = (float) segitigaBola(106.85, -6.166666667);

        System.out.println("KIBLAT  : " + kiblat);
        kiblatView = (KiblatView) this.findViewById(R.id.arahKiblat);
        setArahKiblat(kiblat);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        updatePerputaran(0, 0);

        TextView hijriah = (TextView) findViewById(R.id.hijriah);
        Calendar cal = Calendar.getInstance();
        hijriah.setText(HijriCalendar.getSimpleDate(cal));
        buildGoogleApiClient();
//        new FetchLokasi(this).execute();
    }

    public void initJadwalSholat(double lat, double lon, int timezone){

        buildGoogleApiClient();
//        currentAddress.setText(getAddress(new LatLng(lat, lon)));
    }

    public String getAddress(LatLng latLng) {
        List<Address> addresses = null;
        String currentAddress = "";
        try {

            Geocoder geocoder;

            geocoder = new Geocoder(SholatActivity.this);


            if (latLng.latitude != 0 || latLng.longitude != 0) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                //testing address below

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city
                        + ", country = " + country);

                currentAddress = city + " , " + country;

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return currentAddress;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sholat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MasjidActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            // Something else
            case R.id.menu_lokasi:
                new FetchLokasi(this).execute();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchLokasi extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;
        Activity activity;

        Location location;
        LocationManager locationManager;

        public FetchLokasi(Activity activity){
            this.dialog = new ProgressDialog(activity);
            this.activity = activity;
//            this.dialog.show();
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait");
            dialog.show();

        }

        @Override
        protected Boolean doInBackground(final String... args) {

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            buildGoogleApiClient();
//            GPS gps = new GPS(activity);
//            System.out.println("LOCATION");
//            System.out.println(gps.getLocation());
//            location = gps.getLocation();
//
//            System.out.println(gps.getLocation());
//            System.out.println(gps.getLocation().getLatitude());
//            if(location == null){
//                System.out.println("location null");
//                Toast.makeText(activity, "Lokasi tidak ditemukan", Toast.LENGTH_LONG).show();
//                initJadwalSholat(-6.9167, 107.6000, 7);
//            }else{
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                System.out.println("location ada");
//                initJadwalSholat(latitude, longitude, 7);
//                Toast.makeText(getApplicationContext(), "Finish Fetching Location", Toast.LENGTH_LONG).show();
//            }
//
//            gps.stopUsingGPS();

        }
    }

    private void buildDialogJadwal(double lat, double lon, int timezone){
        boolean wrapInScrollView = true;

        System.out.println("current latitude : " + lat);
        PrayTime prayTime = new PrayTime();
        System.out.println("CURRENT TIME : " + prayTime.floatToTime24(Calendar.getInstance().getTime().getTime()));
        String currentTime = prayTime.floatToTime24(new Date().getTime());
        ArrayList<String> prayTimes = prayTime.waktuSholat(lat, lon, timezone);
        System.out.println("GET TIME FORMAT : " + prayTime.getDhuhrMinutes());

//        new Timer(1000,)

        for(String timeName : prayTime.getTimeNames()){
            System.out.println("time name : " + timeName);
        }


        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LinearLayout layoutMasjid = (LinearLayout) layoutInflater.inflate(R.layout.layout_jadwal_sholat, null);

        waktuSubuh = (TextView) layoutMasjid.findViewById(R.id.waktuSubuh);
        waktuDzuhur = (TextView) layoutMasjid.findViewById(R.id.waktuDzuhur);
        waktuAshar = (TextView) layoutMasjid.findViewById(R.id.waktuAshar);
        waktuMagrib = (TextView) layoutMasjid.findViewById(R.id.waktuMaghrib);
        waktuIsya = (TextView) layoutMasjid.findViewById(R.id.waktuIsya);

        System.out.println("objek waktu subuh");
        System.out.println(waktuSubuh);

        waktuSubuh.setText(prayTimes.get(0));
        waktuDzuhur.setText(prayTimes.get(2));
        waktuAshar.setText(prayTimes.get(3));
        waktuMagrib.setText(prayTimes.get(4));
        waktuIsya.setText(prayTimes.get(6));



        new MaterialDialog.Builder(this)
                .title(R.string.title_jadwal_sholat)
                .customView(layoutMasjid, wrapInScrollView)
                .positiveText(R.string.close)
                .build()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(sensorListener,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(sensorListener);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    private final SensorListener sensorListener = new SensorListener() {
        public void onSensorChanged(int sensor, float[] values) {
            updatePerputaran(values[SensorManager.DATA_X], getArahKiblat());
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
        }
    };

    private void updatePerputaran(float _busur, float _arah) {
        busur = (float) (_busur + (float) (360 - _arah));

        if (kiblatView != null) {
            kiblatView.setBearing(busur);
            kiblatView.invalidate();
        }
    }
    /**
     * Rumus ini untuk menghitung arah kiblat dari utara. Perhitungan sesuai
     * dengan arah jarum jam. Jika nilai utara ditemukan maka selanjutnya adalah
     * dengan menambah arah utara dengan hasil perhitungan.
     */
    public double segitigaBola(double lngMasjid, double latMasjid) {
        double lngKabah = 39.82616111;
        double latKabah = 21.42250833;
        double lKlM = (lngKabah - lngMasjid);
        double sinLKLM = Math.sin(lKlM * 2.0 * Math.PI / 360);
        double cosLKLM = Math.cos(lKlM * 2.0 * Math.PI / 360);
        double sinLM = Math.sin(latMasjid * 2.0 * Math.PI / 360);
        double cosLM = Math.cos(latMasjid * 2.0 * Math.PI / 360);
        double tanLK = Math.tan(latKabah * 2 * Math.PI / 360);
        double penyebut = (cosLM * tanLK) - sinLM * cosLKLM;

        double kiblat;
        double arah;

        kiblat = Math.atan2(sinLKLM, penyebut) * 180 / Math.PI;
        arah = kiblat < 0 ? kiblat + 360 : kiblat;
        return arah;

    }

    public float getArahKiblat() {
        return arahKiblat;
    }

    public void setArahKiblat(float arahKiblat) {
        this.arahKiblat = arahKiblat;
    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {

            System.out.println("last location : ");
            System.out.println(mLastLocation);

//            initJadwalSholat(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 7);
            TextView jenisWaktu = (TextView) findViewById(R.id.jenisWaktu);
            jenisWaktu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buildDialogJadwal(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 7);
                }
            });

            PrayTime prayTime = new PrayTime();
            final ArrayList<String> prayTimes = prayTime.waktuSholat(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 7);



            Timer timer = new Timer();

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    final Calendar calendar = Calendar.getInstance();
                    System.out.println(calendar.getTime().getHours() + " : " + calendar.getTime().getMinutes() + " : " + calendar.getTime().getSeconds());
                    int i = 0;
                    String currentTime = calendar.getTime().getHours() + ":" + calendar.getTime().getMinutes();
                    for(String s : prayTimes){
                        if(s.equals(currentTime)){
                            System.out.println("posisi " + i + " : " + s);
                        }else{
                            System.out.println("belum waktu sholat");
                            System.out.println("posisi " + i + " : " + s);
                        }
                        i++;
                    }
                }
            }, 0, 1000);




            TextView currentAddress = (TextView) findViewById(R.id.currentAddress);
            currentAddress.setText(getAddress(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));

            Toast.makeText(getApplicationContext(), "Lokasi : " + getAddress(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())), Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }
        System.out.println(mLastLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


}
