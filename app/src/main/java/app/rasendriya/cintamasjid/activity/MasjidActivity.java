package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.melnykov.fab.FloatingActionButton;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.adapter.ListMasjidAdapter;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.FetchDataMasjid;
import app.rasendriya.cintamasjid.service.JsonParser;
import app.rasendriya.cintamasjid.service.ServiceDistance;
import app.rasendriya.cintamasjid.sources.MasjidSources;

public class MasjidActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, BaseSliderView.OnSliderClickListener {
    public ListView mListView;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public Adapter mAdapter;
    public Intent i;
    public FloatingActionButton fab;

    String TAG = MasjidActivity.class.getSimpleName();

    ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
    ProgressDialog dialog = null;

    MasjidSources masjidSources;

    MasjidActivity self ;

    double lat , lon;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    LocationManager locationManager;
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;


    LayoutInflater layoutInflater;
    ParallaxScrollView parallaxScrollView;

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_no_active)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        buildGoogleApiClient();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageNoNetwork(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.network_no_active)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        buildGoogleApiClient();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertAddCurrentMasjid(final Activity self){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Tambahkan masjid di lokasi anda sekarang?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(self, TagMasjid.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        getAddress(new LatLng(getLat(), getLon()), intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(self, MapTag.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void getAddress(LatLng latLng, Intent i) {
        List<Address> addresses = null;
        System.out.println("masuk ke get address : " + SingletonHelper.getInstance().getLat());
        try {

            Geocoder geocoder;

            geocoder = new Geocoder(MasjidActivity.this);


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
            }else{
                addresses = geocoder.getFromLocation(SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon(), 1);

                //testing address below

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city
                        + ", country = " + country);

                Toast t = Toast.makeText(getApplication(), "Alamat : " + address, Toast.LENGTH_LONG);
                t.show();

                i.putExtra("alamat", address + " , " + city + " , " + country);
                i.putExtra("lat", SingletonHelper.getInstance().getLat());
                i.putExtra("lon", SingletonHelper.getInstance().getLon());
                i.putExtra("latlng", latLng);
                startActivity(i);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
             Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private ProgressDialog dialogCari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masjid);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parallaxScrollView = (ParallaxScrollView) layoutInflater.inflate(R.layout.activity_detail_masjid, null);

        self = this;
        dialogCari = new ProgressDialog(MasjidActivity.this);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        mListView = (ListView) findViewById(R.id.activity_main_listview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c2922")));
        actionBar.setTitle("Cinta Masjid");


        this.dialog = new ProgressDialog(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertAddCurrentMasjid(self);
            }
        });
//
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Toast t = Toast.makeText(getApplicationContext(), "Mencari masjid terdekat ...", Toast.LENGTH_LONG);
//                t.show();
//                buildGoogleApiClient();
//                final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
//                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
////                    new FetchDataMasjidSwipe().execute();
//                    new FetchDataMasjid();
//                }else{
//                    mSwipeRefreshLayout.setRefreshing(false);
//                    buildAlertMessageNoGps();
//                }
//            }
//        });



        System.out.println("SINGLETON ON CREATE: ");
        System.out.println(SingletonHelper.getInstance().getModelMasjids());

        final SingletonHelper singletonHelper = SingletonHelper.getInstance();
        System.out.println("LIST MODEL MASJID");
        System.out.println(singletonHelper.getModelMasjids());
        if(singletonHelper.getModelMasjids() != null){
//            buildGoogleApiClient();
            ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(this, singletonHelper.getModelMasjids(), singletonHelper.getLat(), singletonHelper.getLon());
            mListView.setAdapter(arrayAdapterMasjid);


            mListView.setAdapter(arrayAdapterMasjid);

//        Toast.makeText(activity.getApplicationContext(),"all data masjid local : " + masjidSources.getAllMasjid().toString(), Toast.LENGTH_LONG ).show();

//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf" );
//        TextView textIcon = (TextView) rootView.findViewById(R.id.textIcon);
//        textIcon.setTypeface(font);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                dialog.show();

                    final ModelMasjid m = singletonHelper.getModelMasjids().get(position);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ParallaxScrollView parallaxScrollView = (ParallaxScrollView) layoutInflater.inflate(R.layout.activity_detail_masjid, null);

                    Button btnNavigasi = (Button) parallaxScrollView.findViewById(R.id.btnNavigasi);
                    TextView textJarak = (TextView) parallaxScrollView.findViewById(R.id.textJarak);
                    TextView textAlamat = (TextView) parallaxScrollView.findViewById(R.id.alamat);

//                    ImageView coverImage = (ImageView) parallaxScrollView.findViewById(R.id.coverImg);

//                coverImage.setImageBitmap(m.getCover());

                    String j;
                    if((Double.parseDouble(m.getJarak())) < 1){
                        j =  String.valueOf((int) Math.floor(((Double.parseDouble(m.getJarak())) * 100))) + " meter";
                    }else{
                        j = String.valueOf((int)(Double.parseDouble(m.getJarak()))) + " km";
                    }

                    textJarak.setText(j);
                    textAlamat.setText(m.getAlamat());

                    btnNavigasi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+(m.getLatitude() + "," + m.getLongitude())+"&mode=d");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });

                    new MaterialDialog.Builder(self)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    System.out.println("tutup");
                                }
                            })
                            .title(m.getNama())
                            .customView(parallaxScrollView, true)
                            .positiveText(R.string.close)
                            .build()

                            .show();
                }

            });
        }else{
            System.out.println("MASUK ELSE CREATE");
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                buildAlertMessageNoGps();
//            System.out.println("GPS BELUM AKTIF");
            }else{
//            System.out.println("GPS SUDAH AKTIF");
                buildGoogleApiClient();
            }
        }

