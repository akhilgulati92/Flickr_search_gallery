package com.akhil.appstreet.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;

import com.akhil.appstreet.model.FlickrImage;

/**
 * Created by Akhil on 27/03/18.
 */

public class CommonUtils {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    public static String constructURL(FlickrImage flickrImage) {
        String constructedURL = null;
        if( constructedURL == null ) {

            constructedURL =  "https://farm" +
                    flickrImage.getFarm() +
                    ".staticflickr.com/" +
                    flickrImage.getServer() +
                    "/" +
                    flickrImage.getId() +
                    "_" +
                    flickrImage.getSecret() +
                    ".jpg";
        }

        return constructedURL;
    }

}
