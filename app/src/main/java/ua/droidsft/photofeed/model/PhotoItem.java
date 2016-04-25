package ua.droidsft.photofeed.model;

import android.net.Uri;

import com.google.gson.annotations.Expose;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

import ua.droidsft.photofeed.Constants;
import ua.droidsft.photofeed.db.PhotoItemsDB;

/**
 * Model of our Photo Item for. Used by GSON and DBFlow.
 * Created by Vlad on 21.04.2016.
 */

@Table(databaseName = PhotoItemsDB.NAME)
public class PhotoItem extends BaseModel{

    @Column
    @PrimaryKey(autoincrement = false)
    @Expose
    public String id;

    @Column
    @Expose
    public String title;

    @Column
    @Expose
    public long dateupload;

    @Column
    @Expose
    public String url_s;

    @Column
    @Expose
    public String owner;

    @Override
    public String toString() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateupload() {
        return new Date(dateupload * 1000);
    }

    public String getUrl_s() {
        return url_s;
    }

    public Uri getPageUri() {
        return Uri.parse(Constants.PHOTOS_URL)
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }

}
