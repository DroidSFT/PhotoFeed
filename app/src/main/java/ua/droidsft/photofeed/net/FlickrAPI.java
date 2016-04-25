package ua.droidsft.photofeed.net;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import ua.droidsft.photofeed.model.FlickrResult;

/**
 * API for Retrofit2.
 * Created by Vlad on 21.04.2016.
 */
public interface FlickrAPI {

    @GET("rest")
    Call<FlickrResult> loadPhotoItems(@QueryMap(encoded = true) Map<String, String> queries);

}