//        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        int margin = ((Number) (getResources().getDisplayMetrics().density * 55)).intValue();
//        lps.setMargins(margin, margin, margin, margin);
////        buildGoogleApiClient();
//        ShowcaseView sv;
//        ViewTarget target = new ViewTarget(R.id.fab, this);
//        sv = new ShowcaseView.Builder(this, true)
//                .setTarget(target)
//                .setContentTitle("Tag Masjid")
//                .setContentText("Gunakan tombol ini untuk melakukan tag masjid, pilih YES apabila saat ini anda berada di masjid atau NO untuk melihat lokasi masjid.")
//                .setStyle(R.style.CustomShowcaseTheme2)
//                .setShowcaseEventListener(this)
//                .build();
//        sv.setButtonPosition(lps);

//        ViewTarget target2 = new ViewTarget(R.id.adapter_masjid, this);
//        sv = new ShowcaseView.Builder(this, true)
//                .setTarget(target2)
//                .setContentTitle("List Masjid")
//                .setContentText("Tap di sini untuk melihat informasi masjid dan pilih NAVIGASI untuk mengetahui rute ke masjid tersebut")
//                .setStyle(R.style.CustomShowcaseTheme2)
//                .setShowcaseEventListener(this)
//                .build();
//        sv.setButtonPosition(lps);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_masjid, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
//                TextView textView=(TextView)findViewById(R.id.aa);
//                textView.setText(newText);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
//                dialogCari = new ProgressDialog(self.getApplicationContext());

                dialogCari.setMessage("Please wait");
                dialogCari.show();
                dialogCari.setCancelable(false);
                dialogCari.setCanceledOnTouchOutside(false);

                System.out.println("keywords : " + query);
                System.out.println("lat : " + SingletonHelper.getInstance().getLat());
//                TextView textView=(TextView)findViewById(R.id.aa);
//                textView.setText(query);
//                Toast.makeText(self, "query : " + query, Toast.LENGTH_LONG).show();
                RequestParams requestParams = new RequestParams();
                requestParams.put("keyword", query.trim());
                System.out.println(requestParams.toString());

