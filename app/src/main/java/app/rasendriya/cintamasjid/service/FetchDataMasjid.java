package app.rasendriya.cintamasjid.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;


import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.activity.DetailMasjidActivity;
import app.rasendriya.cintamasjid.adapter.ListMasjidAdapter;

import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.model.ModelMasjid;
import app.rasendriya.cintamasjid.sources.MasjidSources;


/**
 * Created by muhammadagungrizkyana on 2/25/15.
 */
public class FetchDataMasjid extends AsyncTask<String, Void, Boolean> {

    ProgressDialog dialog;
    Activity activity;
    ListView mListView;
    ArrayList<ModelMasjid> modelMasjids = new ArrayList<>();
    MasjidSources masjidSources;
    ArrayList<Bitmap> bitmaps;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Bitmap bitmap;

    double currentLat, currentLon;

    double start, end;

    public FetchDataMasjid(Activity activity, SwipeRefreshLayout mSwipeRefreshLayout, ListView mListView, double currentLat, double currentLon){
        this.dialog = new ProgressDialog(activity);
        this.activity = activity;
        this.mListView = mListView;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    public FetchDataMasjid(Activity activity, ListView mListView, double currentLat, double currentLon){
        this.dialog = new ProgressDialog(activity);
        this.activity = activity;
        this.mListView = mListView;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
        System.out.println("MASUK FETCH DATA MASJID");

    }

    public FetchDataMasjid(Activity activity){
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Please wait");
        this.dialog.show();
        this.dialog.setCancelable(false);
        this.dialog.setCanceledOnTouchOutside(false);
        start = System.nanoTime();
        System.out.println("start - time : " + start);

    }

    @Override
    protected Boolean doInBackground(final String... args) {
        String urls = Config.FETCH_MASJID + "?lat="+this.currentLat + "&lon=" + this.currentLon;
        System.out.println("URL : " + urls);
        JsonParser jsonParser = new JsonParser();
        String json = jsonParser
                .getJSONFromUrl(urls);
//        String json = "[{\"id\":\"16\",\"nama\":\"Masjid Garuda\",\"alamat\":\"Jl. Rajawali 1 No. 5\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.913144\",\"longitude\":\"107.577042\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/516edcaf5dc1e9912d9c721b6980e89f.jpg\",\"jenis\":\"1\",\"kapasitas\":\"400 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:55:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id\":\"17\",\"nama\":\"Masjid Jami Al Jumuah\",\"alamat\":\"Jl. Pesantren\",\"kota\":\"32.77.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.8237325\",\"longitude\":\"107.2379843\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/afb95a52fb9bb22a5a658fc0c108cfda.jpg\",\"jenis\":\"1\",\"kapasitas\":\"200 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-11 20:11:03\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"176\",\"lokasi_kode\":\"32.77.00.0000\",\"lokasi_nama\":\"KOTA CIMAHI\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"77\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"},{\"id\":\"18\",\"nama\":\"Masjid Attaqwa\",\"alamat\":\"Jl. Dadali 2 no.10\",\"kota\":\"32.73.00.0000\",\"provinsi\":\"32\",\"latitude\":\"-6.911168\",\"longitude\":\"107.576801\",\"foto\":\"http:\\/\\/localhost\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"foto_folder\":\"\\/Applications\\/MAMP\\/htdocs\\/cmasjid\\/upload\\/0f75f3732c981830ca10060aa4d39dc0.jpg\",\"jenis\":\"1\",\"kapasitas\":\"100 Jamaah\",\"status\":\"1\",\"create_dt\":\"2015-03-25 06:58:08\",\"update_dt\":\"0000-00-00 00:00:00\",\"lokasi_ID\":\"172\",\"lokasi_kode\":\"32.73.00.0000\",\"lokasi_nama\":\"KOTA BANDUNG\",\"lokasi_propinsi\":\"32\",\"lokasi_kabupatenkota\":\"73\",\"lokasi_kecamatan\":\"00\",\"lokasi_kelurahan\":\"0000\"}]";

        Log.e("Response: ", "> " + json);

        this.masjidSources = new MasjidSources(activity.getApplicationContext());
        try {
//            LoadImage loadImage;
            this.masjidSources.open();
            if (json != null) {
                try {
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

//                        InputStream is = null;
//                        try {
//                            URL url = new URL(modelMasjid.getFotoCover());
//                            is = url.openConnection().getInputStream();
//                            Bitmap bitMap = BitmapFactory.decodeStream(is);
//
//                            modelMasjid.setCover(bitMap);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

//                        modelMasjid.setCover(bitMap);
//                        masjidSources.createMasjid(modelMasjid);
                        modelMasjids.add(modelMasjid);
                    }

                    modelMasjid_.setModelMasjids(modelMasjids);
//                    SingletonHelper.getInstance().setModelMasjids(modelMasjids);
//                    Log.e("JSON", "> " + now_playing + earned);
                    return true;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }

            }
            this.masjidSources.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        end = System.nanoTime() - start;
        System.out.println("end - time : " + end);

        System.out.println("image");
        System.out.println(bitmaps);

        if(modelMasjids.size() <= 0){
            new AlertDialogWrapper.Builder(activity)
                    .setTitle("")
                    .setMessage(R.string.masjid_not_coverage)
                    .setNegativeButton(R.string.OK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setCancelable(false).show();

        }else{
            modelMasjids = ServiceDistance.sortDistance(modelMasjids);
            SingletonHelper singletonHelper = SingletonHelper.getInstance();
            singletonHelper.setModelMasjids(modelMasjids);
            singletonHelper.setLat(currentLat);
            singletonHelper.setLon(currentLon);

            ArrayAdapter arrayAdapterMasjid = new ListMasjidAdapter(activity, modelMasjids, currentLat, currentLon);
            mListView.setAdapter(arrayAdapterMasjid);

//        Toast.makeText(activity.getApplicationContext(),"all data masjid local : " + masjidSources.getAllMasjid().toString(), Toast.LENGTH_LONG ).show();

//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf" );
//        TextView textIcon = (TextView) rootView.findViewById(R.id.textIcon);
//        textIcon.setTypeface(font);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                dialog.show();

                    final ModelMasjid m = modelMasjids.get(position);
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    ParallaxScrollView parallaxScrollView = (ParallaxScrollView) layoutInflater.inflate(R.layout.activity_detail_masjid, null);

                    Button btnNavigasi = (Button) parallaxScrollView.findViewById(R.id.btnNavigasi);
                    TextView textJarak = (TextView) parallaxScrollView.findViewById(R.id.textJarak);
                    TextView textAlamat = (TextView) parallaxScrollView.findViewById(R.id.alamat);
                    ImageView coverImage = (ImageView) parallaxScrollView.findViewById(R.id.cover);
                    Picasso.with(parallaxScrollView.getContext()).load(m.getFotoCover()).into(coverImage);
                    System.out.println("IMAGE FOR PICASO : " + m.getFotoCover());

//                    ImageView coverImage = (ImageView) parallaxScrollView.findViewById(R.id.coverImg);


                    String j;
                    if((Double.parseDouble(m.getJarak())) < 1){
                        j =  String.valueOf((int) Math.floor(((Double.parseDouble(m.getJarak())) * 100))) + " meter";
                    }else{
                        j = String.valueOf((int)(Double.parseDouble(m.getJarak()))) + " km";
                    }


//                System.out.println("Jarak : " + jarak);

                    textJarak.setText(j);
                    textAlamat.setText(m.getAlamat());

                    btnNavigasi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q="+(m.getLatitude() + "," + m.getLongitude())+"&mode=d");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            activity.startActivity(mapIntent);
                        }
                    });

                    new MaterialDialog.Builder(activity)
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

    class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());
                bitmaps.add(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){

            }else{

            }
        }
    }
}


