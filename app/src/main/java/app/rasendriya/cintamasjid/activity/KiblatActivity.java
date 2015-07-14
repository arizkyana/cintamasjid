/**
 *
 */
package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.view.KiblatView;

/**
 * @author cipto
 *
 */

public class KiblatActivity extends Activity {

    private float arahKiblat = 0;
    private float busur = 0;

    private double lonMasjid;
    private double latMasjid;

    private KiblatView kiblatView;
    private SensorManager sensorManager;
    TextView text;
    View bukaPeta;

    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.kiblat);
/*
		TextView textView = new TextView(this);
		textView.setText("kiblat dapat berjalan dalam mode tidur");

		Toast toast = new Toast(this);
		toast.setView(textView);
		toast.show();
*/

        Toast.makeText(getBaseContext(), "kiblat dapat berjalam dalam mode tidur", Toast.LENGTH_SHORT).show();
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
        setArahKiblat(kiblat);

        kiblatView = (KiblatView) this.findViewById(R.id.arahKiblat);
        text = (TextView) findViewById(R.id.text);

        text.setText("Arah Kiblat dari Utara: " + kiblat + " derajat");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        updatePerputaran(0, 0);
        //
        // bukaPeta = findViewById(R.id.cekMap);
        // bukaPeta.setOnClickListener(this);
        Log.d("kupluk", "kiblat: " + getArahKiblat());
        Log.d("kupluk", "Latitude masjid: " + latMasjid + "Longitude Masjid: "
                + lonMasjid);
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

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        sensorManager.unregisterListener(sensorListener);
        super.onStop();
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
}