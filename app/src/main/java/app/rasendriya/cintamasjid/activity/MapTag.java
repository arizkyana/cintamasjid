package app.rasendriya.cintamasjid.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.ServiceDistance;

public class MapTag extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, BaseSliderView.OnSliderClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ImageView cover ;
    RatingBar ratingBar;
    Button btnNavigasi;
    Button btnRating;
    TextView namaMasjid;
    TextView jarak;

    String tujuan;

    double tujuanLatitude;
    double tujuanLongitude;

    double distance;
    double waktu;

    final double kecepatan = 40;

    Bundle extra;
    private SliderLayout mDemoSlider;

    protected static final String TAG = "masjid-location";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_tag);
        context = this;
//        this.buildGoogleApiClient();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f15d28")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Lokasi Masjid");
        System.out.println("SINGLETON MASJIDS");
        System.out.println(SingletonHelper.getInstance().getModelMasjids());
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
//        this.buildGoogleApiClient();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                this.buildGoogleApiClient();
//                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng currentPosition = new LatLng(tujuanLatitude, tujuanLongitude);

        mMap.addMarker(new MarkerOptions().position(new LatLng(tujuanLatitude, tujuanLongitude)).title("Lokasi Anda"));

// Move the camera instantly to Sydney with a zoom of 15.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
//        mMap.set


        //load masjid
        ArrayList<ModelMasjid> modelMasjids = SingletonHelper.getInstance().getModelMasjids();
        if(modelMasjids != null){
            for(ModelMasjid m : modelMasjids){
                mMap.addMarker(new MarkerOptions().position(new LatLng(m.getLatitude(), m.getLongitude())).title(m.getNama()));
            }
        }


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {


                final Handler handler = new Handler();
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Please wait");
                progressDialog.show();
                final Runnable r = new Runnable() {
                    public void run() {
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        Intent i = new Intent(getApplication(), TagMasjid.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getAddress(latLng, i);

                    }
                };

                handler.postDelayed(r, 1450);
            }
        });
    }

    public void getAddress(LatLng latLng, Intent i) {
        List<Address> addresses = null;
        try {

            Geocoder geocoder;

            geocoder = new Geocoder(MapTag.this);


            if (latLng.latitude != 0 || latLng.longitude != 0) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                //testing address below

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city
                        + ", country = " + country);

                Toast t = Toast.makeText(getApplication(), "Alamat : " + address, Toast.LENGTH_LONG);
                t.show();

                i.putExtra("alamat", address + " , " + city + " , " + country);
                i.putExtra("lat", latLng.latitude);
                i.putExtra("lon", latLng.longitude);
                i.putExtra("latlng", latLng);
                startActivity(i);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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

//
            tujuanLatitude = mLastLocation.getLatitude();
            tujuanLongitude = mLastLocation.getLongitude();

            setUpMap();
//            setUpMapIfNeeded();

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

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, CmMainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
            // Something else
            case R.id.action_about:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
