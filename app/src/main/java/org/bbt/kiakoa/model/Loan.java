package org.bbt.kiakoa.model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.exception.LoanException;

import java.text.DateFormat;
import java.util.Calendar;

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
     * date return
     */
    private long returnDate = -1;

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
        out.writeLong(returnDate);
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
        returnDate = in.readLong();
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
    @NonNull
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
     * This method return -1 if returnDate is not set
     *
     * @return day number. can be negative
     * @throws LoanException if there's no return date
     */
    public int getDatesDifferenceInDays() throws LoanException {
        if (returnDate == -1) {
            throw new LoanException();
        } else {
            Calendar sDate = toCalendar(returnDate);
            Calendar eDate = toCalendar(System.currentTimeMillis());

            // Get the represented date in milliseconds
            long millis1 = sDate.getTimeInMillis();
            long millis2 = eDate.getTimeInMillis();

            // Calculate difference in milliseconds
            long diff = millis2 - millis1;

            return (int)(diff / (24 * 60 * 60 * 1000));
        }
    }

    /**
     * Create calendar instance from time with these values to zero :
     * <ul>
     *     <li>hours</li>
     *     <li>minutes</li>
     *     <li>seconds</li>
     *     <li>millis</li>
     * </ul>
     * @param timestamp time in millis
     * @return calendar instance
     */
    private Calendar toCalendar(long timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
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
    public LoanStatus getStatus() {
        LoanLists loanLists = LoanLists.getInstance();

        if (loanLists.getLentList().contains(this)) {
            return LoanStatus.LENT;
        } else if (loanLists.getBorrowedList().contains(this)) {
            return LoanStatus.BORROWED;
        } else if (loanLists.getArchivedList().contains(this)) {
            return LoanStatus.ARCHIVED;
        } else {
            return LoanStatus.NONE;
        }
    }

    /**
     * Test if this {@link Loan} is archived
     *
     * @return is archived or not
     */
    public boolean isArchived() {
        return getStatus().equals(LoanStatus.ARCHIVED);
    }

    /**
     * Get a String representing loan date
     * if no return date set, "none" is returned
     * @return formatted date
     */
    public String getLoanDateString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(loanDate);
    }

    /**
     * Get a String representing return date
     * if no return date set, "none" is returned
     * @return formatted date or none
     */
    public String getReturnDateString(Context context) {
        String result;
        if (returnDate == -1) {
            result = context.getString(R.string.none);
        } else {
            result = DateFormat.getDateInstance(DateFormat.SHORT).format(returnDate);
        }
        return result;
    }

    /**
     * Get a string representing delay remaining
     *
     * @param context a context for translations
     * @return a string representation of the delay
     */
    public String getReturnDateDelayString(Context context) {

        int delay;
        try {
            delay = getDatesDifferenceInDays();
        } catch (LoanException e) {
            // no return date
            return "";
        }

        int strId;
        if (delay <= 0) {
            strId = R.plurals.plural_day_left;
        } else {
            strId = R.plurals.plural_day_late;
        }

        return context.getResources().getQuantityString(strId, delay, Math.abs(delay));
    }
}
