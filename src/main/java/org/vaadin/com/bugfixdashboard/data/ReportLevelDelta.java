package org.vaadin.com.bugfixdashboard.data;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ReportLevelDelta {

    private final ReportLevel original;
    private final ReportLevel other;

    private final Number delta;

    /**
     * The delta value is counted as original.value - other.value. If other or
     * original is null, the delta will be null.
     * 
     * @param original
     * @param compareTo
     */
    public ReportLevelDelta(ReportLevel original, ReportLevel other) {
        this.original = original;
        this.other = other;
        if (other == null || original == null || original.getValue() == null
                || other.getValue() == null) {
            delta = null;
            return;
        } else {
            Number originalVal = original.getValue();

            if (originalVal instanceof Double || originalVal instanceof Float) {
                delta = (Double) original.getValue()
                        - (Double) other.getValue();
            } else if (originalVal instanceof BigDecimal) {
                delta = ((BigDecimal) originalVal).subtract((BigDecimal) other
                        .getValue());

            } else if (originalVal instanceof BigInteger) {
                delta = ((BigInteger) originalVal).subtract((BigInteger) other
                        .getValue());

            } else if (originalVal instanceof Integer) {
                delta = (Integer) originalVal - (Integer) other.getValue();
            } else if (originalVal instanceof Long) {
                delta = (Long) originalVal - (Long) other.getValue();
            } else {
                throw new IllegalArgumentException("Undefined Number type!");
            }

        }

    }

    public ReportLevel getOriginal() {
        return original;
    }

    public ReportLevel getOther() {
        return other;
    }

    public Number getDelta() {
        return delta;
    }

}
