package com.rolfje.anonimatron.configuration;

import org.exolab.castor.mapping.ValidityException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColumnShortLivedFieldHandlerTest {

    private ColumnShortLivedFieldHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ColumnShortLivedFieldHandler();
    }

    @Test
    public void getValue() {
        assertTrue(handler.getValue(shortLivedColumn()));
        assertNull(handler.getValue(column()));
        assertNull(handler.getValue(new Object()));
    }

    @Test
    public void setValue() {
        Column column = column();
        handler.setValue(column, true);
        assertTrue(column.isShortLived());

        handler.setValue(column, false);
        assertFalse(column.isShortLived());

        try {
            handler.setValue(new Object(), true);
            fail("Can not set boolean on non-column object.");
        } catch (UnsupportedOperationException e) {
            // ok
        }
    }

    @Test
    public void resetValue() {
        Column column = shortLivedColumn();
        handler.resetValue(column);
        assertFalse(column.isShortLived());
    }

    @Test
    public void checkValidity() throws ValidityException {
        boolean[] all = new boolean[]{true, false};
        for (boolean b : all) {
            // Should not do anything
            Column column = column();
            column.setShortlived(b);
            handler.checkValidity(column);
            assertEquals(b, column.isShortLived());
        }
    }

    @Test
    public void newInstance() {
        assertNull(handler.newInstance(new Object()));
    }

    private Column shortLivedColumn() {
        Column column = new Column();
        column.setShortlived(true);
        return column;
    }

    private Column column() {
        return new Column();
    }
}