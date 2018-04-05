package com.akhil.appstreet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmModel;
import io.realm.annotations.RealmClass;

/**
 * Created by Akhil on 26/3/2018.
 */

@RealmClass
public class FlickrImage implements Parcelable, RealmModel {

    //POJO class (encapsulation) that holds data and processes data to construct pictures url

    public FlickrImage() {
    }

    private String id;
    private String secret;
    private String server;
    private String farm;
    private String title;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String tag;

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    private boolean flipped;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {

        out.writeString(this.id);
        out.writeString(this.secret);
        out.writeString(this.server);
        out.writeString(this.farm);
        out.writeString(this.title);
    }

    protected FlickrImage(Parcel in){
        this.id = in.readString();
        this.secret = in.readString();
        this.server = in.readString();
        this.farm = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<FlickrImage> CREATOR = new Parcelable.Creator<FlickrImage>() {
        @Override
        public FlickrImage createFromParcel(Parcel source) {
            return new FlickrImage(source);
        }

        @Override
        public FlickrImage[] newArray(int size) {
            return new FlickrImage[size];
        }
    };
}
