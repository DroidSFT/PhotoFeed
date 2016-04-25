package ua.droidsft.photofeed.model;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * This is used to map the JSON keys to the object by GSON.
 * Created by Vlad on 31.03.2016.
 */
public class FlickerPhotos {
    @Expose
    public int page;

    @Expose
    public List<PhotoItem> photo;
}
