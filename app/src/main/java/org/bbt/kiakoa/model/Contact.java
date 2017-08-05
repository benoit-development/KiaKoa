package org.bbt.kiakoa.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class of a contact for a lend
 */
public class Contact implements Parcelable {

    /**
     * if from Contacts._ID
     */
    private long id;

    /**
     * name from Contacts.DISPLAY_NAME or not (depending on used constructor)
     */
    private String name;

    /**
     * photo from Contacts.PHOTO_URI
     */
    private String photoUri;

    /**
     * Constructor with contact inforamtion from user contact list
     *
     * @param id contact id from Contacts._ID
     * @param name contact name from Contacts.DISPLAY_NAME
     * @param photoUri photo from Contacts.PHOTO_URI
     */
    public Contact(long id, String name, String photoUri) {
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
    public Contact(String name) {
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
     * @return id
     */
    public long getId() {
        return id;
    }

    /**
     * id setter
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * name getter
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name setter
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * photo uri getter
     * @return photo uri
     */
    public String getPhotoUri() {
        return photoUri;
    }

    /**
     * photo uri setter
     * @param photoUri photo uri
     */
    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }


    @Override
    public String toString() {
        return name;
    }
}
