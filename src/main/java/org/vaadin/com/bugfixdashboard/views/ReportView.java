package org.vaadin.com.bugfixdashboard.views;

import java.util.Date;

import org.vaadin.com.bugfixdashboard.data.ReportSummary.ReportType;
import org.vaadin.com.bugfixdashboard.views.ReportPresenter.ReportViewDTO;

public interface ReportView extends View {

    void showReport(ReportViewDTO dto);

    void showEmptyReport(ReportType type);

    public void updateDateSpanSilently(Date start, Date end);

    void clearCurrentReports();

    void showFullScreen(ReportViewDTO reportViewDTO);

}
