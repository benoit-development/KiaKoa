package org.bbt.kiakoa.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class defining a Land
 *
 * @author Benoit Bousquet
 */
public class Lend implements Parcelable {

    /**
     * Unique instance of {@link AtomicInteger} generating unique ID for {@link Lend} instances
     */
    private final static AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * id of the {@link Lend} instance
     */
    private String id = UUID.randomUUID().toString();;

    /**
     * Label to identify the loaned item
     */
    private String item;

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(item);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Lend> CREATOR = new Parcelable.Creator<Lend>() {
        public Lend createFromParcel(Parcel in) {
            return new Lend(in);
        }

        public Lend[] newArray(int size) {
            return new Lend[size];
        }
    };

    /**
     * Constructor from a {@link Parcel}
     *
     * @param in the {@link Parcel}
     */
    private Lend(Parcel in) {
        id = in.readString();
        item = in.readString();
    }

    /**
     * Constructor
     *
     * @param item lend item
     */
    public Lend(String item) {
        this.item = item;
    }

    /**
     * item Getter
     *
     * @return item label
     */
    public String getItem() {
        return item;
    }

    /**
     * item setter
     *
     * @param item item label
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * id getter
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Serialize this lend to JSON
     *
     * @return json string
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return id + " : " + item;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Lend) {
            return (id == ((Lend) obj).id);
        } else {
            return super.equals(obj);
        }
    }
}
