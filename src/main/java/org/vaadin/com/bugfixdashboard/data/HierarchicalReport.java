package org.vaadin.com.bugfixdashboard.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
import org.vaadin.com.bugfixdashboard.util.DateUtil;

public class HierarchicalReport implements Report {

    private List<ReportLevel> reportLevelRoots = new ArrayList<ReportLevel>();

    private Map<ReportLevel, ReportLevelDelta> reportLevelDeltas = new HashMap<ReportLevel, ReportLevelDelta>();

    private final Date date;

    private final ReportType reportType;

    public HierarchicalReport(Date date, ReportType reportType) {
        this.date = date;

        this.reportType = reportType;
    }

    /**
     * Adds a report. If parent is null, the report will become root
     * 
     * @param parent
     * @param name
     * @param value
     */
    public void addRootLevel(ReportLevel reportLevel) {
        reportLevelRoots.add(reportLevel);
    }

    public String getHeader() {
        return reportType.getName();
    }

    public List<ReportLevel> getReportRoots() {
        return reportLevelRoots;
    }

    @Override
    public Date getReportDate() {
        return date;
    }

    @Override
    public Number getValueForReportLevel(ReportLevel reportLevel) {
        List<ReportLevel> allReportLevels = getAllReportLevelsAsList();
        int index = allReportLevels.indexOf(reportLevel);
        if (index < 0) {
            return null;
        } else {
            return allReportLevels.get(index).getValue();
        }
    }

    /**
     * Returns all the report levels in a flat manner, using lowestDepth as
     * starting point and only levels deeper or equal will be returned. For a
     * certain date the levels may vary
     * 
     * @return
     */
    public List<ReportLevel> getAllReportLevelsAsList(int lowestDepth) {
        List<ReportLevel> result = new ArrayList<ReportLevel>();
        for (ReportLevel level : getReportRoots()) {
            result.addAll(getFlatListFor(level));
        }

        // This is a little optimization
        if (lowestDepth <= 1) {
            return result;
        }

        List<ReportLevel> filteredResult = new ArrayList<ReportLevel>();
        for (ReportLevel level : result) {
            if (level.getLevel() >= lowestDepth) {
                filteredResult.add(level);
            }
        }

        return filteredResult;
    }

    /**
     * Returns all the report levels in a flat manner. For a certain date the
     * levels may vary
     * 
     * @return
     */
    public List<ReportLevel> getAllReportLevelsAsList() {
        return getAllReportLevelsAsList(1);
    }

    private List<ReportLevel> getFlatListFor(ReportLevel level) {
        List<ReportLevel> result = new ArrayList<ReportLevel>();
        result.add(level);

        if (!level.hasChildren()) {
            return result;
        }

        for (ReportLevel child : level.getChildren()) {
            result.addAll(getFlatListFor(child));
        }
        return result;
    }

    /**
     * Creates (or recreates) deltas for all common report levels (the
     * differences between this and the other report). If otherReport is null,
     * the deltas will be cleared;
     * 
     * @param otherReport
     */
    public void createReportLevelDeltas(HierarchicalReport otherReport) {
        reportLevelDeltas.clear();
        if (otherReport == null) {
            return;
        }

        List<ReportLevel> allLevelsThis = this.getAllReportLevelsAsList();
        List<ReportLevel> allLevelsOther = otherReport
                .getAllReportLevelsAsList();
        for (ReportLevel rl : allLevelsThis) {
            int index = allLevelsOther.indexOf(rl);
            if (index >= 0) {
                ReportLevelDelta rld = new ReportLevelDelta(rl,
                        allLevelsOther.get(index));

                reportLevelDeltas.put(rl, rld);
            }
        }

    }

    /**
     * returns ReportLevelDeltas (differences between this report and another),
     * but only if createDeltas() has been called. Returns null if the
     * ReportLevel is not found in both reports or the other ReportLevel is null
     * 
     * @param date
     * @return
     */
    public ReportLevelDelta getDeltaFor(ReportLevel reportLevel) {
        return reportLevelDeltas.get(reportLevel);
    }

    /**
     * The report is considered due if the dates do not match on a
     * day-granularity
     * 
     * @return
     */
    public boolean isDue() {
        Date now = new Date();
        return !DateUtil.isInDateRangeOnDayPrecision(now, now, date);

    }

    /**
     * Returns the report type
     * 
     * @return
     */
    public ReportType getReportType() {
        return reportType;
    }
}
