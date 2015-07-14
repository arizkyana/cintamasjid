package app.rasendriya.cintamasjid.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.adapter.ListKajianAdapter;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelKajian;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.JsonParser;

//import app.rasendriya.cintamasjid.adapter.RecyclerKajianAdapter;

@SuppressLint("SimpleDateFormat")
public class CaldroidSampleActivity extends ActionBarActivity {
    private boolean undo = false;
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Activity self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cal);

        Bundle extra = getIntent().getExtras();
        self = this;
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2c2922")));
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(extra.getString("nama"));
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new FetchDataKajian().execute();
//            }
//        });
        new FetchDataKajian().execute();
        // Setup caldroid fragment
        // **** If you want normal CaldroidFragment, use below line ****
        caldroidFragment = new CaldroidFragment();

        // //////////////////////////////////////////////////////////////////////
        // **** This is to show customized fragment. If you want customized
        // version, uncomment below line ****
//		 caldroidFragment = new CaldroidSampleCustomFragment();

        // Setup arguments

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
        }



        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener

        Button btnRekomendasi = (Button) findViewById(R.id.btnRekomendasiKajian);

        btnRekomendasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(self, KajianActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("tanggalKajian", "Rekomendasi Kajian Bulan Ini");
                i.putExtra("dateQuery", "rekomendasi");
                startActivity(i);
            }
        });

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(), formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
//                new InitDataKajian().execute();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dtBar = new SimpleDateFormat("dd MMM yyyy");
                String dateQuery = df.format(date);
                String dateBar = dtBar.format(date);

//                Date dtQuery = df.parse()
                Intent i = new Intent(self, KajianActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                System.out.println("date : " + date);
                i.putExtra("tanggalKajian", dateBar);
                i.putExtra("dateQuery", dateQuery);
                startActivity(i);

            }

            @Override
            public void onChangeMonth(int month, int year) {
                String text = "month: " + month + " year: " + year;
//                Toast.makeText(getApplicationContext(), text,
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClickDate(Date date, View view) {
//                Toast.makeText(getApplicationContext(),
//                        "Long click " + formatter.format(date),
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
//                    Toast.makeText(getApplicationContext(),
//                            "Caldroid view is created", Toast.LENGTH_SHORT)
//                            .show();
                }
            }

        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);



        // Customize the calendar
//        customizeButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (undo) {
//                    customizeButton.setText(getString(R.string.customize));
//                    textView.setText("");
//
//                    // Reset calendar
//                    caldroidFragment.clearDisableDates();
//                    caldroidFragment.clearSelectedDates();
//                    caldroidFragment.setMinDate(null);
//                    caldroidFragment.setMaxDate(null);
//                    caldroidFragment.setShowNavigationArrows(true);
//                    caldroidFragment.setEnableSwipe(true);
//                    caldroidFragment.refreshView();
//                    undo = false;
//                    return;
//                }
//
//                // Else
//                undo = true;
//                customizeButton.setText(getString(R.string.undo));
//                Calendar cal = Calendar.getInstance();
//
//                // Min date is last 7 days
//                cal.add(Calendar.DATE, -7);
//                Date minDate = cal.getTime();
//
//                // Max date is next 7 days
//                cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, 14);
//                Date maxDate = cal.getTime();
//
//                // Set selected dates
//                // From Date
//                cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, 2);
//                Date fromDate = cal.getTime();
//
//                // To Date
//                cal = Calendar.getInstance();
//                cal.add(Calendar.DATE, 3);
//                Date toDate = cal.getTime();
//
//                // Set disabled dates
//                ArrayList<Date> disabledDates = new ArrayList<Date>();
//                for (int i = 5; i < 8; i++) {
//                    cal = Calendar.getInstance();
//                    cal.add(Calendar.DATE, i);
//                    disabledDates.add(cal.getTime());
//                }
//
//                // Customize
////                caldroidFragment.setMinDate(minDate);
////                caldroidFragment.setMaxDate(maxDate);
////                caldroidFragment.setDisableDates(disabledDates);
////                caldroidFragment.setSelectedDates(fromDate, toDate);
////                caldroidFragment.setShowNavigationArrows(false);
////                caldroidFragment.setEnableSwipe(false);
////
////                caldroidFragment.refreshView();
//
//                // Move to date
//                // cal = Calendar.getInstance();
//                // cal.add(Calendar.MONTH, 12);
//                // caldroidFragment.moveToDate(cal.getTime());
//
//                String text = "Today: " + formatter.format(new Date()) + "\n";
//                text += "Min Date: " + formatter.format(minDate) + "\n";
//                text += "Max Date: " + formatter.format(maxDate) + "\n";
//                text += "Select From Date: " + formatter.format(fromDate)
//                        + "\n";
//                text += "Select To Date: " + formatter.format(toDate) + "\n";
//                for (Date date : disabledDates) {
//                    text += "Disabled Date: " + formatter.format(date) + "\n";
//                }
//                System.out.println(text);
////                textView.setText(text);
//            }
//        });

