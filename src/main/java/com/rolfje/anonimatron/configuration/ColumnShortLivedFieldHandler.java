package com.rolfje.anonimatron.configuration;

import org.exolab.castor.mapping.FieldHandler;
import org.exolab.castor.mapping.ValidityException;

/**
 * Field handler which makes sure that if {@link Column#isShortLived()} returns false,
 * this handler returns null so that Castor will not put it in the configuration file.
 */
public class ColumnShortLivedFieldHandler implements FieldHandler<Boolean> {

    @Override
    public Boolean getValue(Object o) throws IllegalStateException {
        if (o instanceof Column) {
            return ((Column) o).isShortLived()
                    ? Boolean.TRUE
                    : null;
        }
        return null;
    }

    @Override
    public void setValue(Object o, Boolean b) throws IllegalStateException, IllegalArgumentException {

        boolean shortlived = (b != null && b.booleanValue());

        if (o instanceof Column) {
            ((Column) o).setShortlived(shortlived);
        } else {
            throw new UnsupportedOperationException("Can not set shortlived boolean on object of type " + o.getClass());
        }
    }

    @Override
    public void resetValue(Object o) throws IllegalStateException, IllegalArgumentException {
        setValue(o, false);
    }

    @Override
    public void checkValidity(Object o) throws ValidityException, IllegalStateException {
        // not much to check on a boolean
    }

    @Override
    public Boolean newInstance(Object o) throws IllegalStateException {
        return null;
    }
}
