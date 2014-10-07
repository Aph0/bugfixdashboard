package org.vaadin.com.bugfixdashboard.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.vaadin.com.bugfixdashboard.data.HierarchicalReport;
import org.vaadin.com.bugfixdashboard.data.ReportDay;
import org.vaadin.com.bugfixdashboard.data.ReportDay.DateAndValue;
import org.vaadin.com.bugfixdashboard.data.ReportLevel;
import org.vaadin.com.bugfixdashboard.data.ReportSummary;
import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
import org.vaadin.com.bugfixdashboard.util.DateUtil;

public class ReportPresenter {

    private ReportView view;
    private ReportSummary reportSummary;
    private final int defaultDaySpan;

    private Date currentHistoryRepresentationStartDate;

    private Date currentHistoryRepresentationEndDate;

    private Map<ReportType, ReportViewDTO> lastCreatedReportTypeData = new HashMap<ReportSummary.ReportType, ReportPresenter.ReportViewDTO>();

    public ReportPresenter(ReportView view, ReportSummary reportSummary,
            int visibleDaySpan) {
        this.view = view;
        this.reportSummary = reportSummary;
        defaultDaySpan = visibleDaySpan;

    }

    public void init() {
        currentHistoryRepresentationEndDate = DateUtil
                .clearDateBelowDays(new Date());
        Calendar c = Calendar.getInstance();
        c.setTime(currentHistoryRepresentationEndDate);
        c.add(Calendar.DATE, -defaultDaySpan);
        currentHistoryRepresentationStartDate = c.getTime();
        showAllReportsUsingCurrentDates();

    }

    private void showAllReportsUsingCurrentDates() {

        view.clearCurrentReports();
        lastCreatedReportTypeData.put(ReportType.REVIEW,
                showReport(ReportType.REVIEW, reportSummary, 2));
        lastCreatedReportTypeData.put(ReportType.BFP,
                showReport(ReportType.BFP, reportSummary, 2));
        lastCreatedReportTypeData.put(ReportType.SUPPORT,
                showReport(ReportType.SUPPORT, reportSummary, 2));
        lastCreatedReportTypeData.put(ReportType.TC_BUGFIX,
                showReport(ReportType.TC_BUGFIX, reportSummary, 2));
        lastCreatedReportTypeData.put(ReportType.SUPPORT_STATUS,
                showReport(ReportType.SUPPORT_STATUS, reportSummary, 1));

        view.updateDateSpanSilently(currentHistoryRepresentationStartDate,
                currentHistoryRepresentationEndDate);
    }

    private ReportViewDTO showReport(ReportType type, ReportSummary summary,
            int startLevel) {

        HierarchicalReport latestReport = getMostRecentReportFromSummary(type,
                summary);
        ReportViewDTO reportDTO = null;

        if (latestReport != null) {

            Map<String, List<DateAndValue>> historyData = getHistoryDataMapFrom(
                    type, summary, latestReport,
                    currentHistoryRepresentationStartDate,
                    currentHistoryRepresentationEndDate);

            List<ReportLevel> pieChartData = latestReport
                    .getAllReportLevelsAsList(startLevel);
            // We exclude first level and nodes that are not leafs.
            pieChartData = sortOutOtherThanLeafNodes(pieChartData);
            reportDTO = new ReportViewDTO(type, historyData, latestReport,
                    pieChartData);
            view.showReport(reportDTO);
        } else {
            // This is if NO report was found at all
            view.showEmptyReport(type);
        }

        return reportDTO;
    }

    public void requestFullScreenFor(ReportType type) {
        ReportViewDTO reportViewDTO = lastCreatedReportTypeData.get(type);
        if (reportViewDTO != null) {

            view.showFullScreen(reportViewDTO);

        }
    }

    private List<ReportLevel> sortOutOtherThanLeafNodes(List<ReportLevel> levels) {
        List<ReportLevel> result = new ArrayList<ReportLevel>();
        for (ReportLevel level : levels) {
            if (!level.hasChildren()) {
                result.add(level);
            }
        }
        return result;
    }

    private HierarchicalReport getMostRecentReportFromSummary(ReportType type,
            ReportSummary summary) {
        ReportDay latestReportDay = summary.getMostRecentReport(type);
        if (latestReportDay == null) {
            return null;
        }
        HierarchicalReport latestReport = latestReportDay.getReportByType(type);
        return latestReport;
    }

    private Map<String, List<DateAndValue>> getHistoryDataMapFrom(
            ReportType type, ReportSummary summary,
            HierarchicalReport forReport, Date start, Date end) {
        Map<String, List<DateAndValue>> historyData = new HashMap<String, List<DateAndValue>>();
        for (ReportLevel level : forReport.getAllReportLevelsAsList()) {
            List<DateAndValue> dAv = summary.getHistoryDataForReportLevel(type,
                    level, start, end);
            if (dAv.isEmpty()) {
                continue;
            }
            historyData.put(level.getName(), dAv);
        }
        return historyData;
    }

    public void dateSpanChanged(Date startDate, Date endDate) {
        currentHistoryRepresentationEndDate = endDate;
        currentHistoryRepresentationStartDate = startDate;
        if (currentHistoryRepresentationStartDate.getTime() > currentHistoryRepresentationEndDate
                .getTime()) {
            currentHistoryRepresentationStartDate = currentHistoryRepresentationEndDate;
            view.updateDateSpanSilently(currentHistoryRepresentationStartDate,
                    currentHistoryRepresentationEndDate);
        }

        showAllReportsUsingCurrentDates();
    }

    public static class ReportViewDTO {
        public final ReportType type;
        public final Map<String, List<DateAndValue>> historyData;
        public final HierarchicalReport currentReportToShow;
        public final List<ReportLevel> pieChartData;

        public ReportViewDTO(ReportType type,
                Map<String, List<DateAndValue>> historyData,
                HierarchicalReport currentReportToShow,
                List<ReportLevel> pieChartData) {
            this.type = type;
            this.historyData = historyData;
            this.currentReportToShow = currentReportToShow;
            this.pieChartData = pieChartData;

        }
    }
}
