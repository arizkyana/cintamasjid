package app.rasendriya.cintamasjid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.rasendriya.cintamasjid.R;

/**
 * Created by muhammadagungrizkyana on 6/2/15.
 */
public class FragmentPageFive extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_five, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
