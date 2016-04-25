package ua.droidsft.photofeed.bus;

import java.util.List;

import ua.droidsft.photofeed.model.PhotoItem;

/**
 * Events for our bus.
 * Created by Vlad on 23.04.2016.
 */
public class PhotoEvents {

    public static final String UNHANDLED_MSG = "UNHANDLED_MSG";
    public static final int UNHANDLED_CODE = -1;

    public static final PhotoItemsLoadFailedEvent FAIL = new PhotoItemsLoadFailedEvent(UNHANDLED_MSG, UNHANDLED_CODE);

    public static class PhotoItemsLoadSuccessEvent {
        private final List<PhotoItem> mPhotoItems;

        public PhotoItemsLoadSuccessEvent(List<PhotoItem> photoItems) {
            mPhotoItems = photoItems;
        }

        public List<PhotoItem> getPhotoItems() {
            return mPhotoItems;
        }
    }

    public static class PhotoItemsLoadFailedEvent {
        private String mErrorMessage;
        private int mCode;

        public PhotoItemsLoadFailedEvent(String errorMessage, int code) {
            mErrorMessage = errorMessage;
            mCode = code;
        }

        public String getErrorMessage() {
            return mErrorMessage;
        }

        public int getCode() {
            return mCode;
        }
    }

    public static class PhotoItemsEmptyListEvent {
        private String mQuery;

        public PhotoItemsEmptyListEvent(String query) {
            mQuery = query;
        }

        public String getQuery() {
            return mQuery;
        }
    }
}
