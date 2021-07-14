package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

/**
 * <a href="https://developer.spotify.com/web-api/object-model/#album-object-full">Album object model</a>
 */
public class Show extends ShowSimple implements Parcelable {
    public List<Copyright> copyrights;
    public Map<String, String> external_ids;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(copyrights);
        dest.writeMap(this.external_ids);
    }

    public Show() {
    }

    protected Show(Parcel in) {
        super(in);
        this.copyrights = in.createTypedArrayList(Copyright.CREATOR);
        this.external_ids = in.readHashMap(ClassLoader.getSystemClassLoader());
    }

    public static final Creator<Show> CREATOR = new Creator<Show>() {
        public Show createFromParcel(Parcel source) {
            return new Show(source);
        }

        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}