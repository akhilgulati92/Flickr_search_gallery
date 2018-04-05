package com.akhil.appstreet.constants;

/**
 * Created by Akhil on 26/3/2018.
 */

public class Constants {

    //traditional way of holding constants

//    public static final String FLICKR_API_KEY = "d4cfac4a5c1f882569451abc41e92a06";

    public static final String FLICKR_API_KEY = "b8b795e737457d0e3c9761fccf861c58";
    public static final int REQUEST_CODE_FULL_SCREEN = 1;
    public static final String CURRENT_IMAGE_POSITION = "current_image_position";


    public static String URL = "https://api.flickr.com/services/rest?method=flickr.photos.search&api_key=" +FLICKR_API_KEY +"&per_page=30&page=pageno&tags=keyword&format=json&nojsoncallback=1";

    public static final String URL_RECENT_UPLOADS = "https://api.flickr.com/services/rest?method=flickr.photos.getRecent&api_key=" +FLICKR_API_KEY +"&format=json&nojsoncallback=1&per_page=30";


}