//                mSwipeRefreshLayout.setRefreshing(true);
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.get(Config.FETCH_MASJID + "?lat="+SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon() + "&", requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }

                        dialogCari.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray result) {
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
                            initListMasjidFromQuery(result);
                            dialogCari.dismiss();
//                            System.out.println(result.toString());
//                        }

//                    System.out.println(result.toString());
                    }
                });
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_sholat:
//                Intent intent = new Intent(this, SholatActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;

            case R.id.menu_kajian:
                Intent intent = new Intent(this, KajianActivity.class);
                intent = new Intent(this, CaldroidSampleActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_sholat:
                Intent intentSholat = new Intent(this, JadwalSholatActivity.class);
                startActivity(intentSholat);
                break;
            case R.id.action_about:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.layout_info, null);

                new MaterialDialog.Builder(self)
                        .title(R.string.action_about)
                        .customView(linearLayout, true)
                        .positiveText(R.string.close)
                        .build()
                        .show();
                break;
            case R.id.action_refresh:

                new FetchDataMasjid(this, mSwipeRefreshLayout, mListView, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon()).execute();
                break;
            case R.id.action_guide:
                Intent intent1 = new Intent(this, FirstUse.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDataMasjid(final ArrayList<ModelMasjid> modelMasjids){
        ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(this, modelMasjids, getLat(), getLon());
        mListView.setAdapter(arrayAdapterMasjid);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(self.getApplicationContext(), "ini item di click", Toast.LENGTH_LONG).show();
//                Intent i = new Intent(activity, DetailMasjidActivity.class);
                final ModelMasjid m = modelMasjids.get(position);
////                Toast t = Toast.makeText(getActivity(), "latitude : " + m.getLatitude(), Toast.LENGTH_LONG);
////                t.show();
//                i.putExtra("nama", m.getNama());
//                i.putExtra("tujuan", m.getLatitude() + "," + m.getLongitude());
//                i.putExtra("tujuanLatitude", m.getLatitude());
//                i.putExtra("tujuanLongitude", m.getLongitude());
//                i.putExtra("alamat", m.getAlamat());
//                i.putExtra("masjidPos", position);
//                i.putExtra("cover", m.getFotoCover());
//                i.putExtra("jarak", m.getJarak());
//
//                activity.startActivity(i);

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ParallaxScrollView parallaxScrollView = (ParallaxScrollView) layoutInflater.inflate(R.layout.activity_detail_masjid, null);
                ImageView cover = (ImageView) parallaxScrollView.findViewById(R.id.cover);
                Picasso.with(self.getApplicationContext()).load(m.getFotoCover()).into(cover);

                Button btnNavigasi = (Button) parallaxScrollView.findViewById(R.id.btnNavigasi);

                btnNavigasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+(m.getLatitude() + "," + m.getLongitude())+"&mode=d");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                new MaterialDialog.Builder(self)
                        .title(m.getNama())
                        .customView(parallaxScrollView, true)
                        .positiveText(R.string.close)
                        .build()
                        .show();
            }

        });
    }


    public void initListMasjidFromQuery(JSONArray jsonArray){
        System.out.println("besar json array : " + jsonArray.length());

        if(jsonArray.length() <= 0){
            new AlertDialogWrapper.Builder(this)
                    .setTitle("")
                    .setMessage(R.string.masjid_not_found)
                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new FetchDataMasjid(self, mSwipeRefreshLayout, mListView, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon()).execute();
//                            new FetchDataMasjid(self, mListView, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon());
                        }
                    }).setCancelable(false).show();
        }

        try {
            modelMasjids.removeAll(modelMasjids);
//
            ModelMasjid modelMasjid_ = new ModelMasjid();
//            JSONArray jsonArray = jsonArray;
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ModelMasjid modelMasjid = new ModelMasjid();
                modelMasjid.setNama(jsonObject.getString("nama"));
                modelMasjid.setAlamat(jsonObject.getString("alamat"));
                modelMasjid.setFotoCover(jsonObject.getString("foto"));
                modelMasjid.setKapasitas(jsonObject.getString("kapasitas"));
                modelMasjid.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                modelMasjid.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                modelMasjid.setJenis(jsonObject.getString("jenis"));
                modelMasjid.setKota(jsonObject.getString("kota"));
                modelMasjid.setProvinsi(jsonObject.getString("provinsi"));
                modelMasjid.setStatus(jsonObject.getString("status"));
                modelMasjid.setIdMasjid(jsonObject.getString("id"));
                modelMasjid.setJarak(jsonObject.getString("distance"));

//                        masjidSources.createMasjid(modelMasjid);

                modelMasjids.add(modelMasjid);
            }

            modelMasjid_.setModelMasjids(modelMasjids);

//                    Log.e("JSON", "> " + now_playing + earned);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

