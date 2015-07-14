package app.rasendriya.cintamasjid.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.afollestad.materialdialogs.MaterialDialog;

import app.rasendriya.cintamasjid.R;
import app.rasendriya.cintamasjid.activity.MasjidActivity;
import app.rasendriya.cintamasjid.fragment.FragmentPageFive;
import app.rasendriya.cintamasjid.fragment.FragmentPageFour;
import app.rasendriya.cintamasjid.fragment.FragmentPageOne;
import app.rasendriya.cintamasjid.fragment.FragmentPageThree;
import app.rasendriya.cintamasjid.fragment.FragmentPageTwo;

/**
 * Created by muhammadagungrizkyana on 6/2/15.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    FragmentManager fragmentManager;
    public PagerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new FragmentPageOne();
            case 1:
                return new FragmentPageTwo();
            case 2:
                return new FragmentPageThree();
            case 3:
                return new FragmentPageFour();
            case 4:
                return new FragmentPageFive();

        }

        if(position == 4){

        }

        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
