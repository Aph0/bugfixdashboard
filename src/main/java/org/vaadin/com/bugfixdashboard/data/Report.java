package org.vaadin.com.bugfixdashboard.data;

import java.util.Date;

public interface Report {

    Date getReportDate();

    /**
     * Returns the value for given report level. Reportlevels must be considered
     * equal if the level and name match.
     * 
     * @param reportLevel
     * @return
     */
    Integer getValueForReportLevel(ReportLevel reportLevel);

}
