package org.bbt.kiakoa.model;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LendUnitTest {

    private static final String LEND_ITEM = "a label";
    private static final String LEND_ANOTHER_ITEM = "a another label";
    final private Lend lendTest = new Lend(LEND_ITEM);

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
    }
}