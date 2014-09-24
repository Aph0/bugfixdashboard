package org.vaadin.com.bugfixdashboard.data;

/**
 * Used for getting difference between two reports at first level only
 * 
 */
public class FlatReportComparisonData {

    private final HierarchicalReport oldReport;
    private final HierarchicalReport newReport;

    public FlatReportComparisonData(HierarchicalReport oldReport,
            HierarchicalReport newReport) {
        this.oldReport = oldReport;
        this.newReport = newReport;

    }

    public HierarchicalReport getOldReport() {
        return oldReport;
    }

    public HierarchicalReport getNewReport() {
        return newReport;
    }

}
