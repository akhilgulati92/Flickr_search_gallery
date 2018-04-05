package com.akhil.appstreet.utils;

import com.akhil.appstreet.model.FlickrImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * Created by Akhil on 28/3/2018.
 */

public class DatabaseHelper<T extends RealmModel> {

    private Realm mRealm;
    private static DatabaseHelper mDBHelper;

    public static DatabaseHelper getInstance() {
        if (mDBHelper == null)
            mDBHelper = new DatabaseHelper();

        return mDBHelper;
    }

    public DatabaseHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    /**
     * Process a successful API result
     *
     * @param result   the API's response
     * @param dataType The requested dataType(FlickrImage in this project's case)
     * @return list of the items parsed
     */
    public void processApiResult(ArrayList<FlickrImage> result, Class<T[]> dataType) {

        // Open a transaction to store items into the realm
        try {
            getRealm().beginTransaction();
            getRealm().insertOrUpdate(result);
            getRealm().commitTransaction();
        } catch (Exception ex) {
            ex.printStackTrace();
            getRealm().cancelTransaction();
        }
        //    return list;
    }

    /**
     * Gets all the data by dataType from the database
     *
     * @param dataType - the dataType (FlickrImage in this project's case)
     * @return list of the items
     */
    public List<FlickrImage> getCache(Class<T> dataType, String key) {
        //  return this.getRealm().where(dataType).findAll();
        RealmResults<T> query = this.getRealm().where(dataType).findAll();

        List<FlickrImage> flickerImage = (List<FlickrImage>) query;
        ArrayList<FlickrImage> flickrImageObj = new ArrayList<FlickrImage>();
        for (FlickrImage obj : flickerImage) {
            if (obj.getTag().equalsIgnoreCase(key)) {
                flickrImageObj.add(obj);
            }
        }
        return flickrImageObj;
    }

    /**
     * used to make Safe Calls to DB Instance
     *
     * @return
     */
    private Realm getRealm() {
        if (this.mRealm == null || this.mRealm.isClosed())
            mRealm = Realm.getDefaultInstance();
        return mRealm;
    }

    /**
     * Closes Realm instance
     */
    public void close() {
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
    }
}
