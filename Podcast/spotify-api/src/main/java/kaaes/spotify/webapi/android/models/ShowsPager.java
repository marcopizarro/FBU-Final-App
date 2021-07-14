package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ShowsPager implements Parcelable {
    public Pager<Show> shows;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.shows, 0);
    }

    public ShowsPager() {
    }

    protected ShowsPager(Parcel in) {
        this.shows = in.readParcelable(Pager.class.getClassLoader());
    }

    public static final Creator<ShowsPager> CREATOR = new Creator<ShowsPager>() {
        public ShowsPager createFromParcel(Parcel source) {
            return new ShowsPager(source);
        }

        public ShowsPager[] newArray(int size) {
            return new ShowsPager[size];
        }
    };
}
