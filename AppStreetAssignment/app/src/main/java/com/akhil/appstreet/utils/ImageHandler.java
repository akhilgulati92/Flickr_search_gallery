package com.akhil.appstreet.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by Akhil on 26/3/2018.
 */

public abstract class ImageHandler {
    private static final String IMAGE_FOLDERNAME = "FlickrClient";

    /**
     * Creates Image directory & File name
     *
     * @param url Image name are obtained from the url
     * @return
     */
    public static File prepareImageFile(String url) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + IMAGE_FOLDERNAME;
        File dir = new File(filePath);
        //Checks & Creates image folder
        if (!dir.exists())
            dir.mkdirs();

        //..Gets the image name
        String fileName = url.substring(url.lastIndexOf('/'), url.length());

        filePath += File.separator + fileName;
        File image = new File(filePath);
        if (image.exists())
            image.delete();

        return image;
    }

    /**
     * Adds the image to the System Gallery
     *
     * @param filePath
     * @param context
     */
    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    /**
     * Creates an intent to email the image file
     *
     * @param mContext
     * @param ImagePath
     */
    public static void emailImage(Context mContext, String ImagePath) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("application/image");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "zeev.margalit@outlook.com");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "It's raining man....hallelujah!!");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Pizza finally arrived!");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + ImagePath));
        mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
