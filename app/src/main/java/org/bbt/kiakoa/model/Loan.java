package org.bbt.kiakoa.model;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import org.bbt.kiakoa.R;
import org.bbt.kiakoa.activity.MainActivity;
import org.bbt.kiakoa.broadcast.NotificationBroadcastReceiver;
import org.bbt.kiakoa.exception.LoanException;
import org.bbt.kiakoa.tools.Miscellaneous;
import org.bbt.kiakoa.tools.Preferences;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Class defining a loan
 *
 * @author Benoit Bousquet
 */
public class Loan implements Parcelable {

    /**
     * For log
     */
    private static final String TAG = "Loan";

    /**
     * Constant representing a day in millis
     */
    private static final int DAYS_IN_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * Channel id for notifications
     */
    private static final String KIAKOA_CHANNEL_ID = "org.bbt.kiakoa.channelId";

    /**
     * id of the {@link Loan} instance
     */
    @Expose
    private long id = System.currentTimeMillis();

    /**
     * Label to identify the loaned item
     */
    @Expose
    private String item;

    /**
     * if returned or not
     * not returned by default
     */
    private boolean returned = false;

    /**
     * an {@link Uri} path or an index in {@link Miscellaneous#clipartList} for the item picture
     */
    @Expose
    private String itemPicture;

    /**
     * Loan date
     */
    @Expose
    private long loanDate = System.currentTimeMillis();

    /**
     * date return
     */
    @Expose
    private long returnDate = -1;

