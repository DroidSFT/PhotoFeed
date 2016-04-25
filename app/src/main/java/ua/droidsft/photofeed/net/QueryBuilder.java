package ua.droidsft.photofeed.net;

import java.util.HashMap;
import java.util.Map;

import ua.droidsft.photofeed.Constants;

/**
 * Helper for building queries for Retrofit2.
 * Created by Vlad on 24.04.2016.
 */
public class QueryBuilder {
    public static final String RECENT_METHOD = "flickr.photos.getRecent";
    public static final String SEARCH_METHOD = "flickr.photos.search";
    public static final String EXTRAS = "date_upload,url_s";
    public static final String PER_PAGE = "50";
    public static final String FORMAT = "json";
    public static final String NO_JSON_CALLBACK = "1";

    public static Map<String, String> getQuery(String searchText) {
        Map<String, String> map = new HashMap<>();

        if (searchText != null) {
            map.put("method", SEARCH_METHOD);
            map.put("text", searchText);
        } else {
            map.put("method", RECENT_METHOD);
        }

        map.put("api_key", Constants.API_KEY);
        map.put("extras", EXTRAS);
        map.put("per_page", PER_PAGE);
        map.put("format", FORMAT);
        map.put("nojsoncallback", NO_JSON_CALLBACK);

        return map;
    }
}
