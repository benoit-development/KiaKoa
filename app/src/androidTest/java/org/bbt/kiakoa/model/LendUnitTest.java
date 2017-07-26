package org.bbt.kiakoa.model;

import android.os.Parcel;

import com.google.gson.Gson;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Lend unit test executed on device
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LendUnitTest {

    private static final String LEND_ITEM = "a label";
    private static final String LEND_ANOTHER_ITEM = "a another label";
    final private Lend lendTest = new Lend(LEND_ITEM);

    @Test
    public void lend_parcelable() throws Exception {

        // get parcel from instance
        Parcel parcel = Parcel.obtain();
        lendTest.writeToParcel(parcel, lendTest.describeContents());
        parcel.setDataPosition(0);

        // build another instance from parcel
        Lend newLend = Lend.CREATOR.createFromParcel(parcel);

        assertEquals(LEND_ITEM, newLend.getItem());
        assertEquals(lendTest.getId(), newLend.getId());
    }

    @Test
    public void lend_class_definition() throws Exception {

        assertEquals(LEND_ITEM, lendTest.getItem());

        lendTest.setItem(LEND_ANOTHER_ITEM);
        assertEquals(LEND_ANOTHER_ITEM, lendTest.getItem());
    }

    @Test
    public void json_test() throws Exception {

        // serialize then instantiate
        String json = lendTest.toJson();
        Lend newLend = new Gson().fromJson(json, Lend.class);

        assertEquals(LEND_ITEM, newLend.getItem());
        assertEquals(lendTest.getId(), newLend.getId());
    }

    @Test
    public void test_toString() throws Exception {
        assertEquals(lendTest.getId() + " : " + lendTest.getItem(), lendTest.toString());
    }

    @Test
    public void test_id() throws Exception {

        Lend anotherLend = new Lend("Another Lend");

        String id1 = lendTest.getId();
        String id2 = anotherLend.getId();

        assertNotEquals(id1, id2);
    }

    @Test
    public void test_equals() {
        // new lend with the same item but different ID
        Lend anotherLend = new Lend(LEND_ITEM);
        assertFalse(lendTest.equals(anotherLend));

        // build another lend with same id
        Lend sameLend = new Gson().fromJson(lendTest.toJson(), Lend.class);
        assertTrue(lendTest.equals(sameLend));

        // equality must be used for Lend deletion. This case represents when Lend is passed and rebuild using parcel
        ArrayList<Lend> list = new ArrayList<>();
        list.add(lendTest);
        list.add(anotherLend);
        assertEquals(2, list.size());
        list.remove(sameLend);
        assertEquals(1, list.size());
    }
}