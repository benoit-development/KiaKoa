package org.bbt.kiakoa.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Lend unit test executed on device
 */
public class LendListUnitTest {

    /**
     * a {@link Context}
     */
    private Context context = InstrumentationRegistry.getContext();

    @Test
    public void get_instance() throws Exception {

        // get a valid instance
        assertTrue(LendLists.getInstance() != null);

    }

    @Test
    public void lend_lists_sharedpreferencces() {

        // clear sharedpreferences
        LendLists lendLists = LendLists.getInstance();

        lendLists.clearLists(context);
        assertEquals(0, lendLists.getLendToList().size());

        assertTrue(lendLists.addLendTo(new Lend("1"), context));
        assertTrue(lendLists.addLendTo(new Lend("2"), context));
        assertTrue(lendLists.addLendTo(new Lend("3"), context));

        assertEquals(3, lendLists.getLendToList().size());

    }

}