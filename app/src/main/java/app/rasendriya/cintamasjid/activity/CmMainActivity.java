package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.adapter.ListMasjidAdapter;
import app.rasendriya.cintamasjid.adapter.ViewPagerAdapter;
import app.rasendriya.cintamasjid.alarm.Alarm;
import app.rasendriya.cintamasjid.alarm.AlarmService;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.FetchDataMasjid;
import app.rasendriya.cintamasjid.service.SlidingTabLayout;

public class CmMainActivity extends ActionBarActivity {

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Sholat", "Masjid", "Kajian"};
    int Numboftabs = 3;
    ListView listMasjid;
    Menu menu;
    Activity self;

    AlarmService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm_main);
        self = this;
//        ActionBar actionBar = getSupportActionBar();
//
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle("Cinta Masjid");
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#101010")));

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(1);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(getApplicationContext(), "hilang search : " + position, Toast.LENGTH_LONG).show();
//                if (position == 0) {
//                    menu.findItem(R.id.action_search).setVisible(false);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frameLayoutMasjid = (FrameLayout) layoutInflater.inflate(R.layout.fragment_masjid_, null);

        listMasjid = (ListView) frameLayoutMasjid.findViewById(R.id.activity_main_listview);

        Alarm alarm = new Alarm();
        alarm.SetAlarm(this);
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

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            AlarmService.LocalBinder binder = (AlarmService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            mService.setOnRingtonePlayListener(new AlarmService.OnRingtonePlayListener() {

                @Override
                public void onRingtonePlay(String title, String time,
                                           String notif) {
                    // TODO Auto-generated method stub

                }
            });

            // cek alarm apakah nyala
            if (mService.isRingtoneActive()) {
                mService.stopRingtone();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cm_main, menu);
        SearchView searchView = null;
        if (pager.getCurrentItem() == 1 || pager.getCurrentItem() == 2) {
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        } else {
            menu.findItem(R.id.action_search).setVisible(false);
        }

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

//                dialogCari.setMessage("Please wait");
//                dialogCari.show();
//                dialogCari.setCancelable(false);
//                dialogCari.setCanceledOnTouchOutside(false);

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
                asyncHttpClient.get(Config.FETCH_MASJID + "?lat=" + SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon() + "&", requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }

//                        dialogCari.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray result) {
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
                        initListMasjidFromQuery(result);
//                        dialogCari.dismiss();
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

    ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();

    public void initListMasjidFromQuery(JSONArray jsonArray) {
        System.out.println("besar json array : " + jsonArray.length());

        if (jsonArray.length() <= 0) {
            new AlertDialogWrapper.Builder(this)
                    .setTitle("")
                    .setMessage(R.string.masjid_not_found)
                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            new FetchDataMasjid(self, listMasjid, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon()).execute();
//                            new FetchDataMasjid(self, mListView, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon());
                        }
                    }).setCancelable(false).show();
        }

        try {
            modelMasjids.removeAll(modelMasjids);
//
            ModelMasjid modelMasjid_ = new ModelMasjid();
//            JSONArray jsonArray = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
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
        ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(self, modelMasjids, SingletonHelper.getInstance().getLat(), SingletonHelper.getInstance().getLon());

        listMasjid.setAdapter(arrayAdapterMasjid);

        listMasjid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + (m.getLatitude() + "," + m.getLongitude()) + "&mode=d");
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        System.out.println("MASUK PREPARTION MENU");
        if (pager.getCurrentItem() == 0) {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
