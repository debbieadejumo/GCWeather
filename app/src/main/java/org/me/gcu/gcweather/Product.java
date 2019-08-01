package org.me.gcu.gcweather;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private final String name;

    public Product(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);

    }
}
