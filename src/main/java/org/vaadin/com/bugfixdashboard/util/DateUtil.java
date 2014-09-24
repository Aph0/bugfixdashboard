package org.vaadin.com.bugfixdashboard.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * Clears the given date's hours, minutes, seconds and milliseconds to 0
     */
    public static Date clearDateBelowDays(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * This is an inclusive test. If tested date is at the same day as end or
     * start, these will be included. Also, if any of the ranges are null, the
     * test will pass
     */
    public static boolean isInDateRangeOnDayPrecision(Date start, Date end,
            Date tested) {

        Date startCleared = clearDateBelowDays(start);
        Date endCleared = clearDateBelowDays(end);
        Date testedCleared = clearDateBelowDays(tested);

        if (startCleared != null && startCleared.after(testedCleared)) {
            return false;
        }

        if (endCleared != null && endCleared.before(testedCleared)) {
            return false;
        }

        return true;

    }
}