//        modelMasjids = ServiceDistance.sortDistance(mLastLocation.getLatitude(), mLastLocation.getLongitude(), modelMasjids);
        SingletonHelper singletonHelper = SingletonHelper.getInstance();
        singletonHelper.setModelMasjids(modelMasjids);
        ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(self, modelMasjids, getLat(), getLon());

        mListView.setAdapter(arrayAdapterMasjid);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(activity, DetailMasjidActivity.class);
                final ModelMasjid m = modelMasjids.get(position);
////                Toast t = Toast.makeText(getActivity(), "latitude : " + m.getLatitude(), Toast.LENGTH_LONG);
////                t.show();
//                i.putExtra("nama", m.getNama());
//                i.putExtra("tujuan", m.getLatitude() + "," + m.getLongitude());
//                i.putExtra("tujuanLatitude", m.getLatitude());
//                i.putExtra("tujuanLongitude", m.getLongitude());
//                i.putExtra("alamat", m.getAlamat());
//                i.putExtra("masjidPos", position);
//                i.putExtra("cover", m.getFotoCover());
//                i.putExtra("jarak", m.getJarak());
//
//                activity.startActivity(i);

                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ParallaxScrollView parallaxScrollView = (ParallaxScrollView) layoutInflater.inflate(R.layout.activity_detail_masjid, null);

                Button btnNavigasi = (Button) parallaxScrollView.findViewById(R.id.btnNavigasi);

                btnNavigasi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+(m.getLatitude() + "," + m.getLongitude())+"&mode=d");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                new MaterialDialog.Builder(self)
                        .title(m.getNama())
                        .customView(parallaxScrollView, true)
                        .positiveText(R.string.close)
                        .build()
                        .show();
            }

        });
    }




    private class FetchDataMasjidSwipe extends AsyncTask<String, Void, Boolean> {

        ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
        private ProgressDialog dialog = new ProgressDialog(getApplicationContext());
        Location location;
        LocationManager locationManager;
        /** progress dialog to show user that the backup is processing. */

        /**
         * application context.
         */
        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Please wait");
//            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... args) {
            JsonParser jsonParser = new JsonParser();
            String json = jsonParser
                    .getJSONFromUrl(Config.FETCH_MASJID + "?lat="+SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon());

//            String json = "[{\"id\":\"16\",\"nama\":\"Masjid Garuda\",\"alamat\":\"Jl. Rajawali 1 No. 5\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.913144\",\"longitude\":\"107.577042\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"jenis\":\"1\",\"kapasitas\":\"400 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:55:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id\":\"17\",\"nama\":\"Masjid Jami Al Jumuah\",\"alamat\":\"Jl. Pesantren\",\"kota\":\"32.77.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.8237325\",\"longitude\":\"107.2379843\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"jenis\":\"1\",\"kapasitas\":\"200 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-11 20:11:03\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"176\",\"lokasi_kode\":\"32.77.00.0000\",\"lokasi_nama\":\"KOTA CIMAHI\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"77\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id\":\"18\",\"nama\":\"Masjid Attaqwa\",\"alamat\":\"Jl. Dadali 2 no.10\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.911168\",\"longitude\":\"107.576801\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"jenis\":\"1\",\"kapasitas\":\"100 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:58:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"}]";

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
//                    locationManager = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);
//                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    LatLng current = new LatLng(latitude, longitude);
//                    Toast.makeText(getActivity().getApplicationContext(), "current : " + current.latitude, Toast.LENGTH_LONG).show();
                    ModelMasjid modelMasjid_ = new ModelMasjid();
                    JSONArray jsonArray = new JSONArray(json);
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ModelMasjid modelMasjid = new ModelMasjid();
                        modelMasjid.setNama(jsonObject.getString("nama"));
                        modelMasjid.setAlamat(jsonObject.getString("alamat"));
                        modelMasjid.setFotoCover(jsonObject.getString("foto_small"));
                        modelMasjid.setKapasitas(jsonObject.getString("kapasitas"));
                        modelMasjid.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                        modelMasjid.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
                        modelMasjid.setJenis(jsonObject.getString("jenis"));
                        modelMasjid.setKota(jsonObject.getString("kota"));
                        modelMasjid.setProvinsi(jsonObject.getString("provinsi"));
                        modelMasjid.setStatus(jsonObject.getString("status"));
                        modelMasjid.setIdMasjid(jsonObject.getString("id"));

                        modelMasjid.setJarak(jsonObject.getString("distance"));


//                        masjidSources.createMasjid(modelMasjid);
                        modelMasjids.add(modelMasjid);
                    }

                    modelMasjid_.setModelMasjids(modelMasjids);

