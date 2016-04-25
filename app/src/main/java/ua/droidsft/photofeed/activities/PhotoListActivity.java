package ua.droidsft.photofeed.activities;

import android.support.v4.app.Fragment;

import ua.droidsft.photofeed.fragments.PhotoListFragment;

/**
 * Activity for PhotoListFragment.
 * Created by Vlad on 21.04.2016.
 */
public class PhotoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoListFragment.newInstance();
    }
}
