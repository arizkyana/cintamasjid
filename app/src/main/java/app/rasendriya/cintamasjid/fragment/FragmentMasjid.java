package app.rasendriya.cintamasjid.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.activity.MapTag;
import app.rasendriya.cintamasjid.activity.TagMasjid;
import app.rasendriya.cintamasjid.helper.SingletonHelper;
import app.rasendriya.cintamasjid.service.FetchDataMasjid;

/**
 * Created by muhammadagungrizkyana on 6/26/15.
 */
public class FragmentMasjid extends Fragment {

    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_masjid_, container, false);
        ListView listMasjid = (ListView) rootView.findViewById(R.id.activity_main_listview);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildAlertAddCurrentMasjid(getActivity().getApplicationContext());
            }
        });
        new FetchDataMasjid(getActivity(), listMasjid, -6.9167, 107.6000).execute();
        return rootView;
    }



    private void buildAlertAddCurrentMasjid(final Context self){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Tambahkan masjid di lokasi anda sekarang?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(self, TagMasjid.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        getAddress(new LatLng(-6.9167, 107.6000), intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent intent = new Intent(self, MapTag.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
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

            geocoder = new Geocoder(getActivity());


            if (latLng.latitude != 0 || latLng.longitude != 0) {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                //testing address below

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String country = addresses.get(0).getAddressLine(2);
                Log.d("TAG", "address = " + address + ", city =" + city
                        + ", country = " + country);

                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Alamat : " + address, Toast.LENGTH_LONG);
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

                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Alamat : " + address, Toast.LENGTH_LONG);
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
            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
