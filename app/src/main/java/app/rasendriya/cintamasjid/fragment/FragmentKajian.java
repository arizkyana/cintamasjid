package app.rasendriya.cintamasjid.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.roomorama.caldroid.DateGridFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.activity.CmMainActivity;
import app.rasendriya.cintamasjid.activity.KajianActivity;
import app.rasendriya.cintamasjid.adapter.ListKajianAdapter;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelKajian;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.JsonParser;

/**
 * Created by muhammadagungrizkyana on 6/26/15.
 */
public class FragmentKajian extends Fragment {
    FragmentKajian self;
    CaldroidFragment caldroidFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_kajian_, container, false);
        self = this;
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

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
        FragmentTransaction t = self.getActivity().getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener

        Button btnRekomendasi = (Button) rootView.findViewById(R.id.btnRekomendasiKajian);

        btnRekomendasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(self.getActivity(), KajianActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.putExtra("tanggalKajian", "Rekomendasi Kajian Bulan Ini");
                i.putExtra("dateQuery", "rekomendasi");
                startActivity(i);
            }
        });

        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onCaldroidViewCreated() {

            }
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
                Intent i = new Intent(self.getActivity(), KajianActivity.class);
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



        };

        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new FetchDataKajian().execute();
    }

    private class FetchDataKajian extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(self.getActivity());
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

            ListKajianAdapter listKajianAdapter = new ListKajianAdapter(self.getActivity(), modelKajiansRekomendasi);
//            listKajianRekomendasi.setAdapter(listKajianAdapter);
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
