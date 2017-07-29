package org.bbt.kiakoa.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Date;

/**
 * Class defining a Land
 *
 * @author Benoit Bousquet
 */
public class Lend implements Parcelable {

    /**
     * id of the {@link Lend} instance
     */
    private long id = System.currentTimeMillis();

    /**
     * Label to identify the loaned item
     */
    private String item;

    /**
     * Lend date
     */
    private long lendDate = new Date().getTime();

    /**
     * date when item should be return
     * init to -1 meaning there's not date
     */
    private long returnDate = -1;

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(item);
        out.writeLong(lendDate);
        out.writeLong(returnDate);
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
        id = in.readLong();
        item = in.readString();
        lendDate = in.readLong();
        returnDate = in.readLong();
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
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * lend date getter
     *
     * @return lend date
     */
    public long getLendDate() {
        return lendDate;
    }

    /**
     * lend date setter
     *
     * @param lendDate new lend date
     */
    public void setLendDate(long lendDate) {
        this.lendDate = lendDate;
    }

    /**
     * return date getter
     *
     * @return return date
     */
    public long getReturnDate() {
        return returnDate;
    }

    /**
     * return date setter
     *
     * @param returnDate new return date
     */
    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
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
    public boolean equals(Object obj) {
        if (obj instanceof Lend) {
            return (id == ((Lend) obj).id);
        } else {
            return super.equals(obj);
        }
    }
}
