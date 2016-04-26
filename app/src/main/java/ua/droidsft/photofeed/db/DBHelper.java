package ua.droidsft.photofeed.db;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

import ua.droidsft.photofeed.model.PhotoItem;

/**
 * Helper class for DB operations.
 * Created by Vlad on 24.04.2016.
 */
public class DBHelper {
    private static DBHelper sDBHelper;

    public static DBHelper get() {
        if (sDBHelper == null) {
            sDBHelper = new DBHelper();
        }
        return sDBHelper;
    }

    private DBHelper() {
    }

    public List<PhotoItem> loadAll() {
        return new Select().from(PhotoItem.class).queryList();
    }

    public synchronized void saveAll(List<PhotoItem> items) {
        new Delete().from(PhotoItem.class).queryClose();
        for (PhotoItem item : items) {
            item.save();
        }
    }
}
