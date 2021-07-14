package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Shows implements Parcelable {
    public List<Show> shows;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(shows);
    }

    public Shows() {
    }

    protected Shows(Parcel in) {
        this.shows = in.createTypedArrayList(Show.CREATOR);
    }

    public static final Creator<Shows> CREATOR = new Creator<Shows>() {
        public Shows createFromParcel(Parcel source) {
            return new Shows(source);
        }

        public Shows[] newArray(int size) {
            return new Shows[size];
        }
    };
}
