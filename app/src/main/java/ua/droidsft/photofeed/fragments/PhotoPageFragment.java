package ua.droidsft.photofeed.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import ua.droidsft.photofeed.R;

/**
 * Fragment shows the source website of selected photo item.
 * Created by Vlad on 25.04.2016.
 */
public class PhotoPageFragment extends Fragment {
    private static final String PHOTO_PAGE_URI = "photo_page_uri";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(PHOTO_PAGE_URI, uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(PHOTO_PAGE_URI);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mUri == null) {
            Toast.makeText(getActivity(), R.string.page_uri_failed, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return null;
        }

        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);
        mProgressBar = (ProgressBar) v.findViewById(R.id.photo_page_progressbar);
        mProgressBar.setMax(100);
        mWebView = (WebView) v.findViewById(R.id.photo_page_web_view);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true); // Needed to be able to open some sites
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Show site loading progress bar and hide it when loading finish
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            // Use news page title as actionbar subtitle
            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                ActionBar actionBar = activity.getSupportActionBar();
                if (actionBar == null) return;
                actionBar.setSubtitle(title);
            }
        });

        // Handle non http(s) links by allowing user to choose an app for it
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] schema = url.split(":");
                if (schema[0].equals("http") || schema[0].equals("https")) {
                    return false;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(Intent.createChooser(i, getString(R.string.chooser_title)));
                    return true;
                }
            }
        });

        mWebView.loadUrl(mUri.toString());

        return v;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
