package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import app.rasendriya.cintamasjid.MainActivity;
import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.service.ServiceDistance;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DetailMasjidActivity extends ActionBarActivity{
    //    ImageView cover ;
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

    protected static final String TAG = "basic-location-sample";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;

    private TextView cover;
    private ImageView coverImg;

    Activity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_masjid);
        self = this;
        overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);

        extra = getIntent().getExtras();

//        cover = (TextView) findViewById(R.id.cover);
//        coverImg = (ImageView) findViewById(R.id.cover);
        int masjidPos = extra.getInt("masjidPos");
        System.out.println("masjid pos : " + masjidPos);


        final Bundle state = savedInstanceState;

        tujuan = extra.getString("tujuan");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c2922")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(extra.getString("nama"));

//        namaMasjid = (TextView) findViewById(R.id.textNamaMasjid);
//        namaMasjid.setText(extra.getString("nama"));

        new LoadImage().execute();

        btnNavigasi = (Button) findViewById(R.id.btnNavigasi);
        btnNavigasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+tujuan+"&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        jarak = (TextView) findViewById(R.id.textJarak);
        jarak.setText(extra.getString("jarak"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_masjid, menu);
        return true;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(this, MasjidActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            // Something else
            case R.id.action_settings:
//                intent = new Intent(this, ThirdActivity.class);
//                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    private class LoadImage extends AsyncTask<String, Void, Boolean> {
        URL url = null;
        InputStream is = null;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {

            this.dialog = new ProgressDialog(self);
            this.dialog.show();

        }

        @Override
        protected Boolean doInBackground(final String... args) {



            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            this.dialog.dismiss();

//            try {
////                if(extra.getString("cover").toString() != null){
//
////                }else{
////
////                }
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


        }
    }
}
