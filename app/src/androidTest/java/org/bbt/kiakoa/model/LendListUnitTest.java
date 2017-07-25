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
    private final Context context = InstrumentationRegistry.getContext();

    final private LendLists lendLists = LendLists.getInstance();

    @Test
    public void test_instance() throws Exception {

        // test instance
        assertTrue(LendLists.getInstance() != null);

    }

    @Test
    public void test_lend_lists() throws Exception {

        // init lists
        lendLists.initLists(null);

        // add some lend to
        Lend lend1 = new Lend("1");
        assertTrue(lendLists.addLendTo(lend1, context));
        assertTrue(lendLists.addLendTo(new Lend("2"), context));
        assertTrue(lendLists.addLendTo(new Lend("3"), context));

        assertEquals(3, lendLists.getLendToList().size());


        // add some lend from
        Lend lend4 = new Lend("4");
        assertTrue(lendLists.addLendFrom(lend4, context));
        assertTrue(lendLists.addLendFrom(new Lend("5"), context));
        assertTrue(lendLists.addLendFrom(new Lend("6"), context));

        assertEquals(3, lendLists.getLendFromList().size());


        // add some lend archive
        assertTrue(lendLists.addLendArchive(lend1, context));
        assertEquals(2, lendLists.getLendToList().size());
        assertEquals(3, lendLists.getLendFromList().size());
        assertEquals(1, lendLists.getLendArchiveList().size());

        assertTrue(lendLists.addLendArchive(lend4, context));
        assertEquals(2, lendLists.getLendToList().size());
        assertEquals(2, lendLists.getLendFromList().size());
        assertEquals(2, lendLists.getLendArchiveList().size());

        // clear lists
        lendLists.clearLists(context);
        assertEquals(0, lendLists.getLendToList().size());
        assertEquals(0, lendLists.getLendFromList().size());
        assertEquals(0, lendLists.getLendArchiveList().size());

    }


    @Test
    public void get_instance() throws Exception {

        // get a valid instance
        assertTrue(LendLists.getInstance() != null);

    }

    @Test
    public void lend_lists_sharedpreferencces() {

        // clear SharedPreferences
        LendLists lendLists = LendLists.getInstance();

        lendLists.clearLists(context);
        assertEquals(0, lendLists.getLendToList().size());

        assertTrue(lendLists.addLendTo(new Lend("1"), context));
        assertTrue(lendLists.addLendTo(new Lend("2"), context));
        assertTrue(lendLists.addLendTo(new Lend("3"), context));

        assertEquals(3, lendLists.getLendToList().size());

    }

}