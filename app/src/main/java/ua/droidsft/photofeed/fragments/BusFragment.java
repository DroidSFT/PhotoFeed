package ua.droidsft.photofeed.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.path.android.jobqueue.JobManager;

import ua.droidsft.photofeed.App;
import ua.droidsft.photofeed.bus.BusProvider;

/**
 * Base Fragment, that uses Bus and JobManager.
 * Created by Vlad on 21.04.2016.
 */
public abstract class BusFragment extends Fragment {
    protected JobManager mJobManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobManager = App.getApp().getJobManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.bus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BusProvider.bus().unregister(this);
    }
}
