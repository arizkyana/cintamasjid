package app.rasendriya.cintamasjid.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.rasendriya.cintamasjid.R;

/**
 * Created by muhammadagungrizkyana on 6/2/15.
 */
public class FragmentPageOne extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_one, container, false);
    }
}
