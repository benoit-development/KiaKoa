package org.bbt.kiakoa.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

/**
 * Class of a contact for a loan
 */
public class Contact implements Parcelable {

    /**
     * id from Contacts._ID
     */
    @Expose
    private long id = -1;

    /**
     * name from Contacts.DISPLAY_NAME or not (depending on used constructor)
     */
    @Expose
    @NonNull
    private final String name;

    /**
     * photo from Contacts.PHOTO_URI
     */
    @Expose
    private String photoUri;

    /**
     * Constructor with contact information from user contact list
     *
     * @param id contact id from Contacts._ID
     * @param name contact name from Contacts.DISPLAY_NAME
     * @param photoUri photo from Contacts.PHOTO_URI
     */
    public Contact(long id, @NonNull String name, String photoUri) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
    }

    /**
     * Constructor with simple named
     * Not linked to user contact list
     *
     * @param name contact name
     */
    public Contact(@NonNull String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(photoUri);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    /**
     * Constructor from a {@link Parcel}
     *
     * @param in the {@link Parcel}
     */
    private Contact(Parcel in) {
        id = in.readLong();
        name = in.readString();
        photoUri = in.readString();
    }

    /**
     * id getter
     * id is set to -1 if there's no contact from contact list
     *
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * name getter
     * @return name
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * photo uri getter
     * @return photo uri
     */
    public String getPhotoUri() {
        return photoUri;
    }


    @Override
    public String toString() {
        return name;
    }

    /**
     * Check if contact seems valid
     * @return contact validity
     */
    public boolean isValid() {
        return name.length() > 0;
    }
}
