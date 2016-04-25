package ua.droidsft.photofeed.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.droidsft.photofeed.Constants;

/**
 * Provider of Retrofit2 instance.
 * Created by Vlad on 21.04.2016.
 */
public class RetrofitProvider {
    private static RetrofitProvider sRetrofitProvider;
    private static FlickrAPI sFlickrAPI;

    public static RetrofitProvider get() {
        if (sRetrofitProvider == null) {
            sRetrofitProvider = new RetrofitProvider();
        }
        return sRetrofitProvider;
    }

    private RetrofitProvider() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        sFlickrAPI = retrofit.create(FlickrAPI.class);
    }

    public FlickrAPI getFlickrAPI() {
        return sFlickrAPI;
    }
}
