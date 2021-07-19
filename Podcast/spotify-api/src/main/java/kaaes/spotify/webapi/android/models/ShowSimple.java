package kaaes.spotify.webapi.android.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

public class ShowSimple implements Parcelable {
    public String publisher;
    public List<String> available_markets;
    public Map<String, String> external_urls;
    public String href;
    public String id;
    public List<Image> images;
    public String name;
    public String type;
    public String uri;
    public String description;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.publisher);
        dest.writeStringList(this.available_markets);
        dest.writeMap(this.external_urls);
        dest.writeString(this.href);
        dest.writeString(this.id);
        dest.writeTypedList(images);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.uri);
        dest.writeString(this.description);
    }

    public ShowSimple() {
    }

    protected ShowSimple(Parcel in) {
        this.publisher = in.readString();
        this.available_markets = in.createStringArrayList();
        this.external_urls = in.readHashMap(ClassLoader.getSystemClassLoader());
        this.href = in.readString();
        this.id = in.readString();
        this.images = in.createTypedArrayList(Image.CREATOR);
        this.name = in.readString();
        this.type = in.readString();
        this.uri = in.readString();
        this.description = in.readString();
    }

    public static final Creator<ShowSimple> CREATOR = new Creator<ShowSimple>() {
        public ShowSimple createFromParcel(Parcel source) {
            return new ShowSimple(source);
        }

        public ShowSimple[] newArray(int size) {
            return new ShowSimple[size];
        }
    };
}
