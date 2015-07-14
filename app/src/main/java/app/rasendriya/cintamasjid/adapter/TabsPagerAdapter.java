package app.rasendriya.cintamasjid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.rasendriya.cintamasjid.fragment.KajianFragment;
import app.rasendriya.cintamasjid.fragment.MasjidFragment;
import app.rasendriya.cintamasjid.fragment.SholatFragment;

/**
 * Created by muhammadagungrizkyana on 3/13/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
//                    return new TopRatedFragment();
//                    buildGoogleApiClient();
                return new SholatFragment();
            case 1:
                // Games fragment activity
//                    return new GamesFragment();
                return new MasjidFragment();
            case 2:
                // Movies fragment activity
//                    return new MoviesFragment();
                return new KajianFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
