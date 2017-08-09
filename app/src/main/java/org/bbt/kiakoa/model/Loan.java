package org.bbt.kiakoa.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

/**
 * Class defining a loan
 *
 * @author Benoit Bousquet
 */
public class Loan implements Parcelable {

    /**
     * id of the {@link Loan} instance
     */
    private long id = System.currentTimeMillis();

    /**
     * Label to identify the loaned item
     */
    private String item;

    /**
     * an {@link Uri} path for the item picture
     */
    private String itemPicture;

    /**
     * Loan date
     */
    private long loanDate = System.currentTimeMillis();

    /**
     * loan contact
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
        out.writeString(itemPicture);
        out.writeLong(loanDate);
        out.writeParcelable(contact, flags);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Loan> CREATOR = new Parcelable.Creator<Loan>() {
        public Loan createFromParcel(Parcel in) {
            return new Loan(in);
        }

        public Loan[] newArray(int size) {
            return new Loan[size];
        }
    };

    /**
     * Constructor from a {@link Parcel}
     *
     * @param in the {@link Parcel}
     */
    private Loan(Parcel in) {
        id = in.readLong();
        item = in.readString();
        itemPicture = in.readString();
        loanDate = in.readLong();
        contact = in.readParcelable(Contact.class.getClassLoader());
    }

    /**
     * Constructor
     *
     * @param item loan item
     */
    public Loan(String item) {
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
     * item picture Getter
     *
     * @return item label
     */
    public String getItemPicture() {
        return itemPicture;
    }

    /**
     * item setter
     *
     * @param itemPicture item label
     */
    public void setItemPicture(String itemPicture) {
        this.itemPicture = itemPicture;
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
     * loan date getter
     *
     * @return loan date
     */
    public long getLoanDate() {
        return loanDate;
    }

    /**
     * loan date setter
     *
     * @param loanDate new loan date
     */
    public void setLoanDate(long loanDate) {
        this.loanDate = loanDate;
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
     * Serialize this loan to JSON
     *
     * @return json string
     */
    String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Loan) {
            return (id == ((Loan) obj).id);
        } else {
            return super.equals(obj);
        }
    }

    /**
     * Calculate duration of the loan in days
     *
     * @return day number
     */
    public int getDatesDifferenceInDays() {
        long different = System.currentTimeMillis() - loanDate;
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
        if (itemPicture != null) {
            result = Uri.parse(itemPicture);
        } else if (contact != null) {
            String contactPhoto = contact.getPhotoUri();
            if (contactPhoto != null) {
                result = Uri.parse(contactPhoto);
            }
        }

        return result;
    }

    /**
     * Method to determine this {@link Loan} state in the {@link LoanLists} instance
     *
     * @return current state
     */
    public LoanState getState() {
        LoanLists loanLists = LoanLists.getInstance();

        if (loanLists.getLentList().contains(this)) {
            return LoanState.LENT;
        } else if (loanLists.getBorrowedList().contains(this)) {
            return LoanState.BORROWED;
        } else if (loanLists.getArchiveList().contains(this)) {
            return LoanState.ARCHIVED;
        } else {
            return LoanState.NONE;
        }
    }

    /**
     * Test if this {@link Loan} is archived
     *
     * @return is archived or not
     */
    public boolean isArchived() {
        return getState().equals(LoanState.ARCHIVED);
    }
}
