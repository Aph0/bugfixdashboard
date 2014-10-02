package org.vaadin.com.bugfixdashboard.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HierarchicalReport implements Report {

    private final String header;

    private List<ReportLevel> reportLevelRoots = new ArrayList<ReportLevel>();

    private final Date date;

    public HierarchicalReport(Date date, String header) {
        this.date = date;
        this.header = header;
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
        return header;
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

}
