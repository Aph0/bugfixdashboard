package org.vaadin.com.bugfixdashboard.views;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.data.ReportLevel;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;

public interface ReportView extends View {

    void showReport(ReportType type,
            Map<String, List<DateAndValue>> historyData,
            HierarchicalReport currentReportToShow,
            List<ReportLevel> pieChartData);

    void showEmptyReport(ReportType type);

    public void updateDateSpanSilently(Date start, Date end);

    void clearCurrentReports();

}