//        Button showDialogButton = (Button) findViewById(R.id.show_dialog_button);
//
//        final Bundle state = savedInstanceState;
//        showDialogButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Setup caldroid to use as dialog
//                dialogCaldroidFragment = new CaldroidFragment();
//                dialogCaldroidFragment.setCaldroidListener(listener);
//
//                // If activity is recovered from rotation
//                final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
//                if (state != null) {
//                    dialogCaldroidFragment.restoreDialogStatesFromKey(
//                            getSupportFragmentManager(), state,
//                            "DIALOG_CALDROID_SAVED_STATE", dialogTag);
//                    Bundle args = dialogCaldroidFragment.getArguments();
//                    if (args == null) {
//                        args = new Bundle();
//                        dialogCaldroidFragment.setArguments(args);
//                    }
//                } else {
//                    // Setup arguments
//                    Bundle bundle = new Bundle();
//                    // Setup dialogTitle
//                    dialogCaldroidFragment.setArguments(bundle);
//                }
//
//                dialogCaldroidFragment.show(getSupportFragmentManager(),
//                        dialogTag);
//            }
//        });


    }

    /**
     * Save current states of the Caldroid here
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_kajian, menu);

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
            public boolean onQueryTextSubmit(final String query) {

                System.out.println("keywords : " + query);
                System.out.println("lat : " + SingletonHelper.getInstance().getLat());
//                TextView textView=(TextView)findViewById(R.id.aa);
//                textView.setText(query);
//                Toast.makeText(self, "query : " + query, Toast.LENGTH_LONG).show();
                RequestParams requestParams = new RequestParams();
                requestParams.put("keyword", query.trim());
                System.out.println(requestParams.toString());
                final ProgressDialog progressDialog = new ProgressDialog(self);
                progressDialog.setMessage("Please wait ...");
                progressDialog.show();
//                mSwipeRefreshLayout.setRefreshing(true);
                AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
                asyncHttpClient.get(Config.FETCH_KAJIAN, requestParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // If the response is JSONObject instead of expected JSONArray
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
//                        }
                        System.out.println("sukses 1");
                        System.out.println(response.toString());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray result) {
//                        if(mSwipeRefreshLayout.isRefreshing()){
//                            mSwipeRefreshLayout.setRefreshing(false);
//                            initListMasjidFromQuery(result);
//                            System.out.println(result.toString());
                        System.out.println("sukses 2");
                        progressDialog.dismiss();
                        if(result.length() <= 0){
                            new AlertDialogWrapper.Builder(self)
                                    .setTitle("")

                                    .setMessage(R.string.kajian_not_found)

                                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();


                                        }
                                    }).setCancelable(false).show();
                        }else{
                            SingletonHelper singletonHelper = SingletonHelper.getInstance();
                            singletonHelper.setJsonArray(result);
                            Intent intent = new Intent(self, KajianActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("keyword", query.trim());
                            startActivity(intent);
                        }


//                        }

//                    System.out.println(result.toString());
                    }
                });
                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu);
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
            // Something else
            case R.id.action_refresh:
                new FetchDataKajian().execute();
//                intent = new Intent(this, ThirdActivity.class);
//                startActivity(intent);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchDataKajian extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(CaldroidSampleActivity.this);
        ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
        TextView t;
        JSONArray jsonArray;
        String jenis;

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
//        public FetchDataKajian(String jenis){
//            this.jenis = jenis;
//        }

        @Override
        protected void onPreExecute() {
//            mSwipeRefreshLayout.setRefreshing(true);
//            if(swipe == false){
                this.dialog.setMessage("Please wait");
                this.dialog.show();
                this.dialog.setCancelable(false);
                this.dialog.setCanceledOnTouchOutside(false);
//            }

        }

        @Override
        protected Boolean doInBackground(final String... args) {
            JsonParser jsonParser = new JsonParser();
            String json = jsonParser
                    .getJSONFromUrl(Config.FETCH_KAJIAN + "?lat=" + SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon());
            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {

                    jsonArray = new JSONArray(json);

                    return true;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

//            if(mSwipeRefreshLayout.isRefreshing()){
//                mSwipeRefreshLayout.setRefreshing(false);
//            }

            List<ModelKajian> modelKajians = new ArrayList<ModelKajian>();
            ArrayList<ModelKajian> modelKajiansRekomendasi = new ArrayList<ModelKajian>();
            for (int i = 0; i < jsonArray.length(); i++){
                ModelKajian modelKajian = new ModelKajian();
                ModelMasjid modelMasjid = new ModelMasjid();
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    modelKajian.setJudul(jsonObject.getString("judul"));
                    modelKajian.setTanggal(new SimpleDateFormat("EEE MMM d h:mm:ss z yyyy").parse(jsonObject.getString("mulaiGMT")));
                    modelKajian.setDeskripsi(jsonObject.getString("deskripsi"));
                    modelKajian.setJam_mulai(jsonObject.getString("jam_mulai"));
                    modelKajian.setJam_akhir(jsonObject.getString("jam_akhir"));

                    modelMasjid.setNama(jsonObject.getString("nama"));
                    modelMasjid.setAlamat(jsonObject.getString("alamat"));

                    modelKajian.setModelMasjid(modelMasjid);

                    modelKajians.add(modelKajian);

                    if(Integer.parseInt(jsonObject.getString("rekomendasi")) == 4){
                        modelKajiansRekomendasi.add(modelKajian);
                    }else{//
                        modelKajians.add(modelKajian);
                    }
                    System.out.println("DARI CALDROID : " + Config.FETCH_KAJIAN + "?lat=" + SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon());
                    setCustomResourceForDates(modelKajian);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex){
                    System.out.println(ex);
                }
            }

//            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View layoutKajian = inflater.inflate(R.layout.layout_list_kajian, null);
//
//            ArrayAdapter arrayAdapterKajian = new ListKajianAdapter(self, (ArrayList) modelKajians);
//
//            ListView listView = (ListView) layoutKajian.findViewById(R.id.listKajian);
//            listView.setAdapter(arrayAdapterKajian);

//            ListView listKajianRekomendasi = (ListView) findViewById(R.id.listRekomendasi);

            ListKajianAdapter listKajianAdapter = new ListKajianAdapter(self, modelKajiansRekomendasi);
//            listKajianRekomendasi.setAdapter(listKajianAdapter);
            caldroidFragment.refreshView();


        }
    }



    private class InitDataKajian extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(CaldroidSampleActivity.this);
        ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
        TextView t;
        JSONArray jsonArray;
        String jenis;

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
//        public FetchDataKajian(String jenis){
//            this.jenis = jenis;
//        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);


        }

        @Override
        protected Boolean doInBackground(final String... args) {
            JsonParser jsonParser = new JsonParser();
            String json = jsonParser
                    .getJSONFromUrl(Config.FETCH_KAJIAN);
            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {

                    jsonArray = new JSONArray(json);

                    return true;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            if(mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.setRefreshing(false);
            }

            ArrayList<ModelKajian> modelKajians = new ArrayList<ModelKajian>();

            for (int i = 0; i < jsonArray.length(); i++){
                ModelKajian modelKajian = new ModelKajian();
                ModelMasjid modelMasjid = new ModelMasjid();
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    modelKajian.setJudul(jsonObject.getString("judul"));
                    modelKajian.setTanggal(new SimpleDateFormat("EEE MMM d h:mm:ss z yyyy").parse(jsonObject.getString("mulaiGMT")));
                    modelKajian.setDeskripsi(jsonObject.getString("deskripsi"));
                    modelKajian.setJam_mulai(jsonObject.getString("jam_mulai"));
                    modelKajian.setJam_akhir(jsonObject.getString("jam_akhir"));

                    modelMasjid.setNama(jsonObject.getString("nama"));
                    modelMasjid.setAlamat(jsonObject.getString("alamat"));

                    modelKajian.setModelMasjid(modelMasjid);



                    setCustomResourceForDates(modelKajian);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex){
                    System.out.println(ex);
                }
            }



//            Toast.makeText(self, "Testing show kajian", Toast.LENGTH_LONG).show();

//            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_list_kajian, null);
////
//            ArrayAdapter arrayAdapterKajian = new ListKajianAdapter(self, modelKajians);
//
//            ListView listView = (ListView) linearLayout.findViewById(R.id.listKajian);
//            listView.setAdapter(arrayAdapterKajian);
////
//            new MaterialDialog.Builder(self)
//                    .title("")
//                    .customView(linearLayout, true)
//                    .positiveText(R.string.close)
//                    .build()
//                    .show();

//            new AlertDialogWrapper.Builder(self)
//                    .setTitle("")
//                    .customView(layoutKajian, true)
//                    .setMessage(R.string.masjid_not_found)
//
//                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//
//
//                        }
//                    }).setCancelable(false).show();

            caldroidFragment.refreshView();

        }
    }

    private void setCustomResourceForDates(ModelKajian modelKajian) {
//        System.out.println("tanggal mulai kajian : " +);
//        Date date = new Date(modelKajian.getTanggal());
//        System.out.println("tanggal date : " + date.toString());

        // Min date is last 7 days
//        cal.add(Calendar.DATE, -18);
//        Date blueDate = cal.getTime();
//        System.out.println("DATE bluedate : " + blueDate);

        // Max date is next 7 days
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 16);
        System.out.println("CAL TIME : " + modelKajian.getTanggal());
        Date tanggalKajian = modelKajian.getTanggal();


//
//        System.out.println("calendar : " + greenDate);

        if (caldroidFragment != null) {

            caldroidFragment.setBackgroundResourceForDate(R.color.green,
                   tanggalKajian);

            caldroidFragment.setTextColorForDate(R.color.white, tanggalKajian);
        }
    }

}