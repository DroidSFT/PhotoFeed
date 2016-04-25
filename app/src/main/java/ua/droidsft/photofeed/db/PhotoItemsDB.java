package ua.droidsft.photofeed.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * DBFlow database class.
 * Created by Vlad on 24.04.2016.
 */

@Database(name = PhotoItemsDB.NAME, version = PhotoItemsDB.VERSION)
public class PhotoItemsDB {
    public static final String NAME = "photo";
    public static final int VERSION = 1;
}
