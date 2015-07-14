package app.rasendriya.cintamasjid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.model.ModelMasjid;


/**
 * Created by muhammadagungrizkyana on 1/16/15.
 */
public class ListMasjidAdapter extends ArrayAdapter<ArrayList> {

    private Context context ;
    private ArrayList values;
    private Bitmap bitmap;

    ModelMasjid m;
    double currentLat;
    double currentLon;
    public ListMasjidAdapter(Context context, ArrayList values, double currentLat, double currentLon) {
        super(context, R.layout.adapter_list, values);
        this.context = context;
        this.values = values;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
    }

    public ListMasjidAdapter(Context context, ArrayList values, double currentLat, double currentLon, Bitmap bitmap) {
        super(context, R.layout.adapter_list, values);
        this.context = context;
        this.values = values;
        this.currentLat = currentLat;
        this.currentLon = currentLon;
        this.bitmap = bitmap;
    }

    public View getView(int position, View containView, ViewGroup parent){
        m = (ModelMasjid) values.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_list, parent, false);
        TextView firstLine = (TextView) rowView.findViewById(R.id.judulKajian);
        final TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
        TextView jarak = (TextView) rowView.findViewById(R.id.jarak);
//        if(m.getFotoCover() != null){
//        CircularImageView circularImageView = (CircularImageView) rowView.findViewById(R.id.icon);
//        circularImageView.setImageBitmap(m.getCover());
//            new ImageDownloader(circularImageView).execute(m.getFotoCover());
//            circularImageView.setIm;

//            circularImageView.setImageBitmap(getImageBitmap(m.getFotoCover()));
//        }


        System.out.println("FOTO COVER : " + m.getFotoCover());
//        icon.setImageBitmap(new RoundedImageView(rowView.getContext()));

        firstLine.setText(m.getNama());

        System.out.println("nama masjid : " + m.getNama());
        secondLine.setText(m.getAlamat());
//        getAddress(m.getLatitude(), m.getLongitude(), secondLine);
//        jarak.setText(String.valueOf(Math.floor(SphericalUtil.computeDistanceBetween(new LatLng(currentLat, currentLon), new LatLng(m.getLatitude(), m.getLongitude())));
//        jarak.setText(String.valueOf(Math.floor(ServiceDistance.countDistance(currentLat, currentLon, m.getLatitude(), m.getLongitude()))) + " KM");
       // double jarakCount = Math.floor(SphericalUtil.computeDistanceBetween(new LatLng(currentLat, currentLon), new LatLng(m.getLatitude(), m.getLongitude()))) / 1000;
//        Location current = new Location("Lokasi anda");
//        current.setLatitude(currentLat);
//        current.setLongitude(currentLon);
//
//        Location tujuan = new Location("Lokasi Tujuan");
//        tujuan.setLatitude(m.getLatitude());
//        tujuan.setLongitude(m.getLongitude());
//
//        double distance = current.distanceTo(tujuan);
        String j;
        if((Double.parseDouble(m.getJarak())) < 1){
            j =  String.valueOf((int) Math.floor(((Double.parseDouble(m.getJarak())) * 100))) + " meter";
        }else{
            j = String.valueOf((int)(Double.parseDouble(m.getJarak()))) + " km";
        }


        Picasso.with(context).load(m.getFotoCover()).into(icon);

        jarak.setText(j);





        return rowView;
    }

    class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        CircularImageView bmImage;



        public ImageDownloader(CircularImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

//

    public void getAddress(double latitude, double longitude, TextView alamat) {
        List<Address> addresses = null;
        try {

            Geocoder geocoder;

            geocoder = new Geocoder(getContext());

            if (latitude != 0 || longitude != 0) {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                //testing address below

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city);

                alamat.setText(address);

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}
