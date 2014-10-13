package org.vaadin.com.bugfixdashboard.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.util.DateUtil;

public class ReportSummary {

    private final List<ReportDay> allReportDays = new ArrayList<ReportDay>();
    private final List<ReportDay> daysContainingReport = new ArrayList<ReportDay>();
    private final Map<Date, ReportDay> dayToReportDay = new HashMap<Date, ReportDay>();
    private final Map<ReportType, ReportDay> mostRecentReportForType = new LinkedHashMap<ReportSummary.ReportType, ReportDay>();

    private final List<ReportType> allReportTypes = new ArrayList<ReportSummary.ReportType>();

    /**
     * The id determines the Report type. If the id's match, two report types
     * are equals, otherwise NOT
     * 
     * 
     */
    public static class ReportType {

        private final String name;
        private final String numberFormat;
        private final String filePrefix;
        private final Integer id;
        private final Integer pieChartParseLevelStart;

        public ReportType(String name, String numberFormat, String filePrefix,
                Integer id, Integer pieChartParseLevelStart) {
            this.name = name;
            this.pieChartParseLevelStart = pieChartParseLevelStart;
            if (id == null) {
                throw new IllegalArgumentException(
                        "id cannot be null. Please add id prefixes to the components in the properties file [id=1,2,...,n]");
            }
            this.id = id;
            if (numberFormat == null) {
                this.numberFormat = "#,#";
            } else {
                this.numberFormat = numberFormat;
            }
            this.filePrefix = filePrefix;

        }

        public String getName() {
            return name;
        }

        public String getNumberFormat() {
            return numberFormat;
        }

        public String getFilePrefix() {
            return filePrefix;
        }

        @Override
        public boolean equals(Object obj) {
            ReportType other = (ReportType) obj;
            if (other == null) {
                return false;
            }
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        public Integer getPieChartParseLevelStart() {
            return pieChartParseLevelStart;
        }

    };

    public void addDay(ReportDay reportDay) {
        allReportDays.add(reportDay);
        if (reportDay.hasReports()) {
            daysContainingReport.add(reportDay);

            for (ReportType type : reportDay.getReportTypesToReports().keySet()) {
                if (!allReportTypes.contains(type)) {
                    allReportTypes.add(type);
                }
            }
        }
        dayToReportDay.put(DateUtil.clearDateBelowDays(reportDay.getDate()),
                reportDay);

        LinkedHashMap<ReportType, HierarchicalReport> reportTypesToReports = reportDay
                .getReportTypesToReports();

        if (!reportTypesToReports.isEmpty()) {
            for (ReportType existing : reportTypesToReports.keySet()) {
                // This overwrites the existing report if a newer one is found
                mostRecentReportForType.put(existing, reportDay);
            }

        }

    }

    /**
     * Returns all report types that have been registered upon project startup
     * 
     * @return
     */
    public List<ReportType> getAllExistingReportTypes() {
        return Collections.unmodifiableList(allReportTypes);
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
            return new ReportDay(
                    new Date(),
                    new LinkedHashMap<ReportSummary.ReportType, HierarchicalReport>());
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
