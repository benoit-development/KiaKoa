package org.bbt.kiakoa.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Lend unit test executed on device
 */
public class LendListUnitTest {

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
        assertTrue(lendLists.addLendTo(new Lend("1"), null));
        assertTrue(lendLists.addLendTo(new Lend("2"), null));
        assertTrue(lendLists.addLendTo(new Lend("3"), null));

        assertEquals(3, lendLists.getLendToList().size());


        // add some lend from
        assertTrue(lendLists.addLendFrom(new Lend("4"), null));
        assertTrue(lendLists.addLendFrom(new Lend("5"), null));
        assertTrue(lendLists.addLendFrom(new Lend("6"), null));

        assertEquals(3, lendLists.getLendFromList().size());

        // clear lists
        lendLists.clearLists(null);
        assertEquals(0, lendLists.getLendToList().size());
        assertEquals(0, lendLists.getLendFromList().size());

    }

}