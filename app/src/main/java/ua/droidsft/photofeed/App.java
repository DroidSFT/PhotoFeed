package ua.droidsft.photofeed;

import android.app.Application;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * App configured for Job Queue and DBFlow.
 * Created by Vlad on 21.04.2016.
 */
public class App extends Application {
    private static App sApp;
    private JobManager mJobManager;

    public App() {
        sApp = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        configJobManager();
    }

    private void configJobManager() {
        Configuration config = new Configuration.Builder(this)
                .minConsumerCount(1)
                .maxConsumerCount(3)
                .consumerKeepAlive(30)
                .build();

        mJobManager = new JobManager(this, config);
    }

    public JobManager getJobManager() {
        return mJobManager;
    }

    public static App getApp() {
        return sApp;
    }


}
