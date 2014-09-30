package org.vaadin.com.bugfixdashboard.data;

import java.util.Date;

import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;

/**
 * Contains all the reports for a date
 * 
 */
public class ReportDay {

    private final Date date;

    private final HierarchicalReport reviewReport;

    private final HierarchicalReport bfpReport;

    private final HierarchicalReport supportReport;

    private final HierarchicalReport tcReport;

    public ReportDay(Date day, HierarchicalReport reviewReport,
            HierarchicalReport bfpReport, HierarchicalReport supportReport,
            HierarchicalReport tcReport) {
        date = day;
        this.reviewReport = reviewReport;
        this.bfpReport = bfpReport;
        this.supportReport = supportReport;
        this.tcReport = tcReport;
    }

    public HierarchicalReport getReportByType(ReportType type) {
        if (type == ReportType.BFP) {
            return bfpReport;
        } else if (type == ReportType.REVIEW) {
            return reviewReport;
        } else if (type == ReportType.SUPPORT) {
            return supportReport;
        } else if (type == ReportType.TC_BUGFIX) {
            return tcReport;
        } else {
            return null;
        }
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
        return reviewReport != null || bfpReport != null
                || supportReport != null || tcReport != null;
    }

    public boolean hasReport(ReportType reportType) {
        if (reportType == ReportType.BFP) {
            return bfpReport != null;
        } else if (reportType == ReportType.REVIEW) {
            return reviewReport != null;
        } else if (reportType == ReportType.SUPPORT) {
            return supportReport != null;
        } else if (reportType == ReportType.TC_BUGFIX) {
            return tcReport != null;
        } else {
            return false;
        }
    }

    public static class DateAndValue {

        public final Date date;

        public final Integer value;

        public DateAndValue(Date date, Integer value) {
            this.date = date;
            this.value = value;
        }
    }

}