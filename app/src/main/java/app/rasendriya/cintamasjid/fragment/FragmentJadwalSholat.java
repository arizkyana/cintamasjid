package app.rasendriya.cintamasjid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.rasendriya.cintamasjid.R;

/**
 * Created by muhammadagungrizkyana on 6/26/15.
 */
public class FragmentJadwalSholat  extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_jadwal_sholat, container, false);
        return rootView;
    }

}
