package app.rasendriya.cintamasjid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.adapter.RecyclerKajianAdapter;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelKajian;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.service.Config;
import app.rasendriya.cintamasjid.service.JsonParser;

//import app.rasendriya.cintamasjid.adapter.ListKegiatanAdapter;

public class KajianActivity extends ActionBarActivity {
    RecyclerView recList;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    boolean swipe;
    Activity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_kajian);
        self = this;
        setContentView(R.layout.rview_kajian);
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

//        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);

        final Bundle extra = getIntent().getExtras();



        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f15d28")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(SingletonHelper.getInstance().getJsonArray() != null && SingletonHelper.getInstance().getJsonArray().length() > 0){
            List<ModelKajian> modelKajians = new ArrayList<ModelKajian>();
            actionBar.setTitle(extra.get("keyword").toString());
            JSONArray jsonArray = SingletonHelper.getInstance().getJsonArray();
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
                    modelMasjid.setLatitude(jsonObject.getDouble("latitude"));
                    modelMasjid.setLongitude(jsonObject.getDouble("longitude"));

                    modelKajian.setModelMasjid(modelMasjid);

                    modelKajians.add(modelKajian);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex){
                    System.out.println(ex);
                }
            }
            recList.setAdapter(new RecyclerKajianAdapter(self, getApplicationContext(), modelKajians));
        }else{
            actionBar.setTitle(extra.get("tanggalKajian").toString());
//        String[] params = {extra.getString("jenis")};
            new FetchDataKajian(extra.getString("dateQuery")).execute();
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Toast t = Toast.makeText(getApplicationContext(), "Mencari kajian ...", Toast.LENGTH_LONG);
                    t.show();
//                new FetchDataMasjidSwipe().execute();
                    swipe = true;
                    new FetchDataKajian(extra.getString("dateQuery")).execute();
                }
            });
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        SingletonHelper.getInstance().setJsonArray(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kajian, menu);
        return true;
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
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.layout_info, null);

                new MaterialDialog.Builder(self)
                        .title(R.string.action_about)
                        .customView(linearLayout, true)
                        .positiveText(R.string.close)
                        .build()
                        .show();
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

    private class FetchDataKajian extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog = new ProgressDialog(KajianActivity.this);
        ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
        TextView t;
        JSONArray jsonArray;
        String jenis;

        /** progress dialog to show user that the backup is processing. */
        /**
         * application context.
         */
        public FetchDataKajian(String jenis){
            this.jenis = jenis;
            System.out.println("REQUEST : " + Config.FETCH_KAJIAN + "?dateQuery=" + jenis);
        }

        @Override
        protected void onPreExecute() {
            if(swipe == false){
                this.dialog.setMessage("Please wait");
                this.dialog.show();
                this.dialog.setCancelable(false);
                this.dialog.setCanceledOnTouchOutside(false);
            }

        }

        @Override
        protected Boolean doInBackground(final String... args) {
            JsonParser jsonParser = new JsonParser();
            String json = "";
            if(jenis.equals("rekomendasi")){
                json = jsonParser
                        .getJSONFromUrl(Config.FETCH_KAJIAN + "?rekomendasi=" + jenis + "&lat=" + SingletonHelper.getInstance().getLat() + "&lon=" + SingletonHelper.getInstance().getLon());
            }else{
                json = jsonParser
                        .getJSONFromUrl(Config.FETCH_KAJIAN + "?dateQuery=" + jenis);
            }

//            String json = "[{\"id_kegiatan\":\"1\",\"id_masjid\":\"16\",\"judul\":\"Talim Madani \",\"deskripsi\":\"Ustad Dudi Mutaqin\",\"mulai\":\"2015-03-31\",\"akhir\":\"2015-03-31\",\"jam_mulai\":\"10:00:00\",\"jam_akhir\":\"10:30:00\",\"ulangi\":\"2\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:55:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"id\":\"16\",\"nama\":\"Masjid Garuda\",\"alamat\":\"Jl. Rajawali 1 No. 5\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.913144\",\"longitude\":\"107.577042\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"jenis\":\"1\",\"kapasitas\":\"400 Jamaah\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id_kegiatan\":\"2\",\"id_masjid\":\"17\",\"judul\":\"Kajian Al Hikmah\",\"deskripsi\":\"Ust. Agus Heryanto\",\"mulai\":\"2015-04-01\",\"akhir\":\"2015-04-01\",\"jam_mulai\":\"8:00:00\",\"jam_akhir\":\"8:30:00\",\"ulangi\":\"3\",\"status\":\"1\",\"create_dt\":\"2015-03-11 20:11:03\",\"update_dt\":\"0000-00-00 00:00:00\",\"id\":\"17\",\"nama\":\"Masjid Jami Al Jumuah\",\"alamat\":\"Jl. Pesantren\",\"kota\":\"32.77.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.8237325\",\"longitude\":\"107.2379843\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"jenis\":\"1\",\"kapasitas\":\"200 Jamaah\",\"lokasi_ID\":\"176\",\"lokasi_kode\":\"32.77.00.0000\",\"lokasi_nama\":\"KOTA CIMAHI\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"77\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id_kegiatan\":\"3\",\"id_masjid\":\"18\",\"judul\":\"Kajian Siroh Nabawi\",\"deskripsi\":\"Ust. Hanan A. \",\"mulai\":\"2015-04-04\",\"akhir\":\"2015-04-04\",\"jam_mulai\":\"8:00:00\",\"jam_akhir\":\"8:30:00\",\"ulangi\":\"2\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:58:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"id\":\"18\",\"nama\":\"Masjid Attaqwa\",\"alamat\":\"Jl. Dadali 2 no.10\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.911168\",\"longitude\":\"107.576801\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"jenis\":\"1\",\"kapasitas\":\"100 Jamaah\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id_kegiatan\":\"4\",\"id_masjid\":\"16\",\"judul\":\"Ma'rifatulloh\",\"deskripsi\":\"Ust. Aa Gym\",\"mulai\":\"2015-04-01\",\"akhir\":\"2015-04-01\",\"jam_mulai\":\"8:00:00\",\"jam_akhir\":\"8:30:00\",\"ulangi\":\"2\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:55:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"id\":\"16\",\"nama\":\"Masjid Garuda\",\"alamat\":\"Jl. Rajawali 1 No. 5\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.913144\",\"longitude\":\"107.577042\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"jenis\":\"1\",\"kapasitas\":\"400 Jamaah\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"}]";
            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
//                    JSONObject jObj = new JSONObject(json)
//                            .getJSONObject("game_stat");
//                    now_playing = jObj.getString("now_playing");
//                    earned = jObj.getString("earned");
                    jsonArray = new JSONArray(json);

//                    for(int i = 0; i < jsonArray.length(); i++){
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        ModelMasjid modelMasjid = new ModelMasjid();
//                        modelMasjid.setNama(jsonObject.getString("nama"));
//                        modelMasjid.setAlamat(jsonObject.getString("alamat"));
//                        modelMasjid.setFotoCover(jsonObject.getString("foto"));
//                        modelMasjid.setKapasitas(jsonObject.getString("kapasitas"));
//                        modelMasjid.setLatitude(jsonObject.getDouble("lat"));
//                        modelMasjid.setLongitude(jsonObject.getDouble("lon"));
//                        modelMasjids.add(modelMasjid);
//                    }

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

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if(mSwipeRefreshLayout.isRefreshing()){
                mSwipeRefreshLayout.setRefreshing(false);
            }

            if(jsonArray.length() <= 0){
                new AlertDialogWrapper.Builder(self)
                    .setTitle("")

                    .setMessage(R.string.kajian_not_found)

                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                        }
                    }).setCancelable(false).show();
            }

//            recList = (RecyclerView) findViewById(R.id.cardList);
//            recList.setHasFixedSize(true);
//            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
//            llm.setOrientation(LinearLayoutManager.VERTICAL);
//            recList.setLayoutManager(llm);

//            Toast.makeText(getApplicationContext(), jsonArray.toString(), Toast.LENGTH_LONG).show();
            List<ModelKajian> modelKajians = new ArrayList<ModelKajian>();
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
                    modelMasjid.setLatitude(jsonObject.getDouble("latitude"));
                    modelMasjid.setLongitude(jsonObject.getDouble("longitude"));

                    modelKajian.setModelMasjid(modelMasjid);

                    modelKajians.add(modelKajian);

                    System.out.println("POSISI SEKARANG : " + SingletonHelper.getInstance().getLat());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception ex){
                    System.out.println(ex);
                }
            }
//            Toast.makeText(getApplicationContext(), modelKajians.toString(), Toast.LENGTH_LONG).show();
            recList.setAdapter(new RecyclerKajianAdapter(self, getApplicationContext(), modelKajians));


        }
    }
}