//                    Log.e("JSON", "> " + now_playing + earned);
                    return true;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }

            }

//            return false;
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (mSwipeRefreshLayout.isRefreshing()) {
//                dialog.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            if(mLastLocation != null) {
                System.out.println("MLAST LOCATION DARI SWIPE ");
                System.out.println(mLastLocation);
                System.out.println(mLastLocation.getLatitude());

                if(modelMasjids.size() <= 0){
                    new AlertDialogWrapper.Builder(self)
                            .setTitle("")
                            .setMessage(R.string.masjid_not_coverage)
                            .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setCancelable(false).show();
                }else {

                    modelMasjids = ServiceDistance.sortDistance(modelMasjids);
                    SingletonHelper singletonHelper = SingletonHelper.getInstance();
                    singletonHelper.setModelMasjids(modelMasjids);
                    ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(self, modelMasjids, mLastLocation.getLatitude(), mLastLocation.getLongitude());

                    mListView.setAdapter(arrayAdapterMasjid);


                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                        dialog.show();

                            final ModelMasjid m = modelMasjids.get(position);
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);




                            Button btnNavigasi = (Button) parallaxScrollView.findViewById(R.id.btnNavigasi);
                            TextView textJarak = (TextView) parallaxScrollView.findViewById(R.id.textJarak);
                            TextView textAlamat = (TextView) parallaxScrollView.findViewById(R.id.alamat);
                            ImageView coverImage = (ImageView) parallaxScrollView.findViewById(R.id.cover);
                            Picasso.with(self.getApplicationContext()).load(m.getFotoCover()).into(coverImage);
                            System.out.println("IMAGE FOR PICASO : " + m.getFotoCover());

//                coverImage.setImageBitmap(m.getCover());

                            String j;
                            if ((Double.parseDouble(m.getJarak())) < 1) {
                                j = String.valueOf((int) Math.floor(((Double.parseDouble(m.getJarak())) * 100))) + " meter";
                            } else {
                                j = String.valueOf((int) (Double.parseDouble(m.getJarak()))) + " km";
                            }

                            textJarak.setText(j);
                            textAlamat.setText(m.getAlamat());

                            btnNavigasi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + (m.getLatitude() + "," + m.getLongitude()) + "&mode=d");
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                }
                            });

                            new MaterialDialog.Builder(self)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            super.onPositive(dialog);
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog) {
                                            super.onNegative(dialog);
                                            System.out.println("tutup");
                                        }
                                    })
                                    .title(m.getNama())
                                    .customView(parallaxScrollView, true)
                                    .positiveText(R.string.close)
                                    .build()

                                    .show();
                        }

                    });
                }
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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
        if(!isOnline()) {
            buildAlertMessageNoNetwork();
        }else{
            if(mGoogleApiClient == null){
//                buildAlertMessageNoGps();
                buildGoogleApiClient();
            }else{
                mGoogleApiClient.connect();
            }


        }




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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        System.out.println("tampilkan mLastLocation toString");
//        System.out.println(mLastLocation.toString());
        if(mLastLocation==null){
            setLat(-6.9167);
            setLon(107.6000);
            System.out.println("current lat : " + getLat());

            System.out.println("SINGLETON LOCATION NULL: ");
            System.out.println(SingletonHelper.getInstance().getModelMasjids());

            new FetchDataMasjid(this, mSwipeRefreshLayout, mListView, getLat(), getLon()).execute();

        }else{
            setLat(mLastLocation.getLatitude());
            setLon(mLastLocation.getLongitude());
            System.out.println("current lat : " + getLat());

            System.out.println(SingletonHelper.getInstance().getModelMasjids());
            new FetchDataMasjid(this, mSwipeRefreshLayout, mListView, getLat(), getLon()).execute();
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
//        buildGoogleApiClient();
        if(SingletonHelper.getInstance().getLat() != 0){
            new FetchDataMasjid(self, mSwipeRefreshLayout, mListView, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon()).execute();
        }

    }

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
//        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        if (result.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + result.getErrorCode());
        }
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

}
