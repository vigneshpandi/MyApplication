package com.bluemapletech.hippatextapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Kumaresan on 20-10-2016.
 */

public class ViewPageAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments = new ArrayList();
    ArrayList<String> fragmentTitles = new ArrayList();


    public void addFragments(Fragment fragments, String tabTitles){
        this.fragments.add(fragments);
        this.fragmentTitles.add(tabTitles);
    }

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position);
    }
}
