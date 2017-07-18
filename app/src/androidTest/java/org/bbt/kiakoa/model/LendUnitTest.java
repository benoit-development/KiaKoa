package org.bbt.kiakoa.model;

import android.os.Parcel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Lend unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LendUnitTest {

    private static final String LEND_ITEM = "a label";
    private Lend lendTest = new Lend(LEND_ITEM);


    @Test
    public void lend_parcelable() throws Exception {

        // get parcel from instance
        Parcel parcel = Parcel.obtain();
        lendTest.writeToParcel(parcel, lendTest.describeContents());
        parcel.setDataPosition(0);

        // build another instance from parcel
        Lend newLend = Lend.CREATOR.createFromParcel(parcel);

        assertEquals(LEND_ITEM, newLend.getItem());
    }
}