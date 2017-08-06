package org.bbt.kiakoa.model;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
        lendLists.initLists(context);

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
        assertEquals(6, lendLists.getLendCount());

        assertTrue(lendLists.addLendArchive(lend4, context));
        assertEquals(2, lendLists.getLendToList().size());
        assertEquals(2, lendLists.getLendFromList().size());
        assertEquals(2, lendLists.getLendArchiveList().size());
        assertEquals(6, lendLists.getLendCount());

        // clear lists
        lendLists.clearLists(context);
        assertEquals(0, lendLists.getLendToList().size());
        assertEquals(0, lendLists.getLendFromList().size());
        assertEquals(0, lendLists.getLendArchiveList().size());
        assertEquals(0, lendLists.getLendCount());

    }


    @Test
    public void get_instance() throws Exception {

        // get a valid instance
        assertTrue(LendLists.getInstance() != null);

    }

    @Test
    public void lend_lists_shared_preferences() {

        // clear SharedPreferences
        lendLists.clearLists(context);
        assertEquals(0, lendLists.getLendToList().size());

        assertTrue(lendLists.addLendTo(new Lend("1"), context));
        assertTrue(lendLists.addLendTo(new Lend("2"), context));
        assertTrue(lendLists.addLendTo(new Lend("3"), context));

        assertEquals(3, lendLists.getLendToList().size());

    }

    @Test
    public void update_lists() {

        // clear lists
        lendLists.clearLists(context);

        // init lends for tests
        Lend lend = new Lend("a lend");
        Lend sameLend = new Gson().fromJson(lend.toJson(), Lend.class);
        Lend anotherLend = new Lend("another lend");

        // update on empty lists
        assertFalse(lendLists.updateLend(lend, context));

        //update lend to list
        lendLists.clearLists(context);
        lendLists.addLendTo(lend, context);
        assertTrue(lendLists.updateLend(lend, context));
        assertTrue(lendLists.updateLend(sameLend, context));
        assertFalse(lendLists.updateLend(anotherLend, context));
        assertEquals(1, lendLists.getLendToList().size());
        assertEquals(0, lendLists.getLendFromList().size());
        assertEquals(0, lendLists.getLendArchiveList().size());

        //update lend to list
        lendLists.clearLists(context);
        lendLists.addLendFrom(lend, context);
        assertTrue(lendLists.updateLend(lend, context));
        assertTrue(lendLists.updateLend(sameLend, context));
        assertFalse(lendLists.updateLend(anotherLend, context));
        assertEquals(0, lendLists.getLendToList().size());
        assertEquals(1, lendLists.getLendFromList().size());
        assertEquals(0, lendLists.getLendArchiveList().size());

        //update lend to list
        lendLists.clearLists(context);
        lendLists.addLendArchive(lend, context);
        assertTrue(lendLists.updateLend(lend, context));
        assertTrue(lendLists.updateLend(sameLend, context));
        assertFalse(lendLists.updateLend(anotherLend, context));
        assertEquals(0, lendLists.getLendToList().size());
        assertEquals(0, lendLists.getLendFromList().size());
        assertEquals(1, lendLists.getLendArchiveList().size());

    }

}