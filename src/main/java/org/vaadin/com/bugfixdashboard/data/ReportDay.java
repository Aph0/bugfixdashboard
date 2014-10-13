package org.vaadin.com.bugfixdashboard.data;

import java.util.Date;
import java.util.LinkedHashMap;

import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;

/**
 * Contains all the reports for a date
 * 
 */
public class ReportDay {

    private final Date date;

    private final LinkedHashMap<ReportType, HierarchicalReport> reportTypeToReportComponent = new LinkedHashMap<ReportSummary.ReportType, HierarchicalReport>();

    public ReportDay(
            Date day,
            LinkedHashMap<ReportType, HierarchicalReport> orderedReportComponents) {
        date = day;
        reportTypeToReportComponent.putAll(orderedReportComponents);
    }

    public HierarchicalReport getReportByType(ReportType reportType) {

        return reportTypeToReportComponent.get(reportType);

    }

    public Date getDate() {
        return date;
    }

    /**
     * Returns true if this day contains at least one report.
     * 
     * @return
     */
    public boolean hasReports() {

        return !reportTypeToReportComponent.isEmpty();

    }

    public boolean hasReport(ReportType reportType) {
        return reportTypeToReportComponent.get(reportType) != null;
    }

    public static class DateAndValue {

        public final Date date;

        public final Number value;

        public DateAndValue(Date date, Number value) {
            this.date = date;
            this.value = value;
        }
    }

    public LinkedHashMap<ReportType, HierarchicalReport> getReportTypesToReports() {
        return reportTypeToReportComponent;
    }

}
