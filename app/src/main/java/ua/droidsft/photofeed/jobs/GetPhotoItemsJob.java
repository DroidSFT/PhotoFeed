package ua.droidsft.photofeed.jobs;

import android.util.Log;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.droidsft.photofeed.bus.BusProvider;
import ua.droidsft.photofeed.bus.PhotoEvents;
import ua.droidsft.photofeed.db.DBHelper;
import ua.droidsft.photofeed.model.FlickrResult;
import ua.droidsft.photofeed.model.PhotoItem;
import ua.droidsft.photofeed.net.QueryBuilder;
import ua.droidsft.photofeed.net.RetrofitProvider;

/**
 * Job for getting list of photo items.
 * Created by Vlad on 23.04.2016.
 */
public class GetPhotoItemsJob extends Job {
    private static final String TAG = "GetPhotoItemsJob";

    private static final AtomicInteger sJobCounter = new AtomicInteger(0);
    private final int mId;
    private boolean mForceUpdate;
    private String mQuery;

    public GetPhotoItemsJob(boolean forceUpdate, String query) {
        super(new Params(Priority.LOW).groupBy(Groups.MAIN));
        mId = sJobCounter.incrementAndGet();
        mForceUpdate = forceUpdate;
        mQuery = query;
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        if (mId != sJobCounter.get()) {
            // Other jobs has been added after this, so cancel this.
            return;
        }

        if (mForceUpdate) { // If forced update from network, do it without referencing to DB
            loadFromNet(mQuery);
        } else { // Otherwise try to load items from DB
            List<PhotoItem> items = loadFromDB();
            if (items != null && items.size() > 0) {
                Log.d(TAG, "onRun: received items from db");
                BusProvider.bus().post(new PhotoEvents.PhotoItemsLoadSuccessEvent(items));
            } else { // If DB is empty, try to load items from network
                loadFromNet(mQuery);
            }
        }
    }

    @Override
    protected void onCancel() {
        BusProvider.bus().post(PhotoEvents.FAIL);
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }

    private void loadFromNet(final String query) {
        RetrofitProvider.get().getFlickrAPI()
                .loadPhotoItems(QueryBuilder.getQuery(query))
                .enqueue(new Callback<FlickrResult>() {
                    @Override
                    public void onResponse(Call<FlickrResult> call, Response<FlickrResult> response) {
                        if (response.isSuccessful() && response.body().photos != null) {
                            List<PhotoItem> items = response.body().photos.photo;
                            if (items.size() > 0) {
                                BusProvider.bus().post(new PhotoEvents.PhotoItemsLoadSuccessEvent(items));
                                DBHelper.get().saveAll(items);
                            } else {
                                BusProvider.bus().post(new PhotoEvents.PhotoItemsEmptyListEvent(query));
                            }
                        } else {
                            BusProvider.bus().post(new PhotoEvents.PhotoItemsLoadFailedEvent(response.message(), response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<FlickrResult> call, Throwable error) {
                        if (error != null && error.getMessage() != null) {
                            BusProvider.bus().post(new PhotoEvents.PhotoItemsLoadFailedEvent(error.getMessage(), PhotoEvents.UNHANDLED_CODE));
                        } else {
                            BusProvider.bus().post(PhotoEvents.FAIL);
                        }
                    }
                });
    }

    private List<PhotoItem> loadFromDB() {
        return DBHelper.get().loadAll();
    }

}
