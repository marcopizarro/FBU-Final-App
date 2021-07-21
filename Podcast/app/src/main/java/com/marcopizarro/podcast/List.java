package com.marcopizarro.podcast;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.Date;

import kaaes.spotify.webapi.android.models.Show;

@ParseClassName("List")
public class List extends ParseObject {
    public static final String KEY_CREATION = "createdAt";
    public static final String KEY_NAME = "name";
    public static final String KEY_POD = "podcasts";
    public static final String KEY_COVER = "cover";
    public static final String KEY_USER = "user";

    public Date getDate() {
        return getDate(KEY_CREATION);
    }


    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public JSONArray getPodcasts() {
        return getJSONArray(KEY_POD);
    }

    public void setPodcasts(JSONArray podcasts) {
        put(KEY_POD, podcasts);
    }

    public ParseFile getCover() {
        return getParseFile(KEY_COVER);
    }

    public void setCover(ParseFile cover) {
        put(KEY_COVER, cover);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }
}
