package app.rasendriya.cintamasjid.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.rasendriya.cintamasjid.fragment.FragmentJadwalSholat;
import app.rasendriya.cintamasjid.fragment.FragmentKajian;
import app.rasendriya.cintamasjid.fragment.FragmentMasjid;
import app.rasendriya.cintamasjid.fragment.ScheduleFragment;

/**
 * Created by muhammadagungrizkyana on 6/26/15.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0){
            ScheduleFragment fragmentJadwalSholat = new ScheduleFragment();
            return fragmentJadwalSholat;

        }else if(position == 1){
            FragmentMasjid fragmentMasjid = new FragmentMasjid();
            return fragmentMasjid;

        }else if(position == 2){
            FragmentKajian fragmentKajian = new FragmentKajian();
            return fragmentKajian;
        }

        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
