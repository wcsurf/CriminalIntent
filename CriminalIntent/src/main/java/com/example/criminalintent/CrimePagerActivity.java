package com.example.criminalintent;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;


public class CrimePagerActivity extends Activity {

    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fragmentManager = getFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });
    }
}
