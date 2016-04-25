package ua.droidsft.photofeed.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import ua.droidsft.photofeed.AppPrefs;
import ua.droidsft.photofeed.R;
import ua.droidsft.photofeed.activities.PhotoPageActivity;
import ua.droidsft.photofeed.bus.PhotoEvents;
import ua.droidsft.photofeed.jobs.GetPhotoItemsJob;
import ua.droidsft.photofeed.model.PhotoItem;
import ua.droidsft.photofeed.net.PicassoProvider;

/**
 * Fragment with list of photo items.
 * Created by Vlad on 21.04.2016.
 */
public class PhotoListFragment extends BusFragment {
    private static final String TAG = "PhotoListFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mNoPhotosText;

    private List<PhotoItem> mPhotoItems = new ArrayList<>();
    private PhotoAdapter mAdapter;

    private ThumbPreloader mThumbPreloader;

    private SearchView mSearchView;

    public static PhotoListFragment newInstance() {
        return new PhotoListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // No need to re-instantiate fragment on rotates, etc.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_list, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.list_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateItems(true, AppPrefs.getQuery(getActivity()));
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.photo_list_recycler);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mNoPhotosText = (TextView) v.findViewById(R.id.no_photos_text);

        mThumbPreloader = new ThumbPreloader();

        // If items list is not empty (ex. after rotate), setup adapter immediately.
        if (!mPhotoItems.isEmpty()) {
            setupAdapter();
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // If items list is empty, try to load items from DB.
        if (mPhotoItems.isEmpty()) {
            updateItemsSpin(false, AppPrefs.getQuery(getActivity()));
        }
    }

    private void setupAdapter() {
        if (isAdded()) { // No need to setup adapter if fragment is not added to its activity.
            mThumbPreloader.resetPreloader();
            if (mAdapter == null) {
                mAdapter = new PhotoAdapter(mPhotoItems);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setPhotoItems(mPhotoItems);
                mAdapter.notifyDataSetChanged();
                if (mRecyclerView.getAdapter() == null) {
                    mRecyclerView.setAdapter(mAdapter);
                }
            }
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoPhotosText.setVisibility(View.GONE);
        }
    }

    // Update items without SwipeRefreshLayout's spin.
    private void updateItems(boolean forceUpdate, String query) {
        mJobManager.addJobInBackground(new GetPhotoItemsJob(forceUpdate, query));
    }

    // Update items with SwipeRefreshLayout's spin.
    private void updateItemsSpin(final boolean forceUpdate, final String query) {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                updateItems(forceUpdate, query);
            }
        });
    }

    @Subscribe
    public void onLoadingSuccessful(PhotoEvents.PhotoItemsLoadSuccessEvent event) {
        Log.d(TAG, "onLoadingSuccessful");
        List<PhotoItem> items = event.getPhotoItems();
        mPhotoItems.clear();
        mPhotoItems.addAll(items);
        setupAdapter();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onLoadingFailed(PhotoEvents.PhotoItemsLoadFailedEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        showSnackbar(getString(R.string.service_error));
        Log.d(TAG, "Loading failed: code = " + event.getCode() + ", msg = " + event.getErrorMessage());
    }

    @Subscribe
    public void onEmptyListReceived(PhotoEvents.PhotoItemsEmptyListEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (event.getQuery() != null) {
            showSnackbar(getString(R.string.empty_list, event.getQuery()));
        } else {
            showSnackbar(getString(R.string.service_error));
        }
        Log.d(TAG, "Empty list received, query: " + event.getQuery());
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateItemsSpin(true, AppPrefs.getQuery(getActivity()));
                    }
                });
        View view = snackbar.getView();
        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(2);
        snackbar.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.photo_list_menu, menu);

        MenuItem search = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) search.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AppPrefs.saveQuery(getActivity(), query);
                updateItemsSpin(true, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) AppPrefs.saveQuery(getActivity(), null);
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setQuery(AppPrefs.getQuery(getActivity()), false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                updateItemsSpin(true, AppPrefs.getQuery(getActivity()));
                return true;
            case R.id.menu_recent:
                AppPrefs.saveQuery(getActivity(), null);
                updateItemsSpin(true, null);
                mSearchView.setQuery("", false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private PhotoItem mPhotoItem;
        private TextView mTitleText;
        private TextView mSubtitleText;
        private ImageView mImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView) itemView.findViewById(R.id.list_title);
            mSubtitleText = (TextView) itemView.findViewById(R.id.list_subtitle);
            mImageView = (ImageView) itemView.findViewById(R.id.list_image);
            itemView.setOnClickListener(this);
        }

        public void bindPhotoItem(PhotoItem photoItem) {
            mPhotoItem = photoItem;
            String title = mPhotoItem.getTitle();
            if (title == null || title.equals("")) {
                title = getString(R.string.no_title);
            }
            mTitleText.setText(title);
            DateFormat df = DateFormat.getDateTimeInstance();
            mSubtitleText.setText(df.format(mPhotoItem.getDateupload()));

            PicassoProvider.get(getActivity()).getPicasso()
                    .load(mPhotoItem.getUrl_s())
                    .placeholder(R.drawable.ic_placeholder)
                    .noFade()
                    .fit()
                    .centerCrop()
                    .into(mImageView, mThumbPreloader);
        }

        @Override
        public void onClick(View v) {
            Intent i = PhotoPageActivity.newIntent(getActivity(), mPhotoItem.getPageUri());
            startActivity(i);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<PhotoItem> mPhotoItems;

        public PhotoAdapter(List<PhotoItem> photoItems) {
            mPhotoItems = photoItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.photo_item, parent, false);
            return new PhotoHolder(v);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            PhotoItem item = mPhotoItems.get(position);
            holder.bindPhotoItem(item);
        }

        public void setPhotoItems(List<PhotoItem> items) {
            mPhotoItems = items;
        }

        @Override
        public int getItemCount() {
            return mPhotoItems.size();
        }
    }

    // Picasso callback for pre-loading images.
    // Once user scrolled one page, images from next to pages are queued for pre-loading.
    // When second page is scrolled, the 4th and the 5th pages are passed for pre-loading and so on.
    private class ThumbPreloader implements Callback {
        private int mPicCount = 0;
        private int mVisibleItems = 0;
        private int mLastPreloadEnd = 0;

        public void resetPreloader() {
            mPicCount = mVisibleItems = mLastPreloadEnd = 0;
        }

        @Override
        public void onSuccess() {
            mPicCount++;
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (mVisibleItems == 0) mVisibleItems = manager.getChildCount();
            if (mPicCount == mVisibleItems) {
                int itemsCount = mPhotoItems.size();
                if (mLastPreloadEnd < itemsCount) {

                    int preloadStart = mLastPreloadEnd;
                    if (preloadStart == 0) preloadStart = mVisibleItems;

                    int preloadEnd = mLastPreloadEnd + mVisibleItems * 2;
                    if (preloadEnd > itemsCount) preloadEnd = itemsCount;

                    List<PhotoItem> preloadList = mPhotoItems.subList(preloadStart, preloadEnd);

                    PicassoProvider.get(getActivity()).preload(preloadList);

                    mLastPreloadEnd = preloadEnd;
                }
                mPicCount = 0;
            }
        }

        @Override
        public void onError() {

        }
    }

}
