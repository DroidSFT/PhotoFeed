package ua.droidsft.photofeed.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.webkit.WebView;

import ua.droidsft.photofeed.R;
import ua.droidsft.photofeed.fragments.PhotoPageFragment;

/**
 * Activity for PhotoPageFragment.
 * Created by Vlad on 25.04.2016.
 */
public class PhotoPageActivity extends SingleFragmentActivity {

    // Construct intent for news page
    public static Intent newIntent(Context context, Uri newsPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(newsPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    // Handle action bar home button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, PhotoListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Allow to navigate back in PhotoPageFragment's WebView
    @Override
    public void onBackPressed() {
        PhotoPageFragment fragment = (PhotoPageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            super.onBackPressed();
            return;
        }

        WebView webView = fragment.getWebView();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
