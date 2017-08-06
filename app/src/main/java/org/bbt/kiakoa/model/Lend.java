package org.bbt.kiakoa.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

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
    private long lendDate = System.currentTimeMillis();

    /**
     * date when item should be return
     * init to -1 meaning there's not date
     */
    private long returnDate = -1;

    /**
     * lend contact
     */
    private Contact contact;

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
        out.writeParcelable(contact, flags);
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
        contact = in.readParcelable(Contact.class.getClassLoader());
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
     * contact getter
     *
     * @return contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * contact setter
     *
     * @param contact contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
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

    /**
     * Calculate duration of the lend in days
     *
     * @return day number
     */
    public int getDatesDifferenceInDays() {
        long different = System.currentTimeMillis() - lendDate;
        long dayInMillis = 1000 * 60 * 60 * 24;
        return (int) (different / dayInMillis);
    }

    /**
     * getter on an {@link Uri} picture. It can return the photo of the item or the contact photo if any exists.
     * return null otherwise.
     * @return an Uri picture or null
     */
    public @Nullable Uri getPicture() {
        Uri result = null;
        if (contact != null) {
            String contactPhoto = contact.getPhotoUri();
            if (contactPhoto != null) {
                result = Uri.parse(contactPhoto);
            }
        }

        return result;
    }
}