    /**
     * loan contact
     */
    @Expose
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
        out.writeByte((byte) (returned ? 1 : 0));
        out.writeString(itemPicture);
        out.writeLong(loanDate);
        out.writeLong(returnDate);
        out.writeParcelable(contact, flags);
    }

    /**
     * this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
     */
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
        returned = in.readByte() != 0;
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
     * Constructor for unit tests only
     *
     * @param id   loan id
     * @param item loan item
     */
    Loan(long id, String item) {
        this.id = id;
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
     * item picture Getter
     * This getter will also check if item picture exists if it's an {@link Uri}
     *
     * @param context a context
     * @return item label
     */
    public String getItemPictureSafe(Context context) {
        if ((!isItemPictureDrawable()) && (itemPicture != null)) {
            // It's an URI (not a drawable and not null)
            ContentResolver cr = context.getContentResolver();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cur = cr.query(Uri.parse(itemPicture), projection, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String filePath = cur.getString(0);

                    if (!new File(filePath).exists()) {
                        itemPicture = null;
                    }

                } else {
                    itemPicture = null;
                }
                cur.close();
            } else {
                itemPicture = null;
            }
        }

        return itemPicture;
    }

    /**
     * picture setter
     *
     * @param itemPicture item label
     */
    public void setItemPicture(String itemPicture) {
        this.itemPicture = itemPicture;
    }

    /**
     * Check if item picture is a drawable index in {@link Miscellaneous#clipartList}
     *
     * @return if itemPicture is a numeric value
     */
    public boolean isItemPictureDrawable() {
        try {
            Integer val = Integer.valueOf(itemPicture);
            return (val != null);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * picture setter from {@link Intent}
     * This method will try to find a picture in the intent and update this loan with it
     *
     * @param context a context
     * @param data    source of the image
     */
    public void setItemPicture(Context context, Intent data) {

        Log.i(TAG, "Setting item picture from intent");

        Uri uri = data.getData();
        Bundle extras = data.getExtras();
        Bitmap bitmap = null;

        try {
            // try extracting bitmap from intent data
            if (uri != null) {
                Log.i(TAG, "uri found in getData()");
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } else if (extras != null) {
                Log.i(TAG, "extras found");
                bitmap = (Bitmap) extras.get("data");
            } else {
                Log.e(TAG, "No picture found :-(");
            }

            // resize bitmap and store it on media store
            if (bitmap != null) {
                Log.i(TAG, "bitmap retrieved successfully");
                bitmap = Miscellaneous.scaleDown(bitmap, context.getResources().getInteger(R.integer.thumbnail_size_in_pixel));
                String uriString = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, getItem(), "");
                setItemPicture(uriString);
            } else {
                Log.i(TAG, "failed retrieving bitmap");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if ((loanDate > returnDate) && (returnDate != -1)) {
            Log.i(TAG, "loan date should not be greater than return date");
            setReturnDate(loanDate);
        }
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
     * Set today as returnDate if no returnDate were set
     */
    private void initReturnDate() {
        if (!hasReturnDate()) {
            setReturnDate(System.currentTimeMillis());
        }
    }

    /**
     * return date setter
     *
     * @param returnDate new return date
     */
    public void setReturnDate(long returnDate) {
        this.returnDate = returnDate;
        if ((returnDate < loanDate) && (returnDate != -1)) {
            Log.i(TAG, "return date should not be lower than loan date");
            setLoanDate(returnDate);
        }
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
     * Calculate delay remaining for the loan in days
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

            return (int) (diff / DAYS_IN_MILLIS);
        }
    }

    /**
     * Calculate duration between loan date and return date in days
     *
     * @return day number
     * @throws LoanException if there's no return date
     */
    int getDurationInDays() throws LoanException {
        if (returnDate == -1) {
            throw new LoanException();
        } else {
            Calendar sDate = toCalendar(loanDate);
            Calendar eDate = toCalendar(returnDate);

            // Get the represented date in milliseconds
            long millis1 = sDate.getTimeInMillis();
            long millis2 = eDate.getTimeInMillis();

            // Calculate difference in milliseconds
            long diff = millis2 - millis1;

            return (int) (diff / DAYS_IN_MILLIS);
        }
    }

    /**
     * Create calendar instance from time with these values to zero :
     * <ul>
     * <li>hours</li>
     * <li>minutes</li>
     * <li>seconds</li>
     * <li>millis</li>
     * </ul>
     *
     * @param timestamp time in millis
     * @return calendar instance
     */
    private Calendar toCalendar(long timestamp) {
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
     *
     * @return an Uri picture or null
     */
    public
    @Nullable
    Uri getPicture(Context context) {
        Uri result = null;
        String itemPicture = getItemPictureSafe(context);
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
     * Test if this {@link Loan} is returned
     *
     * @return is returned or not
     */
    public boolean isReturned() {
        return returned;
    }

    /**
     * returned setter
     */
    public void setReturned() {
        this.returned = true;
        initReturnDate();
    }

    /**
     * Get a String representing loan date
     * if no return date set, "none" is returned
     *
     * @return formatted date
     */
    public String getLoanDateString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(loanDate);
    }

    /**
     * Get a String representing return date
     * if no return date set, "none" is returned
     *
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
     * Check if this loan have a return date
     *
     * @return true or false
     */
    public boolean hasReturnDate() {
        return returnDate != -1;
    }

    /**
     * Notify this loan
     *
     * @param context a context
     */
    public void notify(Context context) {


        // intent to add a week to return date
        Intent addWeekIntent = new Intent(NotificationBroadcastReceiver.INTENT_ACTION_ADD_WEEK)
                .putExtra(NotificationBroadcastReceiver.EXTRA_LOAN_ID, this.getId());

        // build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, KIAKOA_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_kiakoa_white)
                .setContentTitle(context.getString(R.string.loan_reached_return_date, getItem()))
                .setContentText(context.getString(R.string.click_see_loan_details))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent));

        // Miscellaneous if loan is already returned
        if (!isReturned()) {
            // intent to set this loan as returned
            Intent returnIntent = new Intent(NotificationBroadcastReceiver.INTENT_ACTION_RETURN_LOAN)
                    .putExtra(NotificationBroadcastReceiver.EXTRA_LOAN_ID, this.getId());
            builder.addAction(R.drawable.ic_return_24dp, context.getString(R.string.set_as_returned), PendingIntent.getBroadcast(context, 0, returnIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        // add a week to this loan
        builder.addAction(R.drawable.ic_add_24dp, context.getString(R.string.add_a_week), PendingIntent.getBroadcast(context, 0, addWeekIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        // loan contact
        if (hasContactId()) {
            // intent to display contact
            Intent contactIntent = new Intent(NotificationBroadcastReceiver.INTENT_ACTION_LOAN_CONTACT)
                    .putExtra(NotificationBroadcastReceiver.EXTRA_LOAN_ID, this.getId());
            builder.addAction(R.drawable.ic_contact_24dp, context.getString(R.string.show_contact), PendingIntent.getBroadcast(context, 0, contactIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        // add picture if available
        try {
            Uri picture = getPicture(context);
            if (picture != null) {
                builder.setLargeIcon(MediaStore.Images.Media.getBitmap(context.getContentResolver(), picture));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Call MainActivity to display loan details
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(MainActivity.EXTRA_NOTIFICATION_LOAN, this);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.notify(getNotificationId(), builder.build());
        } else {
            Log.e(TAG, "Failed notifying this loan : " + getItem() + ". Should not happen because no notification manager found.");
        }
    }

    /**
     * Schedule notification according to returnDate
     *
     * @param context a context
     */
    void scheduleNotification(Context context) {
        // schedule alarm for notification if necessary
        boolean notificationEnabled = Preferences.isReturnDateNotificationEnabled(context);
        if (hasReturnDate() && (notificationEnabled) && (!isReturned())) {

            Log.i(TAG, "Scheduling notification for loan " + getItem() + " / " + getReturnDateString(context));

            Intent intentAlarm = new Intent(NotificationBroadcastReceiver.INTENT_ACTION_LOAN_NOTIFICATION)
                    .putExtra(NotificationBroadcastReceiver.EXTRA_LOAN_ID, this.getId());
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, returnDate, PendingIntent.getBroadcast(context, getNotificationId(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
            } else {
                Log.e(TAG, "Failed set alarmManager for this loan : " + getItem() + ". Should not happen because no alarm manager found.");
            }

        } else

        {
            Log.i(TAG, "No need to schedule notification for loan " + getItem());
        }

    }

    /**
     * Cancel notification schedule if any
     *
     * @param context a context
     */
    void cancelNotificationSchedule(Context context) {
        Log.i(TAG, "Cancelling loan notification : " + getItem());
        Intent intentAlarm = new Intent(NotificationBroadcastReceiver.INTENT_ACTION_LOAN_NOTIFICATION)
                .putExtra(NotificationBroadcastReceiver.EXTRA_LOAN_ID, this.getId());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(PendingIntent.getBroadcast(context, getNotificationId(), intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            Log.e(TAG, "Failed setting alarmManager this loan : " + getItem() + ". Should not happen because no alarm manager found.");
        }
    }

    /**
     * A notification id according loan id
     *
     * @return an integer id
     */
    public int getNotificationId() {
        return Math.abs((int) id);
    }

    /**
     * Test if contact is from contact list
     *
     * @return is contact from contact list
     */
    public boolean hasContactId() {
        return ((contact != null) && (contact.getId() != -1));
    }

    /**
     * Launch activity to display contact card
     *
     * @param context a context
     */
    public void displayContactCard(Context context) {

        if (!hasContactId()) {
            // error while retrieving data
            Log.e(TAG, "Loan does not have a contact from contact list");
        } else {
            Intent contactIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(getContact().getId()));
            contactIntent.setData(uri);
            context.startActivity(contactIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(getNotificationId());
            } else {
                Log.e(TAG, "Failed setting notificationManager this loan : " + getItem() + ". Should not happen because no notification manager found.");
            }
            Log.i(TAG, "Display contact card : " + getContact().getId());
        }
    }

    /**
     * Check if loan data are valid
     *
     * @return loan validity
     */
    boolean isValid() {
        boolean result = (item != null) && (item.length() > 0);

        // check contact if there's any
        if (contact != null) {
            result &= contact.isValid();
        }

        if (!result) {
            Log.e(TAG, "Loan is invalid.");
        }

        return result;
    }

    /**
     * Check if this loan has a contact that can be displayed
     *
     * @return if loan has a contact or not
     */
    boolean hasContact() {
        return (contact != null) && (contact.getName().length() > 0);
    }

    /**
     * This will toggle this loan returned status
     */
    public void toggleReturnedStatus() {
        returned = !returned;
        if (returned) {
            initReturnDate();
        }
    }

    /**
     * Add a week to return date from the current date
     */
    public void addWeek() {
        setReturnDate(System.currentTimeMillis() + (Loan.DAYS_IN_MILLIS * 7));
    }

    /**
     * {@link Comparator} to sort {@link LoanList}
     */
    public static class LoanComparator implements Comparator<Loan> {

        @Override
        public int compare(Loan loan1, Loan loan2) {
            if (loan1.equals(loan2)) {
                // same id
                return 0;
            } else if (loan2.isReturned() != loan1.isReturned()) {
                // one is returned and the other not
                if (loan1.isReturned()) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                // compare names
                return loan1.getItem().toLowerCase().compareTo(loan2.getItem().toLowerCase());
            }
        }

    }
}
