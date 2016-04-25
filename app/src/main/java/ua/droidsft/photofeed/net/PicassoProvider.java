package ua.droidsft.photofeed.net;

import android.content.Context;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;

import java.util.List;

import ua.droidsft.photofeed.model.PhotoItem;

/**
 * Provider of Picasso instance.
 * Created by Vlad on 22.04.2016.
 */
public final class PicassoProvider {
    private static PicassoProvider sPicassoProvider;
    private static Picasso sPicasso;
    private Context mContext;

    public static PicassoProvider get(Context context) {
        if (sPicassoProvider == null) {
            sPicassoProvider = new PicassoProvider(context);
        }
        return sPicassoProvider;
    }

    private PicassoProvider(Context context) {
        mContext = context.getApplicationContext();
        sPicasso = new Picasso.Builder(mContext)
                .build();
        Picasso.setSingletonInstance(sPicasso);
//        sPicasso.setIndicatorsEnabled(true);
    }

    public Picasso getPicasso() {
        return sPicasso;
    }

    public void preload(List<PhotoItem> itemsList) {
        for (PhotoItem item : itemsList) {
            String url = item.getUrl_s();
            if (!TextUtils.isEmpty(url)) {
                Picasso.with(mContext)
                        .load(url)
                        .fetch();
            }
        }
    }
}
