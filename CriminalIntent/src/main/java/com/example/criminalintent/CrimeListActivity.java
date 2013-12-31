package com.example.criminalintent;

import android.app.Fragment;

/**
 * Created by cthomas8484 on 12/28/13.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
