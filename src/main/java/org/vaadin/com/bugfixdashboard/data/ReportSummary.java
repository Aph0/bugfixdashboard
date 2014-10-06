package org.vaadin.com.bugfixdashboard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.util.DateUtil;

public class ReportSummary {

    public static enum ReportType {
        REVIEW("Bugfixes in review"), BFP("Bugfix priority"), SUPPORT(
                "Support Tickets"), TC_BUGFIX("Teamcity Bugs"), SUPPORT_STATUS(
                "Support status");

        private final String realName;

        public String realName() {
            return realName;
        }

        private ReportType(String realName) {
            this.realName = realName;
        }
    };

    private final List<ReportDay> allReportDays = new ArrayList<ReportDay>();
    private final List<ReportDay> daysContainingReport = new ArrayList<ReportDay>();
    private final Map<Date, ReportDay> dayToReportDay = new HashMap<Date, ReportDay>();
    private final Map<ReportType, ReportDay> mostRecentReportForType = new HashMap<ReportSummary.ReportType, ReportDay>();

    public void addDay(ReportDay reportDay) {
        allReportDays.add(reportDay);
        if (reportDay.hasReports()) {
            daysContainingReport.add(reportDay);
        }
        dayToReportDay.put(DateUtil.clearDateBelowDays(reportDay.getDate()),
                reportDay);
        if (reportDay.hasReport(ReportType.BFP)) {
            mostRecentReportForType.put(ReportType.BFP, reportDay);
        }
        if (reportDay.hasReport(ReportType.REVIEW)) {
            mostRecentReportForType.put(ReportType.REVIEW, reportDay);
        }
        if (reportDay.hasReport(ReportType.SUPPORT)) {
            mostRecentReportForType.put(ReportType.SUPPORT, reportDay);
        }
        if (reportDay.hasReport(ReportType.TC_BUGFIX)) {
            mostRecentReportForType.put(ReportType.TC_BUGFIX, reportDay);
        }
        if (reportDay.hasReport(ReportType.SUPPORT_STATUS)) {
            mostRecentReportForType.put(ReportType.SUPPORT_STATUS, reportDay);
        }
    }

    /**
     * Returns a report for given date or null if no report was found for the
     * given date
     * 
     * @param date
     */
    public ReportDay getReportForDate(Date date) {
        return dayToReportDay.get(DateUtil.clearDateBelowDays(date));
    }

    /**
     * Returns an unmodifiable list of all report days found
     * 
     * @return
     */
    public List<ReportDay> getAllReportDays() {
        return Collections.unmodifiableList(allReportDays);
    }

    /**
     * Returns an unmodifiable list of all reports that contain atleast one
     * report
     * 
     * @return
     */
    public List<ReportDay> getDaysWithReports() {
        return Collections.unmodifiableList(daysContainingReport);
    }

    /**
     * Returns the most recent report day, that contains at least one of the
     * different reports
     * 
     * @return
     */
    public ReportDay getMostRecentReport() {
        if (daysContainingReport.size() > 0) {
            return daysContainingReport.get(daysContainingReport.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * Returns the most recent report day, that contains a report of given type
     * 
     * @return
     */
    public ReportDay getMostRecentReport(ReportType forType) {
        return mostRecentReportForType.get(forType);

    }

    /**
     * Returns the oldest day containing a report on the per-configuration
     * searched time span
     * 
     * @return
     */
    public ReportDay getOldestExistingReport() {
        if (daysContainingReport.size() <= 0) {
            return null;
        }
        return daysContainingReport.get(0);
    }

    /**
     * Returns the date for which the oldest report was searched on. This date
     * does not automatically mean that a report is found for this day
     * 
     * @return
     */
    public Date getSearchStartDate() {
        if (allReportDays.size() <= 0) {
            return null;
        }
        return allReportDays.get(0).getDate();
    }

    /**
     * Returns all history for this report level starting from start and ending
     * at end. If no date is specified, everything will be fetched in that
     * "direction"
     */
    public List<DateAndValue> getHistoryDataForReportLevel(ReportType type,
            ReportLevel reportLevel, Date start, Date end) {

        List<ReportDay> days;
        days = getDaysWithReports();
        List<DateAndValue> result = new ArrayList<ReportDay.DateAndValue>();

        boolean hasValues = false;
        for (ReportDay day : days) {
            if (!DateUtil
                    .isInDateRangeOnDayPrecision(start, end, day.getDate())) {
                continue;
            }
            HierarchicalReport report = day.getReportByType(type);
            if (report == null) {
                continue;
            }
            Number value = report.getValueForReportLevel(reportLevel);
            if (value == null) {
                continue;
            }
            result.add(new DateAndValue(day.getDate(), value));
        }
        return result;

    }
}
