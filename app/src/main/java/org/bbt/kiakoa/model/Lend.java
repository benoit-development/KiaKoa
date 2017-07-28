package org.bbt.kiakoa.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Date;
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
    private long id = System.currentTimeMillis();

    /**
     * Label to identify the loaned item
     */
    private String item;

    /**
     * Lend date
     */
    private Date lendDate = new Date();

    /**
     * date when item should be return
     */
    private Date returnDate;

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(item);
        out.writeLong(lendDate.getTime());
        // if no return Date is set then put -1
        out.writeLong((returnDate == null)?-1:returnDate.getTime());
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
        lendDate = new Date(in.readLong());
        // if return was set to -1 then set returnDate to null
        long returnDateLong = in.readLong();
        returnDate = (returnDateLong == -1)?null:new Date(returnDateLong);
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
    public long getId() {
        return id;
    }

    /**
     * lend date getter
     * @return lend date
     */
    public Date getLendDate() {
        return lendDate;
    }

    /**
     * lend date setter
     * @param lendDate new lend date
     */
    public void setLendDate(Date lendDate) {
        this.lendDate = lendDate;
    }

    /**
     * return date getter
     * @return return date
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * return date setter
     * @param returnDate new return date
     */
    public void setReturnDate(Date returnDate) {
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
        if(obj instanceof Lend) {
            return (id == ((Lend) obj).id);
        } else {
            return super.equals(obj);
        }
    }
}
